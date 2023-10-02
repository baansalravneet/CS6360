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

def add_books(request):
    url = 'http://127.0.0.1:8080/addBooks'
    request = "[%s]" % request
    response = requests.post(url, json=json.loads(request))
    if (response.text != "true"):
        print(request)
        return False
    else:
        print("Done")

request_list = []
count = 10
for row in data:
    authors = get_authors(row['Authro'])
    request = books_request % (row['ISBN13'],row['Title'],row['Cover'],authors)
    request_list.append(request)
    count -= 1
    if (count == 0):
        count = 10
        done = add_books(",".join(request_list))
        if done == False:
            break
        request_list = []
add_books(",".join(request_list))
#     url = 'http://127.0.0.1:8080/addBook'
#     response = requests.post(url, json=json.loads(request))
#     break