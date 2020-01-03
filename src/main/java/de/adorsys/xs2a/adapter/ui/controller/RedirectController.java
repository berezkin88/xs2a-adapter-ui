package de.adorsys.xs2a.adapter.ui.controller;

import de.adorsys.xs2a.adapter.ui.service.AccountInformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class RedirectController {
    private static final Logger log = LoggerFactory.getLogger(AccountInformationService.class);
    public static final String REDIRECT_PATH = "/redirect";
    public static final String NOK_REDIRECT_PATH = "/nok-redirect";

    @GetMapping(REDIRECT_PATH)
    String redirect(HttpSession httpSession) {
        log.info("{}: redirect", httpSession.getId());
        // TODO async get transactions
        return "redirect:/thank-you";
    }

    @GetMapping(NOK_REDIRECT_PATH)
    String nokRedirect(HttpSession httpSession) {
        log.error("{}: nok redirect", httpSession.getId());
        return "redirect:/thank-you";
    }
}
