package com.lulobank.credits.starter.config;

import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddResponseHeaderFilterTest {

    private static final String jwt = "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ.Sle5Ry0WrmGrgN4sLfmRGxuLTGOB8GWZwZO7fYjxrejcdL2xTMxOqtHSxn0MvTc_sOKAtPzLM8LRk0TDNvaqoFWgud65R437XjPlJN3FSw0mDBu2zcXJ28J1Tj4FSebZAhyE1FElU2S6JvL1--QjSUE2tMOu-gXaVViSAKwlHDP30kCeKkvDNvGpwMj6EX5NeHH49bDVt3AatYLnvf2pESPckR7tWKM1Qv8xPUywcSsPA7QgXyDrDMJ0T68ZzLyFtEqAPUWis6r83_VoMRSZWWMYcpUbKfuONScafbhix3JtB20lqioNOeAKfy82g150_yZYSAJ07LJPt8Y3LrcXHA.z9BbuBkT_TvNJpbW.7FOQlDjQHSB79me4xFq2brasFpAKkHgXzdKloNFcO0TPI7ZrZlrI0My_d06M_e6QbqwNfOMvXITDA-hzlL0sjWuxBl0g269M4_nyK3XhLfrp9-O7YuZ3GBV7JL89wBo1P6WRBApVz-PS55WV2jJnT2HFev5Q4KvOnT_Kgvb5lwIcQY8ehicczhoz-rPAP3mCUL-iTVkVkzWabDPcrDbxRgdwuSrIScwqG7AvLOdalFKM0dArxTC8NXAFq_8iRA1hnDIDLU632o8JQKoLg1zwBmpv2x1r7Zjc-jKJL1ECu38r8uiid74MVYFEk1AdUZmJ8GruaA4PDIXDQ8jXolqQVk3Tc3lgyIqqpmZ7eBPRGDC88l9ZD6QOcnIKg3C3qkezHe9BJ6zByK4_Lnwnr1z9tBQ9IXU8eaV6I-AIRwXXHjlVWbzIz-1IGXfDtIkW77QdRnVMYv_XCwZc1AHMG7wadGPW7KMTXjwukVt4tVv8Yh_OBDp2LXCw2Z-y4cbbVSthqYNboI03NMJz31aCE5BIrl-rUygnmBHkAUJmGEgPOglPlJSv3vgHwFKzd5PJRhvOl6s8w4q9bci-Wyu_SU0rin9HvCzH7pBdcaxzlvz27akqhvXKT9PGOYD_T67bZxmiRzMzBG7Tt9uOsKy1gmbVZr9MonpnqA.ORcIa2MwQPVp8uBQhosoaw";

    @Test
    public void testWhenJWTPresent() throws IOException, ServletException {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(httpServletRequest.getHeader("token-type")).thenReturn("userToken");
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer " + jwt);

        AddResponseHeaderFilter maintenanceFilter = new AddResponseHeaderFilter();
        maintenanceFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);

        verify(httpServletResponse).setHeader("nextAccessToken", jwt);
    }

    @Test
    public void testWhenJWTNotPresent() throws IOException, ServletException {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(httpServletRequest.getHeader("token-type")).thenReturn(null);

        AddResponseHeaderFilter maintenanceFilter = new AddResponseHeaderFilter();
        maintenanceFilter.doFilter(httpServletRequest, httpServletResponse,
                filterChain);
        verify(httpServletResponse, never()).setHeader("nextAccessToken", jwt);
        verify(httpServletResponse, never()).setHeader("nextAccessToken", null);
    }
}
