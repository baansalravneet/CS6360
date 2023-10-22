import requests

url = 'http://127.0.0.1:8080/updateFines'
response = requests.post(url)
print(response.text)

# 0 0 * * * python3 update_fines.py