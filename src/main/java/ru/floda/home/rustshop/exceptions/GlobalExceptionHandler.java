package ru.floda.home.rustshop.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserException(UserNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        model.addAttribute("statusCode", HttpStatus.NOT_FOUND.value());
        return "404";
    }
}
