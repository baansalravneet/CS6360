package com.librarysystem.db.repositories;

import com.librarysystem.db.dao.StoredAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<StoredAuthor, String> {
    Optional<StoredAuthor> getAuthorByName(String name);

    @Query(value = "SELECT * FROM AUTHORS WHERE LOWER(Name) LIKE %:name% LIMIT 100", nativeQuery = true)
    List<StoredAuthor> getAuthorsMatchingName(@Param("name") String name);
}
