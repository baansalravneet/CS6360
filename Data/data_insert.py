import csv
import requests
import json
import time

input_file = 'books.csv'
input_file2 = 'borrowers.csv'

books_request = '''{
    "isbn": "%s",
    "title": "%s",
    "coverUrl": "%s",
    "authors": [%s],
    "available": true
}'''

authors_request = '''{
    "name": "%s"
}'''

data = []
with open(input_file, mode='r', newline='\n') as file:
    reader = csv.DictReader(file, delimiter='\t')
    for row in reader:
        data.append(row)

def get_authors(authors):
    author_list = authors.split(',')
    request_list = []
    request_list_string = ""
    for author in author_list:
        request_list_string = request_list_string + authors_request % author
        request_list_string = request_list_string + ","
    return request_list_string[:-1]

for row in data:
    authors = get_authors(row['Authro'])
    request = books_request % (row['ISBN13'],row['Title'],row['Cover'],authors)
    url = 'http://127.0.0.1:8080/addBook'
    response = requests.post(url, json=json.loads(request))
    time.sleep(0.5)