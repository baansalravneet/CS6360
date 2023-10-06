DROP DATABASE LIBRARY_DATABASE;
CREATE DATABASE LIBRARY_DATABASE;
USE LIBRARY_DATABASE;

CREATE TABLE `AUTHORS` (
  `Id`          bigint          NOT NULL    AUTO_INCREMENT,
  `Name`        VARCHAR(255)    DEFAULT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `name_key` (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `BOOKS` (
  `Isbn`        VARCHAR(255)    NOT NULL,
  `Title`       VARCHAR(255)    DEFAULT NULL,
  `Cover_url`   VARCHAR(255)    DEFAULT NULL,
  `Publisher`   VARCHAR(255)    DEFAULT NULL,
  `Pages`       INT             DEFAULT 0,
  `Available`   BIT             DEFAULT 1,
  PRIMARY KEY (`Isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `BOOK_AUTHORS` (
  `Book_id`     VARCHAR(255)    NOT NULL,
  `Author_id`   BIGINT          NOT NULL,
  PRIMARY KEY (`Book_id`,`Author_id`),
  KEY `author_id_key` (`Author_id`),
  CONSTRAINT `author_foreign_key` FOREIGN KEY (`Author_id`) REFERENCES `AUTHORS` (`Id`),
  CONSTRAINT `book_foreign_key` FOREIGN KEY (`Book_id`) REFERENCES `BOOKS` (`Isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `BORROWERS` (
  `Card_id`     VARCHAR(255)    NOT NULL,
  `Ssn`         VARCHAR(255)    NOT NULL,
  `Fname`       VARCHAR(255)    DEFAULT NULL,
  `Lname`       VARCHAR(255)    DEFAULT NULL,
  `Email`       VARCHAR(255)    DEFAULT NULL,
  `Address`     VARCHAR(255)    DEFAULT NULL,
  `City`        VARCHAR(255)    DEFAULT NULL,
  `State`       VARCHAR(255)    DEFAULT NULL,
  `Phone`       VARCHAR(255)    DEFAULT NULL,
  PRIMARY KEY (`Card_id`),
  UNIQUE KEY `ssn_key` (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `LOANS` (
  `Id`          bigint          NOT NULL    AUTO_INCREMENT,
  `Isbn`        varchar(255)    NOT NULL,
  `Card_id`     varchar(255)    NOT NULL,
  `Date_out`    TIMESTAMP       NOT NULL,
  `Due_date`    TIMESTAMP       NOT NULL,
  `Date_in`     TIMESTAMP,
  PRIMARY KEY (`Id`),
  CONSTRAINT `borrower_foreign_key` FOREIGN KEY (`Card_id`) REFERENCES `BORROWERS` (`Card_id`),
  CONSTRAINT `loan_book_foreign_key` FOREIGN KEY (`Isbn`) REFERENCES `BOOKS` (`Isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `FINES` (
  `Loan_id`     bigint          NOT NULL,
  `Fine_amt`    DECIMAL(10,2)   NOT NULL,
  `Paid`        BIT             DEFAULT 0,
  CONSTRAINT `loan_foreign_key` FOREIGN KEY (`Loan_id`) REFERENCES `LOANS` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;