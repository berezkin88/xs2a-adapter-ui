package de.adorsys.xs2a.adapter.ui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.adorsys.xs2a.adapter.model.*;
import de.adorsys.xs2a.adapter.remote.api.AccountInformationClient;
import de.adorsys.xs2a.adapter.service.model.TransactionsReport;
import de.adorsys.xs2a.adapter.ui.service.builder.RequestBuilder;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class AccountInformationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountInformationService.class);

    private final AccountInformationClient accountInformationClient;
    private final RequestBuilder requestBuilder;
    private final ObjectMapper objectMapper;

    public AccountInformationService(AccountInformationClient accountInformationClient,
                                     RequestBuilder requestBuilder,
                                     ObjectMapper objectMapper) {
        this.accountInformationClient = accountInformationClient;
        this.requestBuilder = requestBuilder;
        this.objectMapper = objectMapper;
    }

    public ConsentsResponse201TO createConsent(String iban, String psuId, String aspspId, String sessionId,
                                               UriComponentsBuilder uriComponentsBuilder) {
        LOGGER.info("{}: create consent", sessionId);

        ResponseEntity<ConsentsResponse201TO> response;
        try {
            response = accountInformationClient.createConsent(
                    requestBuilder.createConsentHeaders(psuId, aspspId, sessionId, uriComponentsBuilder),
                    requestBuilder.createConsentBody(iban)
            );
        } catch (FeignException e) {
            LOGGER.error("{}: create consent response status - {}", sessionId, e.status());
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LOGGER.info("{}: create consent response status - {}", sessionId, response.getStatusCodeValue());

        return response.getBody();
    }

    public StartScaprocessResponseTO startAuthorisation(String consentId, String psuId, String aspspId, String sessionId) {
        LOGGER.info("{}: start authorisation", sessionId);

        ResponseEntity<StartScaprocessResponseTO> response;
        try {
            response = accountInformationClient.startConsentAuthorisation(
                    consentId,
                    requestBuilder.startAuthorisationHeaders(psuId, aspspId, sessionId),
                    requestBuilder.startAuthorisationBody()
            );
        } catch (FeignException e) {
            LOGGER.error("{}: start authorisation response status - {}", sessionId, e.status());
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LOGGER.info("{}: start authorisation response status - {}", sessionId, response.getStatusCodeValue());

        return response.getBody();
    }

    public StartScaprocessResponseTO startAuthorisationWithPsuAuthentication(String consentId, String psuId, String pin,
                                                                             String aspspId, String sessionId,
                                                                             boolean encrypted) {
        LOGGER.info("{}: start authorisation with PSU authentication", sessionId);

        ResponseEntity<StartScaprocessResponseTO> response;
        try {
            response = accountInformationClient.startConsentAuthorisation(
                    consentId,
                    requestBuilder.startAuthorisationWithPsuAuthenticationHeaders(psuId, aspspId, sessionId),
                    requestBuilder.startAuthorisationWithPsuAuthenticationBody(pin, encrypted)
            );
        } catch (FeignException e) {
            LOGGER.error("{}: start authorisation with PSU authentication response status - {}", sessionId, e.status());
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LOGGER.info("{}: start authorisation with PSU authentication response status - {}", sessionId, response.getStatusCodeValue());

        return response.getBody();
    }

    // TODO investigate the possibility to extract method parameters into some DTO/DTOs
    public UpdatePsuAuthenticationResponseTO updateConsentsPsuDataPsuPasswordStage(String consentId, String authorisationId, String psuId,
                                                                                   String pin, String aspspId, String sessionId,
                                                                                   boolean encrypted) {
        LOGGER.info("{}: update consent PSU data (PSU password stage)", sessionId);

        ResponseEntity<Object> response;
        try {
            response = accountInformationClient.updateConsentsPsuData(
                    consentId, authorisationId,
                    requestBuilder.updateConsentsPsuDataPsuPasswordStageHeaders(psuId, aspspId, sessionId),
                    requestBuilder.updateConsentsPsuDataPsuPasswordStageBody(pin, encrypted)
            );
        } catch (FeignException e) {
            LOGGER.error("{}: update consent PSU data (PSU password stage) response status - {}", sessionId, e.status());
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LOGGER.info("{}: update consent PSU data (PSU password stage) response status - {}", sessionId, response.getStatusCodeValue());

        return objectMapper.convertValue(response.getBody(), UpdatePsuAuthenticationResponseTO.class);
    }

    public void updateConsentsPsuData(String sessionId,
                                      String aspspid,
                                      String consentId,
                                      String authorisationId,
                                      SelectPsuAuthenticationMethodTO selectPsuAuthenticationMethodTO) {
        LOGGER.info("{}: update consent PSU data (selectPsuAuthenticationMethod)", sessionId);

        Map<String, String> headers = requestBuilder.selectPsuAuthenticationMethodHeaders(sessionId, aspspid);
        ObjectNode body = objectMapper.convertValue(selectPsuAuthenticationMethodTO, ObjectNode.class);
        ResponseEntity<Object> response;
        try {
            response = accountInformationClient.updateConsentsPsuData(consentId, authorisationId, headers, body);
        } catch (FeignException e) {
            LOGGER.error("{}: update consent PSU data (selectPsuAuthenticationMethod) response status - {}",
                    sessionId,
                    e.status());
            throw new RuntimeException(e);
        }
        LOGGER.info("{}: update consent PSU data (selectPsuAuthenticationMethod) response status - {}", sessionId,
                response.getStatusCodeValue());
    }

    public void updateConsentsPsuDataPsuOtpStage(String consentId, String authorisationId, String psuId,
                                                 String otp, String aspspId, String sessionId) {
        LOGGER.info("{}: update consent PSU data (PSU otp stage)", sessionId);

        ResponseEntity<Object> response;
        try {
            response = accountInformationClient.updateConsentsPsuData(consentId, authorisationId,
                requestBuilder.updateConsentsPsuDataPsuPasswordStageHeaders(psuId, aspspId, sessionId),
                requestBuilder.updateConsentsPsuDataPsuOtpStageBody(otp));
        } catch (FeignException e) {
            LOGGER.error("{}: update consent PSU data (PSU otp stage) response status - {}", sessionId, e.status());
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LOGGER.info("{}: update consent PSU data (PSU otp stage) response status - {}", sessionId, response.getStatusCodeValue());
    }

    public ScaStatusResponseTO getConsentScaStatus(String consentId, String authorisationId, String psuId,
                                                   String aspspId, String sessionId) {
        LOGGER.info("{}: get consent status", sessionId);

        ResponseEntity<ScaStatusResponseTO> response;
        try {
            response = accountInformationClient.getConsentScaStatus(consentId, authorisationId,
                requestBuilder.updateConsentsPsuDataPsuPasswordStageHeaders(psuId, aspspId, sessionId));
        } catch (FeignException e) {
            LOGGER.error("{}: get consent status response status - {}", sessionId, e.status());
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LOGGER.info("{}: consent status response status - {}", sessionId, response.getStatusCodeValue());
        return response.getBody();
    }


    public AccountListTO getAccountList(String consentId, String aspspId, String sessionId) {
        LOGGER.info("{}: get account list", sessionId);

        ResponseEntity<AccountListTO> response;
        try {
            response = accountInformationClient.getAccountList(false,
                requestBuilder.getAccountListHeaders(consentId, aspspId, sessionId));
        } catch (FeignException e) {
            LOGGER.error("{}: get account list response status - {}", sessionId, e.status());
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LOGGER.info("{}: get account list response status - {}", sessionId, response.getStatusCodeValue());
        return response.getBody();
    }

    public void getTransactionList(String accountId, LocalDate dateFrom, LocalDate dateTo,
                                   String consentId, String aspspId, String sessionId) {
        LOGGER.info("{}: get transaction list", sessionId);

        ResponseEntity<TransactionsReport> response;
        try {
            response = accountInformationClient.getTransactionList(accountId,
                dateFrom,
                dateTo,
                null,
                BookingStatusTO.BOOKED,
                null,
                true,
                requestBuilder.getTransactionsListHeaders(consentId, aspspId, sessionId));
        } catch (FeignException e) {
            LOGGER.error("{}: get transaction list response status - {}", sessionId, e.status());
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LOGGER.info("{}: get transaction list response status - {}", sessionId, response.getStatusCodeValue());
    }
}
