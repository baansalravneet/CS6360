package com.librarysystem.db.repositories;

import com.librarysystem.db.dao.StoredLoan;
import com.librarysystem.models.FineSummary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<StoredLoan, Long> {

    @Query(value = "SELECT * FROM BOOK_LOANS WHERE LOWER(Isbn) LIKE %:isbn%", nativeQuery = true)
    List<StoredLoan> getLoanByMatchingIsbn(@Param("isbn") String isbn);

    @Query(value = "SELECT * FROM BOOK_LOANS WHERE LOWER(Card_id) LIKE %:borrowerId%", nativeQuery = true)
    List<StoredLoan> getLoanByMatchingBorrowerId(@Param("borrowerId") String borrowerId);

    @Query(value = "SELECT * FROM BOOK_LOANS " +
            "WHERE (Date_in IS NULL AND CURRENT_DATE() > Due_date) "+
            "OR Due_date < Date_in", nativeQuery = true)
    List<StoredLoan> getOverdueLoans();

    @Query(value = "SELECT * FROM BOOK_LOANS WHERE Loan_id = :loanId", nativeQuery = true)
    Optional<StoredLoan> getLoanById(@Param("loanId") long loanId);

    @Query(value = "SELECT * FROM BOOK_LOANS WHERE Card_id = :cardId", nativeQuery = true)
    List<StoredLoan> getLoansByBorrowerId(@Param("cardId") String cardId);

    @Query(value = "SELECT " +
            "l.Card_id cardId, f.Paid paid, SUM(f.Fine_amt) amount " +
            "FROM BOOK_LOANS AS l JOIN FINES AS f " +
            "ON l.Loan_id = f.Loan_id " +
            "GROUP BY l.Card_id, f.Paid", nativeQuery = true)
    List<FineSummary> getFineSummaries();
}
