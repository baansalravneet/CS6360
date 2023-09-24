package com.librarysystem.db.repositories;

import com.librarysystem.db.dao.StoredAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<StoredAuthor, Long> {
}
