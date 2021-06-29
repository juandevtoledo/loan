import time
import uuid
import suite
from credits import back
from aws_sqs import risk
from aws_dynamo import db
from utils import fmt, random_document


def create_user(self):
    s = suite.create()
    id_transaction_biometric = str(uuid.uuid4())
    identity_biometric_rs = back.identity_biometric(s.id_client, id_transaction_biometric, s.token)
    self.assertEqual(identity_biometric_rs.status_code, 200)
    document_id = random_document()
    risk.send_client_verification_result(id_transaction_biometric, s.id_client, document_id)
    # process on boarding
    time.sleep(5)
    address_client_rs = back.address_client(s.id_client,  s.token)
    print(f'address_client: {fmt(address_client_rs.json())}\n')
    # process blacklisted finished
    risk.send_client_verification_result(id_transaction_biometric, s.id_client, document_id, 'NON_BLACKLISTED')
    s.document_id = document_id

    return s


def query_banner(id_client):
    session = db.create_session()
    client_table = session.Table('Clients')
    item = db.query_clients_key(id_client, client_table)
    print(f'clients item: {fmt(item)}\n')
    product_offer = item['approvedRiskAnalysis']['results'][0]
    print(f'pre approved item: {fmt(product_offer)}\n')
    return product_offer['idProductOffer']
