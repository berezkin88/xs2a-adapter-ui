package de.adorsys.xs2a.adapter.ui.service.builder;

import de.adorsys.xs2a.adapter.model.ConsentsTO;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RequestBuilder {
    private static final String APPLICATION_JSON = "application/json";
    private static final String DEFAULT_PSU_ID_TYPE = "";
    private static final String DEFAULT_PSU_IP_ADDRESS = "0.0.0.0";
    // TODO change to the appropriate URI as soon as it is ready
    private static final String DEFAULT_TPP_REDIRECT_URI = "https://example.com";

    private final ModelBuilder modelBuilder;

    public RequestBuilder(ModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }

    public ConsentsTO createConsentBody(String iban) {
        return modelBuilder.buildConsents(iban);
    }

    public Map<String, String> createConsentHeaders(String psuId, String aspspId, String sessionId) {
        Map<String, String> headers = new HashMap<>();

        headers.put(RequestHeaders.PSU_ID, psuId);
        headers.put(RequestHeaders.PSU_ID_TYPE, DEFAULT_PSU_ID_TYPE);
        headers.put(RequestHeaders.X_GTW_ASPSP_ID, aspspId);
        headers.put(RequestHeaders.CONTENT_TYPE, APPLICATION_JSON);
        headers.put(RequestHeaders.X_REQUEST_ID, UUID.randomUUID().toString());
        headers.put(RequestHeaders.PSU_IP_ADDRESS, DEFAULT_PSU_IP_ADDRESS);
        headers.put(RequestHeaders.TPP_REDIRECT_URI, DEFAULT_TPP_REDIRECT_URI);
        headers.put(RequestHeaders.CORRELATION_ID, sessionId);

        return headers;
    }
}
