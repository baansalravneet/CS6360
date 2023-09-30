package com.librarysystem;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LibrarySystemApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LibrarySystemApplication.class)
                .headless(false)
                .run(args);
    }
}
