//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Exceptions;

public class ActionHistoryException extends Exception {

    private int errorCode;

    public ActionHistoryException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public ActionHistoryException setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

}
