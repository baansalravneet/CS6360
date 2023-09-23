package com.librarysystem.db;

import com.librarysystem.config.DatabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Database {
    @Autowired
    public Database(DatabaseConfig config) {

    }
}
