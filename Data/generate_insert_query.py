import csv
import requests
import json
import time
import sys
import codecs

books_input = 'books.csv'
borrowers_input = 'borrowers.csv'

books_file = codecs.open('insert_books.sql', 'w', 'utf-8')
authors_file = codecs.open('insert_authors.sql', 'w', 'utf-8')
book_authors_file = codecs.open('insert_book_authors.sql', 'w', 'utf-8')
borrowers_file = codecs.open('insert_borrowers.sql', 'w', 'utf-8')

insert_query_books = '''INSERT INTO LIBRARY_DATABASE.BOOK VALUES ('%s', '%s', '%s', '%s', %d, 1);\n'''
insert_query_authors = '''REPLACE INTO LIBRARY_DATABASE.AUTHORS VALUES (%d, '%s');\n'''
insert_query_book_authors = '''INSERT INTO LIBRARY_DATABASE.BOOK_AUTHORS VALUES ('%s', %d);\n'''
insert_query_borrowers = '''INSERT INTO LIBRARY_DATABASE.BORROWER VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');\n'''

data = []
with open(books_input, mode='r', newline='\n') as file:
    reader = csv.DictReader(file, delimiter='\t')
    for row in reader:
        data.append(row)

borrowers_data = []
with open(borrowers_input, mode='r', newline='\n') as file:
    reader = csv.DictReader(file, delimiter=',')
    for row in reader:
        borrowers_data.append(row)

def get_books_query(row):
    return insert_query_books % (row['ISBN13'], row['Title'].replace("'", "\\'"), row['Cover'].replace("'", "\\'"), row['Publisher'].replace("'", "\\'"), int(row['Pages']))

def get_authors_query(count, name):
    return insert_query_authors % (count, name)

def get_book_authors_query(book_id, author_id):
    return insert_query_book_authors % (book_id, author_id)

count = 1
author_dict = {}
for row in data:
    books_file.write(get_books_query(row))
    authors = row['Authro'].split(",")
    for a in authors:
        if a == '' or a == '(None)':
            continue
        a = a.replace("'", "\\'").strip()
        author_dict[a] = count
        authors_file.write(get_authors_query(count, a))
        count = count + 1

for row in data:
    authors = row['Authro'].split(",")
    for a in authors:
        if a == '' or a == '(None)':
            continue
        a = a.replace("'", "\\'").strip()
        book_authors_file.write(get_book_authors_query(row['ISBN13'], author_dict[a]))

def get_borrowers_query(row):
    card_id = row['ID0000id']
    ssn = row['ssn']
    first_name = row['first_name']
    last_name = row['last_name']
    email = row['email']
    address = row['address']
    city = row['city']
    state = row['state']
    phone = row['phone']
    return insert_query_borrowers % (card_id, ssn, first_name+" "+last_name, email, address, city, state, phone)

for row in borrowers_data:
    borrowers_file.write(get_borrowers_query(row))


# with codecs.open(books_file, 'w', 'utf-8') as file:
#     values = []
#     count = 100
#     for row in data:
#         isbn = row['ISBN13']
#         title = row['Title'].replace("'", "\\'")
#         cover_url = row['Cover'].replace("'", "\\'")
#         publisher = row['Publisher'].replace("'", "\\'")
#         page = int(row['Pages'])
#         row_values = insert_query_books_values % (isbn, title, cover_url, publisher, page)
#         values.append(row_values)
#         count -= 1
#         if count == 0:
#             count = 100
#             query = insert_query_books % ",".join(values)
#             values = []
#             file.write(query + '\n')
#     if len(values) != 0:
#         query = insert_query_books % ",".join(values)
#         file.write(query + '\n')
#
# with codecs.open(authors_file, 'w', 'utf-8') as file:
#     values = []
#     count = 100
#     for row in data:
#         authors = row['Authro'].split(",")
#         for a in authors:
#             if a == '' or a == '(None)':
#                 continue
#             row_values = insert_query_authors_values % a.replace("'", "\\'")
#             values.append(row_values)
#             count -= 1
#             if count == 0:
#                 count = 100
#                 query = insert_query_authors % ",".join(values)
#                 values = []
#                 file.write(query + '\n')
#     if len(values) != 0:
#         query = insert_query_authors % ",".join(values)
#         file.write(query + '\n')
#
# with codecs.open(book_authors_file, 'w', 'utf-8') as file:
#     for row in data:
#         authors = row['Authro'].split(",")
#         author_ids = get_author_ids(authors)
#         for id in author_ids:
#             insert_book_authors(id, row['ISBN13'])