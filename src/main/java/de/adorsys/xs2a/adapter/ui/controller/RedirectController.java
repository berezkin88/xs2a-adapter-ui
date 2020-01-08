package de.adorsys.xs2a.adapter.ui.controller;

import de.adorsys.xs2a.adapter.ui.service.AccountInformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
public class RedirectController {
    private static final Logger log = LoggerFactory.getLogger(AccountInformationService.class);
    public static final String REDIRECT_PATH = "/redirect";
    public static final String NOK_REDIRECT_PATH = "/nok-redirect";
    private static final String ASPSP_ID_SESSION_ATTRIBUTE = "aspspId";
    private static final String CONSENT_ID_SESSION_ATTRIBUTE = "consentId";
    private static final String DATE_FROM_SESSION_ATTRIBUTE = "dateFrom";
    private static final String DATE_TO_SESSION_ATTRIBUTE = "dateTo";

    private final AccountInformationService service;

    public RedirectController(AccountInformationService service) {
        this.service = service;
    }


    @GetMapping(REDIRECT_PATH)
    String redirect(HttpSession httpSession) {
        String consentId = (String) httpSession.getAttribute(CONSENT_ID_SESSION_ATTRIBUTE);
        String asppsId = (String) httpSession.getAttribute(ASPSP_ID_SESSION_ATTRIBUTE);
        String session = httpSession.getId();
        LocalDate dateFrom = (LocalDate) httpSession.getAttribute(DATE_FROM_SESSION_ATTRIBUTE);
        LocalDate dateTo = (LocalDate) httpSession.getAttribute(DATE_TO_SESSION_ATTRIBUTE);

        log.info("{}: ok redirect", session);

        service.getTransactionsAsync(consentId, asppsId, session, dateFrom, dateTo);

        return "redirect:/thank-you";
    }

    @GetMapping(NOK_REDIRECT_PATH)
    String nokRedirect(HttpSession httpSession) {
        log.error("{}: nok redirect", httpSession.getId());
        return "redirect:/thank-you";
    }
}
