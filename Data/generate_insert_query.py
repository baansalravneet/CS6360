import csv
import requests
import json
import time
import sys
import codecs

books_input = 'books.csv'
borrowers_input = 'borrowers.csv'

books_file = 'insert_books.sql'
authors_file = 'insert_authors.sql'

insert_query_books = '''INSERT INTO LIBRARY_DATABASE.BOOKS VALUES %s;'''
insert_query_books_values = '''('%s', '%s', '%s', '%s', %d, 1)'''

insert_query_authors = '''INSERT IGNORE INTO LIBRARY_DATABASE.AUTHORS (Name) VALUES %s;'''
insert_query_authors_values = '''('%s')'''

data = []
with open(books_input, mode='r', newline='\n') as file:
    reader = csv.DictReader(file, delimiter='\t')
    for row in reader:
        data.append(row)

with codecs.open(books_file, 'w', 'utf-8') as file:
    values = []
    count = 100
    for row in data:
        isbn = row['ISBN13']
        title = row['Title'].replace("'", "\\'")
        cover_url = row['Cover'].replace("'", "\\'")
        publisher = row['Publisher'].replace("'", "\\'")
        page = int(row['Pages'])
        row_values = insert_query_books_values % (isbn, title, cover_url, publisher, page)
        values.append(row_values)
        count -= 1
        if count == 0:
            count = 100
            query = insert_query_books % ",".join(values)
            values = []
            file.write(query + '\n')
    if len(values) != 0:
        query = insert_query_books % ",".join(values)
        file.write(query + '\n')

with codecs.open(authors_file, 'w', 'utf-8') as file:
    values = []
    count = 100
    for row in data:
        authors = row['Authro'].split(",")
        for a in authors:
            row_values = insert_query_authors_values % a.replace("'", "\\'")
            values.append(row_values)
            count -= 1
            if count == 0:
                count = 100
                query = insert_query_authors % ",".join(values)
                values = []
                file.write(query + '\n')
    if len(values) != 0:
        query = insert_query_authors % ",".join(values)
        file.write(query + '\n')