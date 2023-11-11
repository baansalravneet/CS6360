package com.librarysystem.services;

import com.librarysystem.db.dao.*;
import com.librarysystem.db.repositories.AuthorRepository;
import com.librarysystem.db.repositories.BookRepository;
import com.librarysystem.db.repositories.BorrowerRepository;
import com.librarysystem.db.repositories.LoanRepository;
import com.librarysystem.models.Author;
import com.librarysystem.models.Book;
import com.librarysystem.models.Borrower;
import com.librarysystem.models.FineSummary;
import com.librarysystem.models.Response;

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
    public Response checkout(List<String> selectedISBN, String borrowerId) {
        if (selectedISBN.isEmpty()) return new Response("ISBN not provided");
        // check if borrower exist
        Optional<StoredBorrower> storedBorrower = borrowerRepository.findById(borrowerId);
        if (storedBorrower.isEmpty()) return new Response("Borrower not in the system");
        List<StoredBook> books = selectedISBN.stream()
                .map(this::getBookByExactIsbn)
                .filter(Optional::isPresent)
                .filter(b -> b.get().isAvailable())
                .map(Optional::get)
                .toList();
        // check if the borrower can borrow the number of books requested
        if (storedBorrower.get().getLoans()
                .stream().filter(sl -> sl.getDateIn() == null).count() + books.size() > 3) {
                     return new Response("Each borrower can check out 3 books.");
                }
        // check if all the books requested are available or not
        if (books.size() != selectedISBN.size()) {
            return new Response("Selected book is not available");
        }
        // handle checkout
        handleCheckout(books, storedBorrower.get());
        return new Response();
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
        result.addAll(getLoansByMatchingBorrowerId(searchQuery));
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

    private List<StoredLoan> getLoansByMatchingBorrowerId(String searchQuery) {
        return loanRepository.getLoanByMatchingBorrowerId(searchQuery);
    }

    private List<StoredLoan> getLoansByMatchingIsbn(String searchQuery) {
        return loanRepository.getLoanByMatchingIsbn(searchQuery);
    }

    @Transactional
    public Response checkin(long loanId) {
        Optional<StoredLoan> loan = loanRepository.getLoanById(loanId)
                .filter(l -> l.getDateIn() == null);
        if (loan.isEmpty()) return new Response("No loans found!");
        loan.get().setDateIn(new Date(System.currentTimeMillis()));
        loanRepository.save(loan.get());
        return new Response();
    }

    @Transactional
    public Response registerBorrower(String ssn, String firstName, String lastName, String email, String address,
                                    String city, String state, String phone) {
        // TODO: return the card id that is generated.
        StoredBorrower sb = new StoredBorrower(generateCardId(), ssn, firstName + " " + lastName,
                email, address, state, city, phone);
        try {
            borrowerRepository.save(sb);
            return new Response();
        } catch (Exception e) {
            return new Response("Error Occured while saving");
        }
    }

    private static Borrower toBorrower(StoredBorrower sb) {
        return new Borrower(sb.getCardId(), sb.getSsn(), sb.getName(), sb.getAddress(), sb.getPhone());
    }

    private String generateCardId() {
        String prefix = "ID";
        int count = getBorrowerCount();
        return String.format("%s%06d", prefix, count + 1);
    }

    public Response updateFines() {
        try {
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
        } catch (Exception e) {
            return new Response("Error occured.");
        }
        return new Response();
    }

    public int getBorrowerCount() {
        return borrowerRepository.getCount();
    }

    public List<StoredLoan> getLoansByBorrowerId(String cardId) {
        return loanRepository.getLoansByBorrowerId(cardId);
    }

    @Transactional
    public Response handleFeePayment(long loanId) {
        Optional<StoredLoan> loan = loanRepository.getLoanById(loanId);
        loan = loan.filter(l -> l.getFine() != null)
                .filter(l -> l.getDateIn() != null)
                .filter(l -> !l.getFine().isPaid());
        if (loan.isEmpty()) return new Response("No loans found!");
        loan.get().getFine().setPaid(true);
        loanRepository.save(loan.get());
        return new Response();
    }

    public List<FineSummary> getFinesSummary() {
        return loanRepository.getFineSummaries();
    }
}