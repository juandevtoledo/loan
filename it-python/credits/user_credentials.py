from utils import *
from utils_mutual_tls import request_ssl as r


def login(username, password, oauth_token):
    url = f'{api_endpoint()}/authentication-service/V2/login'

    payload = {
        'password': password,
        'username': username,
    }

    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + oauth_token,
    }

    response = r.post(url, data=payload, headers=headers)
    return response.json()
