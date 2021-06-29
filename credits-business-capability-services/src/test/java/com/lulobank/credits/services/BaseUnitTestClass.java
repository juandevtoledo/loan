package com.lulobank.credits.services;

import com.lulobank.credits.services.domain.CreditsConditionDomain;
import com.lulobank.credits.services.outboundadapters.model.CreditsEntity;
import com.lulobank.credits.services.outboundadapters.repository.CreditsRepository;
import flexibility.client.sdk.FlexibilitySdk;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public abstract class BaseUnitTestClass {
    @Mock
    protected CreditsRepository creditsRepository;
    @Mock
    protected FlexibilitySdk coreBankingSdk;
    @Mock
    protected CreditsConditionDomain creditsConditionDomain;
    @Captor
    protected ArgumentCaptor<CreditsEntity> creditsEntityCaptor;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        init();
    }
    protected abstract void init();
}
