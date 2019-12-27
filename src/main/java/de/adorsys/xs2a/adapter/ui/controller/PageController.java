package de.adorsys.xs2a.adapter.ui.controller;

import de.adorsys.xs2a.adapter.ui.model.PsuData;
import de.adorsys.xs2a.adapter.ui.service.validator.PsuDataValidator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;

@Controller
public class PageController {
    private static final String IBAN_SESSION_ATTRIBUTE = "iban";
    private static final String PSU_ID_SESSION_ATTRIBUTE = "psuId";
    private static final String DATE_FROM_SESSION_ATTRIBUTE = "dateFrom";
    private static final String DATE_TO_SESSION_ATTRIBUTE = "dateTo";

    private final PsuDataValidator psuDataValidator;

    public PageController(PsuDataValidator psuDataValidator) {
        this.psuDataValidator = psuDataValidator;
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
}
