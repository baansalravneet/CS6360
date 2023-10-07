package com.librarysystem.db.repositories;

import com.librarysystem.db.dao.StoredBorrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowerRepository extends JpaRepository<StoredBorrower, String> {
}
