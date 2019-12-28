package de.adorsys.xs2a.adapter.ui.controller;

import de.adorsys.xs2a.adapter.model.AspspTO;
import de.adorsys.xs2a.adapter.model.ConsentsResponse201TO;
import de.adorsys.xs2a.adapter.model.StartScaprocessResponseTO;
import de.adorsys.xs2a.adapter.ui.model.PsuData;
import de.adorsys.xs2a.adapter.ui.service.AccountInformationService;
import de.adorsys.xs2a.adapter.ui.service.AspspSearchService;
import de.adorsys.xs2a.adapter.ui.service.link.LinksService;
import de.adorsys.xs2a.adapter.ui.service.validator.PsuDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.List;

@Controller
public class PageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountInformationService.class);

    private static final String PSU_DATA_MODEL_ATTRIBUTE = "psuData";
    private static final String IBAN_SESSION_ATTRIBUTE = "iban";
    private static final String PSU_ID_SESSION_ATTRIBUTE = "psuId";
    private static final String DATE_FROM_SESSION_ATTRIBUTE = "dateFrom";
    private static final String DATE_TO_SESSION_ATTRIBUTE = "dateTo";
    private static final String ASPSP_ID_SESSION_ATTRIBUTE = "aspspId";
    private static final String CONSENT_ID_SESSION_ATTRIBUTE = "consentId";
    private static final String AUTHORISATION_ID_SESSION_ATTRIBUTE = "authorisationId";
    private static final String ENCRYPTED_AUTHORISATION_SESSION_ATTRIBUTE = "encryptedAuthorisation";

    private final PsuDataValidator psuDataValidator;
    private final AspspSearchService aspspSearchService;
    private final AccountInformationService accountInformationService;
    private final LinksService linksService;

    public PageController(PsuDataValidator psuDataValidator,
                          AspspSearchService aspspSearchService,
                          AccountInformationService accountInformationService,
                          LinksService linksService) {
        this.psuDataValidator = psuDataValidator;
        this.aspspSearchService = aspspSearchService;
        this.accountInformationService = accountInformationService;
        this.linksService = linksService;
    }

    @GetMapping()
    public String introPage(HttpSession session) {
        if (!session.isNew()) {
            session.invalidate();
        }

        return "intro";
    }

    @GetMapping("/page/psu-data")
    public String psuDataPage(Model model) {
        model.addAttribute(PSU_DATA_MODEL_ATTRIBUTE, new PsuData());
        return "psu_data";
    }

    @PostMapping("/page/psu-data")
    public String psuDataInput(Model model, @ModelAttribute PsuData psuData, HttpSession session) {
        model.addAttribute(PSU_DATA_MODEL_ATTRIBUTE, null);
        psuDataValidator.validatePsuData(psuData);

        session.setAttribute(IBAN_SESSION_ATTRIBUTE, psuData.getIban());
        session.setAttribute(PSU_ID_SESSION_ATTRIBUTE, psuData.getPsuId());
        session.setAttribute(DATE_FROM_SESSION_ATTRIBUTE, psuData.getDateFrom());
        session.setAttribute(DATE_TO_SESSION_ATTRIBUTE, psuData.getDateTo());

        String sessionId = session.getId();
        LOGGER.info("{}: create a new session", sessionId);

        // search for ASPSP by IBAN
        List<AspspTO> aspsps = aspspSearchService.getAspsps(psuData.getIban(), sessionId);
        // TODO discuss this behaviour with PO
        String aspspId = aspsps.get(0).getId();
        session.setAttribute(ASPSP_ID_SESSION_ATTRIBUTE, aspspId);

        // create consent
        ConsentsResponse201TO consent = accountInformationService.createConsent(psuData.getIban(), psuData.getPsuId(), aspspId, sessionId);
        session.setAttribute(CONSENT_ID_SESSION_ATTRIBUTE, consent.getConsentId());

        if (linksService.isEmbeddedApproach(consent.getLinks())) {
            return proceedEmbeddedStartAuthorisation(consent, session, psuData.getPsuId(), aspspId);
        }

        // TODO add page resolver for REDIRECT and OAUTH approaches
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

    private String proceedEmbeddedStartAuthorisation(ConsentsResponse201TO consent, HttpSession session, String psuId, String aspspId) {
        String authorisationId;
        if (linksService.authorisationNotStarted(consent.getLinks())) {
            // if ASPSP requires sending password within the start authorisation request
            if (linksService.startAuthorisationWithPsuAuthentication(consent.getLinks())) {
                // if ASPSP requires encrypted authorisation
                if (linksService.startAuthorisationEncrypted(consent.getLinks())) {
                    session.setAttribute(ENCRYPTED_AUTHORISATION_SESSION_ATTRIBUTE, "true");
                }
                return "pin";
            }

            StartScaprocessResponseTO authorisation
                    = accountInformationService.startAuthorisation(consent.getConsentId(), psuId, aspspId, session.getId());
            authorisationId = authorisation.getAuthorisationId();
        } else {
            authorisationId = linksService.getAuthorisationId(consent.getLinks());
        }
        session.setAttribute(AUTHORISATION_ID_SESSION_ATTRIBUTE, authorisationId);

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

    @GetMapping("/page/thank-you")
    public String thankYou() {
        return "thank-you";
    }
}
