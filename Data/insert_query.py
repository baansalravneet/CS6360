import csv
import requests
import json
import time
import sys
import codecs

input_file = 'books.csv'
input_file2 = 'borrowers.csv'

output_file = 'insert_books.sql'

insert_query_books = '''INSERT INTO LIBRARY_DATABASE.BOOKS VALUES %s;'''
insert_query_books_values = '''('%s', '%s', '%s', '%s', %d, 1)'''

data = []
with open(input_file, mode='r', newline='\n') as file:
    reader = csv.DictReader(file, delimiter='\t')
    for row in reader:
        data.append(row)

with codecs.open(output_file, 'w', 'utf-8') as file:
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