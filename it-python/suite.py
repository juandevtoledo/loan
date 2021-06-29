from credits import client_credentials, create_user, user_credentials
from utils import fmt


class Suite:
    def __init__(self, user, oauth_token, id_client, token, document_id=None):
        self.user = user
        self.oauth_token = oauth_token
        self.id_client = id_client
        self.token = token
        self.document_id = document_id


def create():
    oauth_token = client_credentials.oauth_token()
    user = create_user.execute(oauth_token)
    user_metadata = user_credentials.login(user.email, user.pwd, oauth_token)
    id_client = user_metadata['content']['idClient']
    token = user_metadata['accessToken']
    print(f'\n >> USER METADATA: {fmt(user_metadata)}\n')
    return Suite(user, oauth_token, id_client, token)


def fixed_login():
    oauth_token = client_credentials.oauth_token()
    mail = "tae_fximg@mailinator.com"
    pwd = "654321"
    user_metadata = user_credentials.login(mail, pwd, oauth_token)
    id_client = user_metadata['content']['idClient']
    token = user_metadata['accessToken']
    print(f'\n>> USER METADATA:  {fmt(user_metadata)}\n')
    return Suite(None, oauth_token, id_client, token)
