package com.librarysystem.models;

public class Author {
    private final String authorID;
    private final String name;

    public Author(String authorID, String name) {
        this.authorID = authorID;
        this.name = name;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getName() {
        return name;
    }
}
