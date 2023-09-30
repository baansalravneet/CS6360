package com.librarysystem.db.repositories;

import com.librarysystem.db.dao.StoredAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<StoredAuthor, String> {
    Optional<StoredAuthor> getAuthorByName(String name);
}
