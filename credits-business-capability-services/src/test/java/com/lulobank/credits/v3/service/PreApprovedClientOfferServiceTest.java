package com.lulobank.credits.v3.service;

import static com.lulobank.credits.v3.util.EntitiesFactory.CreditsEntityFactory.creditsEntityOfferPreApproved;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lulobank.credits.v3.port.out.CreditsV3Entity;
import com.lulobank.credits.v3.port.out.CreditsV3Repository;

import io.vavr.collection.List;
import io.vavr.control.Try;

public class PreApprovedClientOfferServiceTest {
	
	private PreApprovedClientOfferService preApprovedClientOfferService;
	
	@Mock
	private CreditsV3Repository creditsV3Repository;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		preApprovedClientOfferService = new PreApprovedClientOfferService(creditsV3Repository);
	}
	
	@Test
	public void closePreApprovedClientOffersNonEmpty() {
		String idClient = "idClient";
		List<CreditsV3Entity> creditList = List.of(creditsEntityOfferPreApproved());
		when(creditsV3Repository.findByIdClient(idClient)).thenReturn(creditList);
		when(creditsV3Repository.save(any())).thenReturn(Try.of(() -> creditsEntityOfferPreApproved()));
		
		Try<String> response = preApprovedClientOfferService.closePreApprovedClientOffers(idClient);
		assertTrue(response.isSuccess());
		verify(creditsV3Repository).save(any());
	}
	
	@Test
	public void closePreApprovedClientOffersEmpty() {
		String idClient = "idClient";
		List<CreditsV3Entity> creditList = List.empty();
		when(creditsV3Repository.findByIdClient(idClient)).thenReturn(creditList);
		when(creditsV3Repository.save(any())).thenReturn(Try.of(() -> creditsEntityOfferPreApproved()));
		
		Try<String> response = preApprovedClientOfferService.closePreApprovedClientOffers(idClient);
		assertTrue(response.isSuccess());
		verify(creditsV3Repository, times(0)).save(any());
	}
}
