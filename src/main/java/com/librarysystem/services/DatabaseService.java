package com.librarysystem.services;

import com.librarysystem.db.dao.*;
import com.librarysystem.db.repositories.AuthorRepository;
import com.librarysystem.db.repositories.BookRepository;
import com.librarysystem.db.repositories.BorrowerRepository;
import com.librarysystem.db.repositories.LoanRepository;
import com.librarysystem.models.Author;
import com.librarysystem.models.Book;
import com.librarysystem.models.Borrower;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DatabaseService {

    private static final long DAY_IN_MILLIS = 8_64_00_000L;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BorrowerRepository borrowerRepository;
    @Autowired
    private LoanRepository loanRepository;

    Optional<Book> getBookById(String isbn) {
        Optional<StoredBook> savedBook = bookRepository.findById(isbn);
        return savedBook.map(DatabaseService::toBook);
    }

    // TODO: populate this with borrowers
    static Book toBook(StoredBook sb) {
        List<Borrower> borrowers = sb.getLoans().stream()
                .map(StoredLoan::getBorrower).distinct().map(DatabaseService::toBorrower).toList();
        return new Book(sb.getIsbn(), sb.getTitle(), sb.getCoverUrl(), sb.getPublisher(), sb.getPages(),
                toAuthors(sb.getAuthors()), sb.isAvailable(), borrowers);
    }

    private static List<Author> toAuthors(List<StoredAuthor> storedAuthors) {
        return storedAuthors.stream().map(DatabaseService::toAuthor).collect(Collectors.toList());
    }

    @Transactional
    Book saveBook(Book book) {
        StoredBook storedBook = DatabaseService.toStoredBook(book);
        List<StoredAuthor> savedAuthors = getOrCreateAuthors(book.getAuthors());
        storedBook.setAuthors(savedAuthors);
        savedAuthors.forEach(sa -> {
            checkAndAddBook(sa, storedBook);
            authorRepository.save(sa);
        });
        return DatabaseService.toBook(bookRepository.save(storedBook));
    }

    @Transactional
    Book saveBook(StoredBook sb) {
        return DatabaseService.toBook(bookRepository.save(sb));
    }

    private static void checkAndAddBook(StoredAuthor sa, StoredBook sb) {
        if (sa.getBooks().stream().noneMatch(book -> book.getIsbn().equals(sb.getIsbn()))) {
            sa.getBooks().add(sb);
        }
    }

    private static StoredBook toStoredBook(Book book) {
        return new StoredBook(book.getIsbn(), book.getTitle(),
                book.getCoverUrl(), book.getPublisher(), book.getPages(),
                null, null, book.isAvailable());
    }

    private List<StoredAuthor> getOrCreateAuthors(List<Author> authors) {
        return authors.stream().map(a -> getOrCreateAuthor(a.getName()))
                .collect(Collectors.toList());
    }

    private StoredAuthor getOrCreateAuthor(String name) {
        Optional<StoredAuthor> savedAuthor = authorRepository.getAuthorByName(name);
        return savedAuthor.orElseGet(() -> new StoredAuthor(null, name, null));
    }

    private static Author toAuthor(StoredAuthor sa) {
        return new Author(sa.getId(), sa.getName());
    }

    public List<Book> getBookByIsbn(String searchQuery) {
        List<StoredBook> matchingBooks = bookRepository.getBooksMatchingId(searchQuery);
        return matchingBooks.stream().map(DatabaseService::toBook).collect(Collectors.toList());
    }

    public Optional<StoredBook> getBookByExactIsbn(String isbn) {
        return bookRepository.getBookByIsbn(isbn);
    }

    public List<Book> getBooksByTitle(String searchQuery) {
        List<StoredBook> matchingBooks = bookRepository.getBooksMatchingTitle(searchQuery);
        return matchingBooks.stream().map(DatabaseService::toBook).collect(Collectors.toList());
    }

    public List<Book> getBooksByAuthors(String searchQuery) {
        List<StoredAuthor> matchingAuthors = authorRepository.getAuthorsMatchingName(searchQuery);
        return matchingAuthors.stream().map(StoredAuthor::getBooks).flatMap(Collection::stream)
                .map(DatabaseService::toBook).collect(Collectors.toList());
    }

    public Book addBook(final Book book) {
        Optional<Book> savedBook = getBookById(book.getIsbn());
        return savedBook.orElseGet(() -> saveBook(book));
    }

    public boolean addBooks(final List<Book> books) {
        books.forEach(this::addBook);
        return true;
    }

    // TODO: Put these in a single query.
    public List<Book> getBooksForSearchQuery(String searchQuery) {
        List<Book> result = new ArrayList<>();
        result.addAll(getBooksByTitle(searchQuery));
        result.addAll(getBooksByAuthors(searchQuery));
        result.addAll(getBookByIsbn(searchQuery));
        return result.stream().distinct().collect(Collectors.toList());
    }

    // TODO: batch update?
    // TODO: return proper errors to show on the GUI
    // TODO: Try to move these checks at the DB level
    public boolean checkout(List<String> selectedISBN, String borrowerId) {
        if (selectedISBN.isEmpty()) return false;
        // check if borrower exist
        Optional<StoredBorrower> storedBorrower = borrowerRepository.findById(borrowerId);
        if (storedBorrower.isEmpty()) return false;
        List<StoredBook> books = selectedISBN.stream()
                .map(this::getBookByExactIsbn)
                .filter(Optional::isPresent)
                .filter(b -> b.get().isAvailable())
                .map(Optional::get)
                .toList();
        // check if the borrower can borrow the number of books requested
        if (storedBorrower.get().getLoans()
                .stream().filter(sl -> sl.getDateIn() == null).count() + books.size() > 3) return false;
        // check if all the books requested are available or not
        if (books.size() != selectedISBN.size()) {
            return false;
        }
        // handle checkout
        handleCheckout(books, storedBorrower.get());
        return true;
    }

    // TODO: find a way to set the system time.
    @Transactional
    private void handleCheckout(List<StoredBook> books, StoredBorrower borrower) {
        Date dateOut = new Date(System.currentTimeMillis());
        Date dueDate = new Date(dateOut.getTime() + 14 * DAY_IN_MILLIS);
        books.forEach(b -> {
            StoredLoan newLoan = new StoredLoan(null, b, borrower, dateOut, dueDate, null, null);
            b.setAvailable(false);
            b.getLoans().add(new StoredLoan(null, b, borrower, dateOut, dueDate, null, null));
            borrower.getLoans().add(newLoan);
            saveBook(b);
        });
    }

    public List<StoredLoan> getBookLoansForSearchQuery(String searchQuery) {
        List<StoredLoan> result = new ArrayList<>();
        result.addAll(getLoansByMatchingIsbn(searchQuery));
        result.addAll(getLoansByBorrowerId(searchQuery));
        result.addAll(getLoansByBorrowerName(searchQuery));
        return result;
    }

    private List<StoredLoan> getLoansByBorrowerName(String searchQuery) {
        return borrowerRepository.getLoanByMatchingBorrowerName(searchQuery)
                .stream()
                .map(StoredBorrower::getLoans)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<StoredLoan> getLoansByBorrowerId(String searchQuery) {
        return loanRepository.getLoanByMatchingBorrowerId(searchQuery);
    }

    private List<StoredLoan> getLoansByMatchingIsbn(String searchQuery) {
        return loanRepository.getLoanByMatchingIsbn(searchQuery);
    }

    @Transactional
    public boolean checkin(String isbn, String borrowerId) {
        List<StoredLoan> loans = loanRepository.getLoanByMatchingIsbn(isbn);
        Optional<StoredLoan> loan = loans.stream()
                .filter(l -> l.getBook().getIsbn().equals(isbn))
                .filter(l -> l.getDateIn() == null)
                .filter(l -> l.getBorrower().getCardId().equals(borrowerId))
                .peek(l -> l.getBook().setAvailable(true))
                .findFirst();
        if (loan.isEmpty()) return false;
        loan.get().setDateIn(new Date(System.currentTimeMillis()));
        loanRepository.save(loan.get());
        return true;
    }

    @Transactional
    public boolean registerBorrower(String ssn, String firstName, String lastName, String email, String address,
                                    String city, String state, String phone) {
        // TODO: figure out a way to generate card id.
        // TODO: return the card id that is generated.
        StoredBorrower sb = new StoredBorrower(generateCardId(), ssn, firstName,
                lastName, email, address, state, city, phone);
        try {
            borrowerRepository.save(sb);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Borrower toBorrower(StoredBorrower sb) {
        return new Borrower(sb.getCardId(), sb.getSsn(), sb.getFirstName() + " " + sb.getLastName(),
                sb.getAddress(), sb.getPhone());
    }

    private String generateCardId() {
        String prefix = "ID";
        int count = getBorrowerCount();
        return String.format("%s%06d", prefix, count + 1);
    }

    public boolean updateFines() {
        List<StoredLoan> overDueLoans = loanRepository.getOverdueLoans();
        overDueLoans.stream()
                .peek(loan -> {
                    if (loan.getFine() == null) {
                        StoredFine fine = new StoredFine(loan, null, false);
                        loan.setFine(fine);
                    }
                })
                .filter(loan -> !loan.getFine().isPaid())
                .peek(loan -> loan.getFine().updateFine(loan.getDateIn(), loan.getDueDate()))
                .forEach(loan -> loanRepository.save(loan));
        return true;
    }

    public int getBorrowerCount() {
        return borrowerRepository.getCount();
    }
}