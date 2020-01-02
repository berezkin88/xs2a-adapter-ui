package de.adorsys.xs2a.adapter.ui.service.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.adorsys.xs2a.adapter.model.ConsentsTO;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.model.PsuData;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
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
    private final ObjectMapper objectMapper;

    public RequestBuilder(ModelBuilder modelBuilder, ObjectMapper objectMapper) {
        this.modelBuilder = modelBuilder;
        this.objectMapper = objectMapper;
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

    public ObjectNode startAuthorisationBody() {
        return new ObjectNode(JsonNodeFactory.instance);
    }

    public Map<String, String> startAuthorisationHeaders(String psuId, String aspspId, String sessionId) {
        Map<String, String> headers = new HashMap<>();

        headers.put(RequestHeaders.PSU_ID, psuId);
        headers.put(RequestHeaders.PSU_ID_TYPE, DEFAULT_PSU_ID_TYPE);
        headers.put(RequestHeaders.X_GTW_ASPSP_ID, aspspId);
        headers.put(RequestHeaders.CONTENT_TYPE, APPLICATION_JSON);
        headers.put(RequestHeaders.X_REQUEST_ID, UUID.randomUUID().toString());
        headers.put(RequestHeaders.CORRELATION_ID, sessionId);

        return headers;
    }

    public ObjectNode startAuthorisationWithPsuAuthenticationBody(String pin, boolean encrypted) {
        PsuData psuData = new PsuData();

        if (encrypted) {
            psuData.setEncryptedPassword(pin);
        } else {
            psuData.setPassword(pin);
        }

        UpdatePsuAuthentication updatePsuAuthentication = new UpdatePsuAuthentication();
        updatePsuAuthentication.setPsuData(psuData);

        return objectMapper.valueToTree(updatePsuAuthentication);
    }

    public Map<String, String> startAuthorisationWithPsuAuthenticationHeaders(String psuId, String aspspId, String sessionId) {
        return startAuthorisationHeaders(psuId, aspspId, sessionId);
    }

    public ObjectNode updateConsentsPsuDataPsuPasswordStageBody(String pin, boolean encrypted) {
        return startAuthorisationWithPsuAuthenticationBody(pin, encrypted);
    }

    public Map<String, String> updateConsentsPsuDataPsuPasswordStageHeaders(String psuId, String aspspId, String sessionId) {
        return startAuthorisationWithPsuAuthenticationHeaders(psuId, aspspId, sessionId);
    }
}
