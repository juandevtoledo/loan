import boto3
from utils import *


sqs = boto3.resource('sqs', region_name="us-east-1")


def random_approved():
    return random.randint(1_000_560, 40_000_999)


def create_event_risk_v2_ok(id_client, max_total_amount):
    return {
        'eventType': 'RiskEngineResultEventV2Message',
        'id': '604a3fec-12d5-4231-b5ea-4c1b32302ab0',
        'payload': {
            'idClient': id_client,
            'status': 'completed',
            'results': [
                {
                    'type': 'v1',
                    'schedule': [
                        {
                            'installment': '12',
                            'interestRate': '16.55'
                        },
                        {
                            'installment': '24',
                            'interestRate': '16.3'
                        },
                        {
                            'installment': '36',
                            'interestRate': '15'
                        },
                        {
                            'installment': '48',
                            'interestRate': '14.82'
                        }
                    ],
                    'maxAmountInstallment': 700_041,
                    'maxTotalAmount': max_total_amount,
                    'approved': 1,
                    'description': 'QA Python',
                    'score': 0.014688234845045433
                }
            ]
        }
    }


def create_client_verification_result(id_transaction_biometric, id_document,
                                      id_client, blacklist_status):
    return {
        'eventType': 'ClientVerificationResult',
        'id': id_client,
        'payload': {
            'idTransactionBiometric': id_transaction_biometric,
            'status': 'OK',
            'clientPersonalInformation': {
                'name': 'Python Logos',
                'lastName': 'Integration test',
                'birthDate': '1991-05-25T00:00:00',
                'gender': 'M',
                'idDocument': {
                    'documentType': 'CC',
                    'idCard': id_document,
                    'expeditionDate': '1991-05-26T04:32:55'
                },
                'additionalPersonalInformation': {
                    'firstName': 'Python credits',
                    'firstSurname': 'Logos',

                }
            },
            'transactionState': {
                'id': 2,
                'stateName': 'Proceso satisfactorio'
            },
            'blacklist': {
                'status': blacklist_status,
                'reportDate': '2021-02-01T10:42:26.100',
            }
        },
        'receiveCount': 0,
        'maximumReceives': 5,
        'delay': 5
    }


def send_event_pre_approved(id_client):
    approved = random_approved()
    message('creditsEmailQueue-sand', create_event_risk_v2_ok(id_client, approved))
    return approved


def send_client_verification_result(id_transaction_biometric, id_client, document_id, blacklist_status='STARTED'):
    event = create_client_verification_result(id_transaction_biometric, document_id, id_client, blacklist_status)
    # remove-clientsAutomaticDebt
    message('clientsEvents-sand', event)


def message(queue_url, event):
    queue = sqs.get_queue_by_name(QueueName=queue_url)
    print('***** SQS EVENT *****')
    print(f'queue: {queue_url}')
    print(f'event: {fmt(event)}')
    print('***** SQS EVENT *****')
    queue.send_message(MessageBody=json.dumps(event))
