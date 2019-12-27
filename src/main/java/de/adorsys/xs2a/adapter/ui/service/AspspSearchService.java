package de.adorsys.xs2a.adapter.ui.service;

import de.adorsys.xs2a.adapter.model.AspspTO;
import de.adorsys.xs2a.adapter.remote.api.AspspClient;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AspspSearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AspspSearchService.class);

    private static final int MAX_SIZE_OF_RESULT_SET = 10;

    private final AspspClient aspspClient;

    public AspspSearchService(AspspClient aspspClient) {
        this.aspspClient = aspspClient;
    }

    public List<AspspTO> getAspsps(String iban, String sessionId) {
        LOGGER.info("{}: search ASPSPs", sessionId);

        ResponseEntity<List<AspspTO>> response;
        try {
            response = aspspClient.getAspsps(null, null, null, iban, null, MAX_SIZE_OF_RESULT_SET);
        } catch (FeignException e) {
            LOGGER.error("{}: search ASPSPs response status - {}", sessionId, e.status());
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LOGGER.info("{}: search ASPSPs response status - {}", sessionId, response.getStatusCodeValue());

        List<AspspTO> aspsps = response.getBody();

        if (aspsps == null || aspsps.isEmpty()) {
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        return aspsps;
    }
}
