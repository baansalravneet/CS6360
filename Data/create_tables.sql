DROP DATABASE LIBRARY_DATABASE;
CREATE DATABASE LIBRARY_DATABASE;
USE LIBRARY_DATABASE;

CREATE TABLE `AUTHORS` (
  `Author_id`   BIGINT          NOT NULL    AUTO_INCREMENT,
  `Name`        VARCHAR(255)    DEFAULT NULL,
  PRIMARY KEY (`Author_id`),
  UNIQUE KEY `name_key` (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `BOOK` (
  `Isbn`        VARCHAR(255)    NOT NULL,
  `Title`       VARCHAR(255)    DEFAULT NULL,
  `Cover_url`   VARCHAR(255)    DEFAULT NULL,
  `Publisher`   VARCHAR(255)    DEFAULT NULL,
  `Pages`       INT             DEFAULT 0,
  `Available`   BIT             DEFAULT 1,
  PRIMARY KEY (`Isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `BOOK_AUTHORS` (
  `Isbn`        VARCHAR(255)    NOT NULL,
  `Author_id`   BIGINT          NOT NULL,
  PRIMARY KEY (`Isbn`,`Author_id`),
  KEY `author_id_key` (`Author_id`),
  CONSTRAINT `author_foreign_key` FOREIGN KEY (`Author_id`) REFERENCES `AUTHORS` (`Author_id`),
  CONSTRAINT `book_foreign_key` FOREIGN KEY (`Isbn`) REFERENCES `BOOK` (`Isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `BORROWER` (
  `Card_id`     VARCHAR(255)    NOT NULL,
  `Ssn`         VARCHAR(255)    NOT NULL,
  `Bname`       VARCHAR(255)    NOT NULL,
  `Email`       VARCHAR(255)    NOT NULL,
  `Address`     VARCHAR(255)    NOT NULL,
  `City`        VARCHAR(255)    NOT NULL,
  `State`       VARCHAR(255)    NOT NULL,
  `Phone`       VARCHAR(255)    NOT NULL,
  PRIMARY KEY (`Card_id`),
  UNIQUE KEY `ssn_key` (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `BOOK_LOANS` (
  `Loan_id`     BIGINT          NOT NULL    AUTO_INCREMENT,
  `Isbn`        varchar(255)    NOT NULL,
  `Card_id`     varchar(255)    NOT NULL,
  `Date_out`    DATE            NOT NULL,
  `Due_date`    DATE            NOT NULL,
  `Date_in`     DATE,
  PRIMARY KEY (`Loan_id`),
  CONSTRAINT `borrower_foreign_key` FOREIGN KEY (`Card_id`) REFERENCES `BORROWER` (`Card_id`),
  CONSTRAINT `loan_book_foreign_key` FOREIGN KEY (`Isbn`) REFERENCES `BOOK` (`Isbn`),
  CHECK (`Due_date` > `Date_out`),
  CHECK (`Date_in` >= `Date_out`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `FINES` (
  `Loan_id`     BIGINT          NOT NULL,
  `Fine_amt`    DECIMAL(10,2)   NOT NULL,
  `Paid`        BIT             DEFAULT 0,
  CONSTRAINT `loan_foreign_key` FOREIGN KEY (`Loan_id`) REFERENCES `BOOK_LOANS` (`Loan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;