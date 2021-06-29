package com.lulobank.credits.starter.v3.adapters.out.promissorynote;

import static com.lulobank.credits.starter.utils.Constants.ACCOUNT_ID;
import static com.lulobank.credits.starter.utils.Constants.PASSWORD;
import static com.lulobank.credits.starter.utils.Constants.PROMISSORY_NOTE_ID;
import static com.lulobank.credits.starter.utils.Samples.createPromissoryNoteClientAndSignResponseBuilder;
import static com.lulobank.credits.starter.utils.Samples.documentIdV3Builder;
import static com.lulobank.credits.starter.utils.Samples.promissoryNoteRequestBuilder;
import static java.lang.Integer.parseInt;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.credits.v3.port.in.promissorynote.dto.PromissoryNoteResponse;
import com.lulobank.promissorynote.sdk.dto.CreatePromissoryNoteClientAndSignResponse;
import com.lulobank.promissorynote.sdk.operations.IPromissoryNote;

import io.vavr.control.Either;
import io.vavr.control.Try;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class PromissoryNoteServiceAdapterTest {

    @Mock
    private IPromissoryNote promissoryNote;
    private PromissoryNoteServiceAdapter testedClass;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        testedClass = new PromissoryNoteServiceAdapter(promissoryNote);
    }

    @Test
    public void sing() {
        CreatePromissoryNoteClientAndSignResponse signResponse= createPromissoryNoteClientAndSignResponseBuilder();
        when(promissoryNote.createPromissoryNote(anyMap(), any())).thenReturn(Either.right(signResponse));
        Try<PromissoryNoteResponse> reponse = testedClass.sign(promissoryNoteRequestBuilder(documentIdV3Builder()),new HashMap<>());
        assertFalse("Is Not Empty", reponse.isEmpty());
        assertThat("Account id is right", reponse.get().getClientAccountId(),is(parseInt(ACCOUNT_ID)));
        assertThat("Promissory id is right", reponse.get().getPromissoryNoteId(),is(PROMISSORY_NOTE_ID));
        assertThat("Promissory id is right", reponse.get().getSignPassword(),is(PASSWORD));
    }

    @Test
    public void shouldEmpty() {
        Response response=Response.error(500, ResponseBody.create(null, new byte[0]));;
        when(promissoryNote.createPromissoryNote(anyMap(), any())).thenReturn(Either.left(response.errorBody()));
        promissoryNoteRequestBuilder(documentIdV3Builder());
        Try<PromissoryNoteResponse> reponse = testedClass.sign(promissoryNoteRequestBuilder(documentIdV3Builder()),new HashMap<>());
        assertTrue("Not found error", reponse.isEmpty());
    }
}
