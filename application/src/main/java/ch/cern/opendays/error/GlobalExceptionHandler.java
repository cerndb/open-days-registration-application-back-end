//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.error;

import ch.cern.opendays.Constants.EnvironmentConfigConstants;
import static ch.cern.opendays.Enums.MessageStatusCodes.EMPTY_API_INPUT;
import static ch.cern.opendays.Enums.MessageStatusCodes.INCORRECT_API_INPUT;
import ch.cern.opendays.Providers.ErrorMessageTextProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception excptn, Object o, HttpHeaders hh, HttpStatus hs, WebRequest wr) {
        return super.handleExceptionInternal(excptn, o, hh, hs, wr); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException hmtnse, HttpHeaders hh, HttpStatus hs, WebRequest wr) {
        String responseLanguage = (wr.getLocale().toString() != null) ? wr.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();
        String errorMessage = ErrorMessageTextProvider.getErrorMessageText(INCORRECT_API_INPUT.getStatusCode(), responseLanguage);
        return handleExceptionInternal(hmtnse, errorMessage, hh, hs.BAD_REQUEST, wr);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException hmnre, HttpHeaders hh, HttpStatus hs, WebRequest wr) {
        String responseLanguage = (wr.getLocale().toString() != null) ? wr.getLocale().toString() : EnvironmentConfigConstants.DEFAULT_MESSAGE_LANGUAGE.toString();
        String errorMessage = ErrorMessageTextProvider.getErrorMessageText(EMPTY_API_INPUT.getStatusCode(), responseLanguage);
        return handleExceptionInternal(hmnre, errorMessage, hh, hs.BAD_REQUEST, wr);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders hh, HttpStatus hs, WebRequest wr) {
        return handleExceptionInternal(ex, hs.METHOD_NOT_ALLOWED.getReasonPhrase(), hh, hs.METHOD_NOT_ALLOWED, wr);
    }
}
