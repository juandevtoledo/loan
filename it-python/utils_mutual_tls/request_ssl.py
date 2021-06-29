import json
import requests_pkcs12 as pkcs12
from utils import fmt


filename = 'utils_mutual_tls/api_lulobank_com.p12'
pwd = 'g&9wL*LYt0j4O'


def post(url, headers, data=None):
    print('***** HTTP POST *****')
    print(f'url: {url}')
    print(f'headers: {fmt(headers)}')

    if data is None:
        data = {}
    else:
        print(f'data: {fmt(data)}')
    print('***** HTTP POST *****')

    return pkcs12.post(url,
                       data=json.dumps(data),
                       headers=headers,
                       pkcs12_filename=filename,
                       pkcs12_password=pwd)


def get(url, headers):
    print('***** HTTP GET *****')
    print(f'url: {url}')
    print(f'headers: {fmt(headers)}')
    print('***** HTTP GET *****')

    return pkcs12.get(url,
                      headers=headers,
                      pkcs12_filename=filename,
                      pkcs12_password=pwd)
