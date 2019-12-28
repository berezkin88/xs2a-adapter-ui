package de.adorsys.xs2a.adapter.ui.controller;

import de.adorsys.xs2a.adapter.model.AspspTO;
import de.adorsys.xs2a.adapter.model.ConsentsResponse201TO;
import de.adorsys.xs2a.adapter.ui.model.PsuData;
import de.adorsys.xs2a.adapter.ui.service.AccountInformationService;
import de.adorsys.xs2a.adapter.ui.service.AspspSearchService;
import de.adorsys.xs2a.adapter.ui.service.validator.PsuDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.List;

@Controller
public class PageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountInformationService.class);

    private static final String IBAN_SESSION_ATTRIBUTE = "iban";
    private static final String PSU_ID_SESSION_ATTRIBUTE = "psuId";
    private static final String DATE_FROM_SESSION_ATTRIBUTE = "dateFrom";
    private static final String DATE_TO_SESSION_ATTRIBUTE = "dateTo";
    private static final String ASPSP_ID_SESSION_ATTRIBUTE = "aspspId";

    private final PsuDataValidator psuDataValidator;
    private final AspspSearchService aspspSearchService;
    private final AccountInformationService accountInformationService;

    public PageController(PsuDataValidator psuDataValidator,
                          AspspSearchService aspspSearchService,
                          AccountInformationService accountInformationService) {
        this.psuDataValidator = psuDataValidator;
        this.aspspSearchService = aspspSearchService;
        this.accountInformationService = accountInformationService;
    }

    @GetMapping()
    public String introPage() {
        return "intro";
    }

    @GetMapping("/page/psu-data")
    public String psuDataPage() {
        return "psu_data";
    }

    @PostMapping("/page/psu-data")
    public String psuDataInput(@RequestBody PsuData psuData, HttpSession session) {
        psuDataValidator.validatePsuData(psuData);

        session.setAttribute(IBAN_SESSION_ATTRIBUTE, psuData.getIban());
        session.setAttribute(PSU_ID_SESSION_ATTRIBUTE, psuData.getPsuId());
        session.setAttribute(DATE_FROM_SESSION_ATTRIBUTE, psuData.getDateFrom());
        session.setAttribute(DATE_TO_SESSION_ATTRIBUTE, psuData.getDateTo());

        String sessionId = session.getId();

        LOGGER.info("{}: create a new session", sessionId);

        List<AspspTO> aspsps = aspspSearchService.getAspsps(psuData.getIban(), sessionId);
        // TODO discuss this behaviour with PO
        String aspspId = aspsps.get(0).getId();

        session.setAttribute(ASPSP_ID_SESSION_ATTRIBUTE, aspspId);

        ConsentsResponse201TO consent = accountInformationService.createConsent(psuData.getIban(), psuData.getPsuId(), aspspId, sessionId);

        // TODO authorisation flow

        // TODO change to the appropriate view
        return "";
    }

    @GetMapping("/page/pin")
    public String pin() {
        return "pin";
    }

    @PostMapping("/page/pin")
    public String pinInput(@RequestBody String pin) {

        //TODO add appropriate logic
        return "pin";
    }

    @GetMapping("/page/auth-methods")
    public String authMethod(Model model) {

        // this is a sample data for demonstration purposes only, should be replaced with an appropriate logic in further development
        Map<String, String> methods = Map.of("901","SMS-TAN","904","chipTAN comfort",
            "906", "BV AppTAN", "907", "PhotoTAN");
        model.addAttribute("methods", methods);

        return "auth-methods";
    }

    @PostMapping("/page/auth-methods")
    public String authMethodInput(@RequestBody String authMethodId) {

        //TODO add logic
        return "auth-methods";
    }

    @GetMapping("/page/otp")
    public String otp() {
        return "otp";
    }

    @PostMapping("/page/otp")
    public String otpInput(@RequestBody String otp) {

        //TODO add logic
        return "otp";
    }
}
