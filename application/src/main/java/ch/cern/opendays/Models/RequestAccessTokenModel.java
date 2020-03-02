//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestAccessTokenModel {

    @JsonIgnore
    public String contactMailAddress;
    @JsonIgnore
    public String passcode;

    @JsonProperty("firstname253589667")
    public String honeyPotFirstname;
    @JsonProperty("lastname345996689")
    public String honeyPotLastname;
    @JsonProperty("phone268459683")
    public String honeyPotPhone;

    @JsonProperty("mailAddress")
    public void setContactMailAddress(String contactMailAddress) {
        this.contactMailAddress = contactMailAddress.toLowerCase().trim();
    }

    @JsonProperty("passcode")
    public void setPasscode(String passcode) {
        this.passcode = passcode.trim();
    }

    public RequestAccessTokenModel() {
    }
}
