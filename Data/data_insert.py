import csv
import requests

input_file = 'books.csv'
input_file2 = 'borrowers.csv'

books_request = '''{
    "isbn": "%s",
    "title": "%s",
    "coverUrl": "%s",
    "authors": %s,
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
        break

def get_authors(authors):
    return ('%s' % authors.split(',')).replace("'", "\"")

for row in data:
    authors = get_authors(row['Authro'])
    request = books_request % (row['ISBN13'],row['Title'],row['Cover'],authors)
    url = 'http://127.0.0.1:8181/addBook'
    response = requests.post(url, json=request)
    print(response.text)