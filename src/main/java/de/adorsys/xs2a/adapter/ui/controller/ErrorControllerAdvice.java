package de.adorsys.xs2a.adapter.ui.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpSession;

@ControllerAdvice
class ErrorControllerAdvice {

    @ExceptionHandler(Exception.class)
    String anyException(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/thank-you";
    }
}
