package com.librarysystem.db.repositories;

import com.librarysystem.db.dao.StoredLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<StoredLoan, Long> {
    @Query(value = "SELECT * FROM LOANS WHERE LOWER(Isbn) LIKE %:isbn%", nativeQuery = true)
    List<StoredLoan> getLoanByMatchingIsbn(@Param("isbn") String isbn);

    @Query(value = "SELECT * FROM LOANS WHERE LOWER(Card_id) LIKE %:borrowerId%", nativeQuery = true)
    List<StoredLoan> getLoanByMatchingBorrowerId(@Param("borrowerId") String borrowerId);

    @Query(value = "SELECT * FROM LOANS " +
            "WHERE Due_date < Date_in " +
            "OR (Date_in IS NOT NULL AND CURRENT_DATE() > Due_date)", nativeQuery = true)
    List<StoredLoan> getOverdueLoans();

    @Query(value = "SELECT * FROM LOANS WHERE Id = :loanId", nativeQuery = true)
    Optional<StoredLoan> getLoanById(@Param("loanId") long loanId);

    @Query(value = "SELECT * FROM LOANS WHERE Card_id = :cardId", nativeQuery = true)
    List<StoredLoan> getLoansByBorrowerId(@Param("cardId") String cardId);
}
