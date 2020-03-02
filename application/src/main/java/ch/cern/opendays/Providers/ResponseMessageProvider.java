//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Providers;

import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Models.MessageResponseAPIModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResponseMessageProvider {

    private static final Logger logger = LogManager.getLogger(ResponseMessageProvider.class);

    public static <T> MessageResponseAPIModel<T> returnDataMessage(T data, int messageStatusCode, String messageLanguage) {
        MessageResponseAPIModel<T> responseMessage = new MessageResponseAPIModel<>();
        responseMessage.messageContent = data;
        responseMessage.statusCode = messageStatusCode;

        return responseMessage;
    }

    // T data type is required for the error, because the Ok return type should be the same as the Error
    public static <T> MessageResponseAPIModel<T> returnErrorMessage(T data, int errorStatusCode, String messageLanguage) {

        MessageResponseAPIModel<T> errorResponseMessage = new MessageResponseAPIModel<>();

        // if there is no error message defined on the selected language call with no error message specified
        try {
            errorResponseMessage.errorMessage = ErrorMessageTextProvider.getErrorMessageText(errorStatusCode, messageLanguage);
        } catch (Exception ex) {
            logger.error("Failed to map error message", ex);
            // if error message is not defined in the language files
            return ResponseMessageProvider.returnErrorMessage(data, MessageStatusCodes.ERROR_MESSAGE_NOT_DEFINED.getStatusCode(), messageLanguage);
        }

        errorResponseMessage.statusCode = errorStatusCode;
        // we simply pass an empty object back in the data content
        errorResponseMessage.messageContent = data;

        return errorResponseMessage;
    }
}
