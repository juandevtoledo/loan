import os
import sys
import time
import uuid
import suite
import unittest
from utils import fmt
from credits import back
from aws_sqs import risk
from aws_dynamo import db
from workflow import user_banner_with_card, accept_offer

file_dir = os.path.dirname(__file__)
sys.path.append(file_dir)


class CreditsOfferCase(unittest.TestCase):
    """Accept offer comfortable loan to new user in Localhost/Sandbox"""

    def test_simulation(self):
        s = suite.create()
        # Risk engine result
        approved = risk.send_event_pre_approved(s.id_client)
        # Pre approved offer
        time.sleep(3)
        simulation = back.pre_approved_offer(s.id_client, approved, s.token)
        print(f'user simulation: {fmt(simulation.json())} \n')
        self.assertEqual(simulation.status_code, 200)

    def test_waiting_list_error(self):
        s = suite.create()
        # Waiting list
        response = back.waiting_list(s.id_client, str(uuid.uuid4()), s.token)
        print(f'waiting list: {fmt(response.json())} \n')
        self.assertEqual(response.status_code, 406)
        self.assertEqual(response.json()['code'], "CRE_115")

    def test_simulation_error(self):
        s = suite.fixed_login()
        response = back.payment_plan(s.id_client, s.token)
        payment_plan_rs = response.json()
        print(f'payment plan: {fmt(payment_plan_rs)} \n')
        # self.assertIsNotNone(payment_plan_rs['principalDebit'])
        # self.assertEqual(len(payment_plan_rs['paymentPlan']), 48)
        self.assertEquals(response.status_code, 404)

    def test_user_banner_with_card(self):
        s = user_banner_with_card.create_user(self)
        # ClientVerificationResult create the initial product offer
        time.sleep(5)
        id_product_offer = user_banner_with_card.query_banner(s.id_client)
        self.assertIsNotNone(id_product_offer)
        savings_account_rs = back.create_savings_account(s.id_client, s.document_id, s.user.email, s.user.phone, s.token)
        print(f'savings: {fmt(savings_account_rs.json())}\n')
        create_card_rs = back.create_card(s.id_client, s.token)
        self.assertEqual(create_card_rs.status_code, 202)

    def test_accept_offer_from_waiting_list(self):
        s = user_banner_with_card.create_user(self)
        # ClientVerificationResult create the initial product offer
        session = db.create_session()
        time.sleep(5)
        id_product_offer = user_banner_with_card.query_banner(s.id_client)
        self.assertIsNotNone(id_product_offer)
        response_wl = back.waiting_list(s.id_client, id_product_offer, s.token)
        # CreatePreApprovedOfferMessage message is called
        self.assertEqual(response_wl.status_code, 200)
        user = s.user
        new_offer = back.create_offer(s.id_client, s.document_id, user.email, user.phone, s.token)
        print(f'new offer: {fmt(new_offer.json())}\n')
        id_credit = new_offer.json()['idCredit']
        # Query offers
        offers = back.get_offer(s.id_client, s.token)
        savings_account_rs = back.create_savings_account(s.id_client, s.document_id, user.email, user.phone, s.token)
        print(f'savings: {fmt(savings_account_rs.json())}\n')
        create_card_rs = back.create_card(s.id_client, s.token)
        self.assertEquals(create_card_rs.status_code, 202)
        print(f'offers: {fmt(offers.json())}\n')
        credits_table = session.Table('Credits')
        # Accept offer
        credit_rs = back.accept_offer(s.id_client, id_credit, id_product_offer,
                                      credits_table, s.token)
        print(f'credit_rs: {fmt(credit_rs.json())}\n')
        self.assertTrue(credit_rs.json()['valid'])
        accept_offer.summary(self, id_credit, credits_table)


if __name__ == '__main__':
    main = unittest.main()
