package com.lulobank.credits.v3.port.out.productoffer;

import static com.lulobank.credits.v3.port.out.productoffer.ProductOfferErrorStatus.CRE_115;
import static com.lulobank.credits.v3.port.out.productoffer.ProductOfferErrorStatus.DEFAULT_DETAIL;
import static com.lulobank.credits.v3.util.HttpDomainStatus.NOT_ACCEPTABLE;

import com.lulobank.credits.v3.vo.UseCaseResponseError;

public class ProductOfferError extends UseCaseResponseError {

	private ProductOfferError(ProductOfferErrorStatus pepErrorStatus, String providerCode) {
		super(pepErrorStatus.name(), providerCode, DEFAULT_DETAIL);
	}

	public static ProductOfferError errorGettingData() {
		return new ProductOfferError(CRE_115, NOT_ACCEPTABLE.toString());
	}
}
