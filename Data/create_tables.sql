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
  `Available`   TINYINT         DEFAULT 1,
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
  `Address`     VARCHAR(255)    DEFAULT NULL,
  `Name`        VARCHAR(255)    DEFAULT NULL,
  `Phone`       VARCHAR(255)    DEFAULT NULL,
  `Ssn`         VARCHAR(255)    DEFAULT NULL,
  PRIMARY KEY (`card_id`),
  UNIQUE KEY `ssn_key` (`ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;