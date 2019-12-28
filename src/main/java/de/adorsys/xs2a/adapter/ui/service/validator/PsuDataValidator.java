package de.adorsys.xs2a.adapter.ui.service.validator;

import de.adorsys.xs2a.adapter.ui.model.PsuData;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.regex.Pattern;

@Component
public class PsuDataValidator {
    private static final String IBAN_REGEX = "^[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}?$";
    private static final Pattern IBAN_PATTERN = Pattern.compile(IBAN_REGEX);

    public void validatePsuData(PsuData psuData) {
        if (psuData == null) {
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        String iban = psuData.getIban();

        if (iban == null || iban.trim().isEmpty() || !IBAN_PATTERN.matcher(iban).find()) {
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        String psuId = psuData.getPsuId();

        if (psuId == null || psuId.trim().isEmpty()) {
            // TODO change to some more appropriate exception
            throw new RuntimeException();
        }

        LocalDate dateFrom = psuData.getDateFrom();

        if (dateFrom == null || dateFrom.isAfter(LocalDate.now())) {
            psuData.setDateFrom(LocalDate.now().minusMonths(1));
        }

        LocalDate dateTo = psuData.getDateTo();

        if (dateTo == null || psuData.getDateFrom().isAfter(dateTo)) {
            psuData.setDateTo(LocalDate.now());
        }
    }
}
