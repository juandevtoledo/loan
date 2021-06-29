import boto3


def create_session():
    session = boto3.Session(region_name='us-east-1')
    return session.resource('dynamodb')


def query_credit_key(key, table):
    return get_item(key, 'idCredit', table)


def query_clients_key(key, table):
    return get_item(key, 'idClient', table)


def get_item(key_value, id_key, table):
    result = table.get_item(
        Key={id_key: key_value}
    )
    return result['Item']
