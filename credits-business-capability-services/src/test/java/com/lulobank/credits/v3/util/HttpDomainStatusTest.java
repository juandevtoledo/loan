package com.lulobank.credits.v3.util;

import org.junit.Test;

import static com.lulobank.credits.v3.util.HttpDomainStatus.CREATED;
import static com.lulobank.credits.v3.util.HttpDomainStatus.FORBIDDEN;
import static com.lulobank.credits.v3.util.HttpDomainStatus.GATEWAY_TIMEOUT;
import static com.lulobank.credits.v3.util.HttpDomainStatus.Series;
import static com.lulobank.credits.v3.util.HttpDomainStatus.UNSUPPORTED_MEDIA_TYPE;
import static com.lulobank.credits.v3.util.HttpDomainStatus.resolve;
import static com.lulobank.credits.v3.util.HttpDomainStatus.valueOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class HttpDomainStatusTest {

    @Test
    public void shouldResolveOk() {
        assertThat(resolve(201), is(CREATED));
        assertThat(resolve(415), is(UNSUPPORTED_MEDIA_TYPE));
        assertThat(resolve(504), is(GATEWAY_TIMEOUT));
    }

    @Test
    public void shouldReturnValueOf() {
        assertThat(valueOf(201), is(CREATED));
        assertThat(valueOf(415), is(UNSUPPORTED_MEDIA_TYPE));
        assertThat(valueOf(504), is(GATEWAY_TIMEOUT));
    }

    @Test
    public void shouldNotResolveWhenInvalidCode() {
        assertThat(resolve(705), nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotReturnValueOfWhenInvalidCode() {
        valueOf(705);
    }

    @Test
    public void shouldResolveSeriesOk() {
        assertThat(Series.resolve(102), is(Series.INFORMATIONAL));
        assertThat(Series.resolve(201), is(Series.SUCCESSFUL));
        assertThat(Series.resolve(303), is(Series.REDIRECTION));
        assertThat(Series.resolve(415), is(Series.CLIENT_ERROR));
        assertThat(Series.resolve(502), is(Series.SERVER_ERROR));
    }

    @Test
    public void shouldReturnValueOfSeriesOk() {
        assertThat(Series.valueOf(102), is(Series.INFORMATIONAL));
        assertThat(Series.valueOf(FORBIDDEN), is(Series.CLIENT_ERROR));
    }

    @Test
    public void shouldNotReturnResolveSeriesWhenInvalidCode() {
        assertThat(Series.resolve(929), nullValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotReturnValueOfSeriesWhenInvalidCode() {
        Series.valueOf(806);
    }

}