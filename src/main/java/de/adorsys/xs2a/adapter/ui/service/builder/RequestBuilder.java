package de.adorsys.xs2a.adapter.ui.service.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.adorsys.xs2a.adapter.model.ConsentsTO;
import de.adorsys.xs2a.adapter.service.RequestHeaders;
import de.adorsys.xs2a.adapter.service.model.PsuData;
import de.adorsys.xs2a.adapter.service.model.UpdatePsuAuthentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.adorsys.xs2a.adapter.ui.controller.RedirectController.NOK_REDIRECT_PATH;
import static de.adorsys.xs2a.adapter.ui.controller.RedirectController.REDIRECT_PATH;

@Component
public class RequestBuilder {
    private static final String APPLICATION_JSON = "application/json";
    private static final String DEFAULT_PSU_ID_TYPE = "";
    private static final String DEFAULT_PSU_IP_ADDRESS = "0.0.0.0";

    private final ModelBuilder modelBuilder;
    private final ObjectMapper objectMapper;

    public RequestBuilder(ModelBuilder modelBuilder, ObjectMapper objectMapper) {
        this.modelBuilder = modelBuilder;
        this.objectMapper = objectMapper;
    }

    public ConsentsTO createConsentBody(String iban) {
        return modelBuilder.buildConsents(iban);
    }

    public Map<String, String> createConsentHeaders(String psuId, String aspspId, String sessionId,
                                                    UriComponentsBuilder uriComponentsBuilder) {
        Map<String, String> headers = new HashMap<>();

        headers.put(RequestHeaders.PSU_ID, psuId);
        headers.put(RequestHeaders.PSU_ID_TYPE, DEFAULT_PSU_ID_TYPE);
        headers.put(RequestHeaders.X_GTW_ASPSP_ID, aspspId);
        headers.put(RequestHeaders.CONTENT_TYPE, APPLICATION_JSON);
        headers.put(RequestHeaders.X_REQUEST_ID, UUID.randomUUID().toString());
        headers.put(RequestHeaders.PSU_IP_ADDRESS, DEFAULT_PSU_IP_ADDRESS);
        headers.put(RequestHeaders.TPP_REDIRECT_URI, tppRedirectUri(uriComponentsBuilder));
        headers.put(RequestHeaders.TPP_NOK_REDIRECT_URI, tppNokRedirectUri(uriComponentsBuilder));
        headers.put(RequestHeaders.CORRELATION_ID, sessionId);

        return headers;
    }

    private String tppRedirectUri(UriComponentsBuilder uriComponentsBuilder) {
        return uriComponentsBuilder.cloneBuilder().path(REDIRECT_PATH).toUriString();
    }

    private String tppNokRedirectUri(UriComponentsBuilder uriComponentsBuilder) {
        return uriComponentsBuilder.cloneBuilder().path(NOK_REDIRECT_PATH).toUriString();
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

    public Map<String, String> selectPsuAuthenticationMethodHeaders(String sessionId, String aspspId) {
        Map<String, String> headers = new HashMap<>();

        headers.put(RequestHeaders.CONTENT_TYPE, APPLICATION_JSON);
        headers.put(RequestHeaders.X_GTW_ASPSP_ID, aspspId);
        headers.put(RequestHeaders.X_REQUEST_ID, UUID.randomUUID().toString());
        headers.put(RequestHeaders.CORRELATION_ID, sessionId);

        return headers;
    }
}
