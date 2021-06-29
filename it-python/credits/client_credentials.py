from utils import *
from utils_mutual_tls import request_ssl as r


def oauth_token():
    url = f'{api_endpoint()}/oauth'

    headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': 'Basic N3F0MXZuMmlqdTZ2c2prZzExYnZpOXRkYms6M3NjdHJwNzM0am'
                         'VmbnAycjFqdjJjOXFjbzBtdXY4dXFkc2Yxc3ZhbzE5aTljMGM1anRk',
        'Accept': "application/json",
        'Host': "api-internal-sandbox-mtls.lulobank.com",
    }

    response = r.post(url, headers=headers)

    return response.json()['access_token']
