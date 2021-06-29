import os
import random
import simplejson as json


def random_phone():
    return random.randint(3_033_700_000, 3_213_799_999)


def random_document():
    return random.randint(40_000_560, 44_400_999)


def random_email():
    return f'python-it{random.randint(0, 50000)}@yopmail.com'


def api_endpoint(env='sandbox'):
    if os.environ.get('LOCALHOST_TEST', None):
        return 'http://localhost:8084'
    else:
        return f'https://api-internal-{env}-mtls.lulobank.com/stage'


def fmt(o):
    return json.dumps(o)


def auth_headers(access_token):
    authorization = f'Bearer {access_token}' if os.environ.get('LOCALHOST_TEST', None) else access_token
    headers = {
        'Content-Type': 'application/json',
        'Authorization': authorization
    }
    return headers
