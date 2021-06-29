package com.lulobank.credits.v3.events;

import com.lulobank.credits.services.events.CreditAccepted;
import com.lulobank.credits.v3.port.in.loan.LoanFactory;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EventMapperV3Test {

    private final static String EVENT_NAME ="CreditAccepted";
    private final static String CLIEND_ID ="0000";

    @Test
    public void createRequest() {

        CreditAccepted creditAccepted = new CreditAccepted();
        creditAccepted.setIdClient(CLIEND_ID);
        EventV3<CreditAccepted> event = EventMapperV3.of(creditAccepted);

        Pattern patron = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        Matcher match = patron.matcher(event.getId());

        assertThat(event.getEventType().equals(EVENT_NAME), is(true));
        assertThat(event.getPayload().getIdClient().equals(CLIEND_ID), is(true));
        assertThat(match.find(), is(true));


    }
}
