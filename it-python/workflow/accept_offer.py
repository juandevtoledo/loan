from utils import fmt
from aws_dynamo import db


def summary(self, id_credit, credits_table):
    credits_item = db.query_credit_key(id_credit, credits_table)
    print(f'credits_item: {fmt(credits_item)}\n')
    id_saving_account_cbs = credits_item['idSavingAccount']
    self.assertIsNotNone(id_saving_account_cbs)
    id_loan_account_cbs = credits_item['idLoanAccountMambu']
    self.assertIsNotNone(id_loan_account_cbs)
    id_client_cbs = credits_item['idClientMambu']
    self.assertIsNotNone(id_client_cbs)
    print("***** CREDITS ITEM *****")
    print(f'id_saving_account {id_saving_account_cbs}')
    print(f'id_loan_account_cbs {id_loan_account_cbs}')
    print(f'id_client_cbs {id_client_cbs}')
    print("***** CREDITS ITEM *****")
