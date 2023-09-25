package com.librarysystem.db.repositories;

import com.librarysystem.db.dao.StoredBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowerRepository extends JpaRepository<StoredBook, Long> {
}
