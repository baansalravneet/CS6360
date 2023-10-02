package com.librarysystem.db.repositories;

import com.librarysystem.db.dao.StoredBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<StoredBook, String> {
    @Query(value = "SELECT * FROM BOOKS WHERE LOWER(Isbn) LIKE %:bookId% LIMIT 100", nativeQuery = true)
    List<StoredBook> getBooksMatchingId(@Param("bookId") String bookId);
}
