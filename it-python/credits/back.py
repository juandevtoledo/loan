from aws_dynamo import db
from utils import *
from utils_mutual_tls import request_ssl as r


def get_offer(id_client, token):
    url = f'{api_endpoint()}/credits/products/v2/offer/client/{id_client}'
    return r.get(url, auth_headers(token))


def create_savings_account(id_client, document_id, email, phone, token):
    url = f'{api_endpoint()}/savingsaccounts/v3/client/{id_client}/create'
    payload = {
        'clientInformation': {
            'documentId': {
                'id': document_id,
                'type': 'CC',
                'issueDate': '1993-06-13',
                "expirationDate": "1993-06-13"
            },
            'name': 'PythonN',
            'middleName': 'PythonMd',
            'lastName': 'PythonLM',
            'secondSurname': 'PythonSS',
            'email': email,
            'gender': 'M',
            'phone': {
                'prefix': 57,
                'number': phone
            }

        },
        'simpleDeposit': False
    }
    return r.post(url, auth_headers(token), payload)


def create_offer(id_client, document_id, email, phone, token):
    url = f'{api_endpoint()}/credits/products/v2/loan/client/{id_client}/initial-offer'
    payload = {
        'clientLoanRequestedAmount': 35700000,
        'riskEngineAnalysis': {
            'amount': 12325000,
            'interestRate': 16.5,
            'maxAmountInstallment': 1115000,
            'type': 'dummy'
        },
        'clientInformation': {
            'documentId': {
                'id': document_id,
                'type': 'CC',
                'issueDate': '1993-06-13'
            },
            'name': 'ALAN',
            'lastName': 'HARPER R',
            'gender': 'M',
            'email': email,
            'phone': {
                'number': phone,
                'prefix': '57'
            }
        }
    }

    response = r.post(url, data=payload, headers=auth_headers(token))

    return response


def payment_plan(id_client, token):
    url = f'{api_endpoint()}/credits/api/v1/client/{id_client}/payment-plan'
    data = {
        'idCredit': '20c470b7-f333-4805-916a-97314f8e1047',
        'idOffer': '494f19b2-cb0c-40ea-a6e9-19cdf9dbf5d1',
        'installments': '48',
        'dayOfPay': '15'
    }
    return r.post(url, auth_headers(token), data)


def get_loan(id_client, token):
    url = f'{api_endpoint()}/credits/loan/client/{id_client}'
    return r.get(url, auth_headers(token))


def waiting_list(id_client, id_product_offer, token):
    url = f'{api_endpoint()}/credits/api/v3/client/{id_client}/credit-waiting-list'
    payload = {
        'idProductOffer': id_product_offer
    }

    response = r.post(url, data=payload, headers=auth_headers(token))

    return response


def flexible_loan(item):
    return item['type'] == 'FLEXIBLE_LOAN'


def identity_biometric(id_client, id_transaction_biometric, token):
    url = f'{api_endpoint()}/clients/api/v3/client/{id_client}/identity/biometric'
    payload = {
        'idTransactionBiometric': id_transaction_biometric
    }
    response = r.post(url, data=payload, headers=auth_headers(token))
    return response


def accept_offer(id_client, id_credit, id_product_offer, credits_table, token):
    item = db.query_credit_key(id_credit, credits_table)
    offer_entities = item['initialOffer']['offerEntities']
    offer = list(filter(flexible_loan, offer_entities))[0]
    print(f'offer: {fmt(offer)} \n')

    url = f'{api_endpoint()}/credits/api/v1/loan/client/{id_client}/offer/accept'

    payload = {
        'idCredit': id_credit,
        'automaticDebitPayments': True,
        'idProductOffer': id_product_offer,
        'installment': 12,
        'idOffer': offer['idOffer'],
        'confirmationLoanOTP': '1111',
        'dayOfPay': 10
    }

    response = r.post(url, data=payload, headers=auth_headers(token))

    return response


def random_accepted(approved):
    return random.randint(approved, 40_000_999)


def pre_approved_offer(id_client, approved, token):
    url = f'{api_endpoint()}/credits/api/v1/loan/pre-approved/client/{id_client}/offer'

    payload = {
        'clientLoanRequestedAmount': random_accepted(approved)
    }
    return r.post(url, data=payload, headers=auth_headers(token))


def create_card(id_client, token):
    url = f'{api_endpoint()}/cards/debit'

    payload = {
        "cardHolder": {
            "address": "32f # 12",
            "addressAdditionalInfo": "Python",
            "addressPrefix": "Lisp Hackers",
            "code": "AC",
            "city": "Facatativa (Cundinamarca)",
            "cityId": "462",
            "department": "Cundinamarca",
            "departmentId": "25",
            "zipCode": "11002"
        },
        "idClient": id_client,
        "color": "BLUE",
        "nameOnCard": "LOGOS"
    }
    return r.post(url, data=payload, headers=auth_headers(token))


def address_client(id_client, token):
    url = f'{api_endpoint()}/clients/api/v1/clients/{id_client}/main-address'
    payload = {
        "address": "68 13A 52",
        "address": "68 13A 52",
        "addressPrefix": "Av",
        "addressComplement": "La casa que no tiene numero",
        "city": "BOGOTA",
        "cityId": "10101",
        "department": "CUNDINAMARCA",
        "departmentId": "1111",
        "code": "110141"
    }
    response = r.post(url, data=payload, headers=auth_headers(token))
    return response
