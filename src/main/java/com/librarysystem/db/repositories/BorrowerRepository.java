package com.librarysystem.db.repositories;

import com.librarysystem.db.dao.StoredBorrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowerRepository extends JpaRepository<StoredBorrower, String> {
    @Query(value = "SELECT * FROM BORROWER WHERE LOWER(Bname) LIKE %:name%", nativeQuery = true)
    List<StoredBorrower> getLoanByMatchingBorrowerName(@Param("name") String name);

    @Query(value = "SELECT COUNT(*) FROM BORROWER", nativeQuery = true)
    int getCount();
}
