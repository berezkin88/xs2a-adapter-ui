package de.adorsys.xs2a.adapter.ui.service.builder;

import de.adorsys.xs2a.adapter.model.AccountAccessTO;
import de.adorsys.xs2a.adapter.model.AccountReferenceTO;
import de.adorsys.xs2a.adapter.model.ConsentsTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;

@Component
public class ModelBuilder {
    private static final boolean DEFAULT_COMBINED_SERVICE_INDICATOR = false;
    private static final boolean DEFAULT_RECURRING_INDICATOR = true;
    private static final LocalDate DEFAULT_VALID_UNTIL = LocalDate.now().plusDays(1);
    private static final int DEFAULT_FREQUENCY_PER_DAY = 4;

    public ConsentsTO buildConsents(String iban) {
        ConsentsTO consents = new ConsentsTO();

        consents.setCombinedServiceIndicator(DEFAULT_COMBINED_SERVICE_INDICATOR);
        consents.setRecurringIndicator(DEFAULT_RECURRING_INDICATOR);
        consents.setValidUntil(DEFAULT_VALID_UNTIL);
        consents.setFrequencyPerDay(DEFAULT_FREQUENCY_PER_DAY);
        consents.setAccess(buildAccountAccess(iban));

        return consents;
    }

    public AccountAccessTO buildAccountAccess(String iban) {
        AccountAccessTO accountAccess = new AccountAccessTO();

        accountAccess.setBalances(Collections.singletonList(buildAccountReference(iban)));
        accountAccess.setTransactions(Collections.singletonList(buildAccountReference(iban)));
        accountAccess.setAccounts(Collections.singletonList(buildAccountReference(iban)));

        return accountAccess;
    }

    public AccountReferenceTO buildAccountReference(String iban) {
        AccountReferenceTO accountReference = new AccountReferenceTO();

        accountReference.setIban(iban);

        return accountReference;
    }
}
