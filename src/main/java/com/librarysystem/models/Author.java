package com.librarysystem.models;

public class Author {
    private final Long authorId;
    private final String name;

    public Author(Long authorId, String name) {
        this.authorId = authorId;
        this.name = name;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getName() {
        return name;
    }
}
