CREATE TABLE `authors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_c;

CREATE TABLE `authors_books` (
  `book_id` bigint NOT NULL,
  `author_id` varchar(255) NOT NULL,
  PRIMARY KEY (`book_id`,`author_id`),
  KEY `FKbgwbrni148xshcinvt8i2atul` (`author_id`),
  CONSTRAINT `FKbgwbrni148xshcinvt8i2atul` FOREIGN KEY (`author_id`) REFERENCES `books` (`isbn`),
  CONSTRAINT `FKkdh8wp946jsesklc1f5ty9ldb` FOREIGN KEY (`book_id`) REFERENCES `authors` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `books` (
  `isbn` varchar(255) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `borrowers` (
  `card_id` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `ssn` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`card_id`),
  UNIQUE KEY `UK_lby8e16yevnl9etvq6jlougth` (`ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;