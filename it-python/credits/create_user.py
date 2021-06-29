from utils import *
from utils_mutual_tls import request_ssl as r


class User:
    def __init__(self, email, pwd, phone):
        self.email = email
        self.pwd = pwd
        self.phone = phone


def execute(oauth_token):
    url = f'{api_endpoint()}/clients/V2/onboarding/initialClient'
    headers = {
        'Content-Type': "application/json",
        'Authorization': 'Bearer ' + oauth_token,
        'firebase-id': 'c-kFINosRvm05yxHX01KZe'
    }
    email = random_email()
    phone = random_phone()
    pwd = '135790'
    user = {
        "documentAcceptancesTimestamp": "1615304880",
        "emailCreateClientRequest": {"address": email, "verified": True},
        "password": pwd,
        "phoneCreateInitialClient": {"number": phone, "prefix": 57, "verified": True},
        "selectedProduct": "SAVING_ACCOUNT"
    }

    response = r.post(url, data=user, headers=headers)
    print(f'create user {response}')
    return User(email, pwd, phone)
