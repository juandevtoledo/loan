package com.lulobank.credits.starter.v3.mappers;

import com.lulobank.credits.v3.dto.DocumentIdV3;
import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteRequest;
import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteResponse;
import com.lulobank.promissorynote.sdk.dto.CreatePromissoryNoteClientAndSign;
import com.lulobank.promissorynote.sdk.dto.CreatePromissoryNoteClientAndSignResponse;
import org.junit.Test;

import static com.lulobank.credits.starter.utils.Constants.CLIENT_ACCOUNT_ID;
import static com.lulobank.credits.starter.utils.Constants.EMAIL;
import static com.lulobank.credits.starter.utils.Constants.ID_CARD;
import static com.lulobank.credits.starter.utils.Constants.NAME;
import static com.lulobank.credits.starter.utils.Constants.PASSWORD;
import static com.lulobank.credits.starter.utils.Constants.PROMISSORY_NOTE_ID;
import static com.lulobank.credits.starter.utils.Constants.TYPE_CARD;
import static com.lulobank.credits.starter.utils.Samples.createPromissoryNoteClientAndSignResponseBuilder;
import static com.lulobank.credits.starter.utils.Samples.documentIdV3Builder;
import static com.lulobank.credits.starter.utils.Samples.promissoryNoteRequestBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PromissoryNoteMapperTest {

    @Test
    public void toCreatePromissoryNoteClientAndSignMapper() {
        DocumentIdV3 documentIdV3 = documentIdV3Builder();
        PromissoryNoteRequest promissoryNoteRequest = promissoryNoteRequestBuilder(documentIdV3);
        CreatePromissoryNoteClientAndSign createPromissoryNoteClientAndSign = PromissoryNoteMapper.INSTANCE.toCreatePromissoryNoteClientAndSign(promissoryNoteRequest);
        assertThat("NAME is right", createPromissoryNoteClientAndSign.getName(), is(NAME));
        assertThat("EMAIL is right", createPromissoryNoteClientAndSign.getEmail(), is(EMAIL));
        assertThat("ID_CARD is right", createPromissoryNoteClientAndSign.getDocumentId().getId(), is(ID_CARD));
        assertThat("ID_CARD is right", createPromissoryNoteClientAndSign.getDocumentId().getType(), is(TYPE_CARD));
    }

    @Test
    public void toCreatePromissoryNoteClientAndSignResponse() {
        CreatePromissoryNoteClientAndSignResponse createPromissoryNoteClientAndSignResponse = createPromissoryNoteClientAndSignResponseBuilder();
        PromissoryNoteResponse promissoryNoteResponse = PromissoryNoteMapper.INSTANCE.toCreatePromissoryNoteClientAndSignResponse(createPromissoryNoteClientAndSignResponse);
        assertThat("Client Account Id is right", promissoryNoteResponse.getClientAccountId(), is(CLIENT_ACCOUNT_ID));
        assertThat("Promissory Note Id is right", promissoryNoteResponse.getPromissoryNoteId(), is(PROMISSORY_NOTE_ID));
        assertThat("Password right", promissoryNoteResponse.getSignPassword(), is(PASSWORD));
    }
}
