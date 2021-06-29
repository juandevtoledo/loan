package com.lulobank.credits.v3.port.in.clientinformation;

import com.lulobank.credits.Samples;
import com.lulobank.credits.services.outboundadapters.flexibility.flexibilitystateenum.CbsLoanStateEnum;
import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static com.lulobank.credits.Constants.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UpdateProductEmailUseCaseTest {

    private UpdateProductEmailUseCase testedClass;
    private UpdateProductEmailMessage updateEmailPayload;
    @Mock
    private CreditsV3Repository creditsRepository;
    @Captor
    private ArgumentCaptor<String> textCaptor;
    @Captor
    private ArgumentCaptor<CreditsV3Entity> creditsEntityCaptor;
    private CreditsV3Entity creditsEntity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        updateEmailPayload = new UpdateProductEmailMessage()
                .setIdClient(CLIENT_ID)
                .setNewEmail(NEW_EMAIL);
        testedClass = new UpdateProductEmailUseCase(creditsRepository);
    }

    @Test
    public void should_update_email() throws IOException {
        creditsEntity = Samples.creditsV3EntityBuilder();
        when(creditsRepository.findByIdClient(textCaptor.capture())).thenReturn(List.of(creditsEntity));
        when(creditsRepository.save(creditsEntityCaptor.capture())).thenReturn(Try.of(()->creditsEntity));
        testedClass.execute(updateEmailPayload);
        verify(creditsRepository, times(1)).findByIdClient(any());
        verify(creditsRepository, times(1)).save(any());
        assertEquals(NEW_EMAIL, creditsEntityCaptor.getValue().getClientInformation().getEmail());
        assertEquals(CLIENT_ID, textCaptor.getValue());
    }

    @Test
    public void should_not_update_as_new_email_is_not_new() throws IOException {
        creditsEntity = Samples.creditsV3EntityBuilder();
        updateEmailPayload.setNewEmail(CLIENT_EMAIL);
        when(creditsRepository.findByIdClient(CLIENT_ID)).thenReturn(List.of(creditsEntity));
        testedClass.execute(updateEmailPayload);
        verify(creditsRepository, times(1)).findByIdClient(any());
        verify(creditsRepository, times(0)).save(any());
    }

    @Test
    public void should_not_update_as_credit_is_closed() throws IOException {
        creditsEntity = Samples.creditsV3EntityBuilder();
        creditsEntity.getLoanStatus().setStatus(CbsLoanStateEnum.CLOSED.name());
        when(creditsRepository.findByIdClient(CLIENT_ID)).thenReturn(List.of(creditsEntity));
        testedClass.execute(updateEmailPayload);
        verify(creditsRepository, times(1)).findByIdClient(any());
        verify(creditsRepository, times(0)).save(any());
    }

    @Test
    public void should_not_update_as_mambu_id_is_nonexistent() throws IOException {
        creditsEntity = Samples.creditsV3EntityBuilder();
        creditsEntity.setIdLoanAccountMambu(null);
        when(creditsRepository.findByIdClient(CLIENT_ID)).thenReturn(List.of(creditsEntity));
        testedClass.execute(updateEmailPayload);
        verify(creditsRepository, times(1)).findByIdClient(any());
        verify(creditsRepository, times(0)).save(any());
    }

    @Test
    public void should_not_update_as_client_information_empty() throws IOException {
        creditsEntity = Samples.creditsV3EntityBuilder();
        creditsEntity.setClientInformation(null);
        updateEmailPayload.setNewEmail(CLIENT_EMAIL);
        when(creditsRepository.findByIdClient(CLIENT_ID)).thenReturn(List.of(creditsEntity));
        testedClass.execute(updateEmailPayload);
        verify(creditsRepository, times(1)).findByIdClient(any());
        verify(creditsRepository, times(0)).save(any());
    }

}
