//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransportTypeModel {

    @JsonProperty("displayName")
    public String displayName;

    @JsonProperty("value")
    public boolean value;

    @JsonProperty("idTransportType")
    public Integer idTransportType;

    public TransportTypeModel() {
    }

    public TransportTypeModel setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public TransportTypeModel setValue(boolean value) {
        this.value = value;
        return this;
    }

    public TransportTypeModel setIdTransportType(Integer idTransportType) {
        this.idTransportType = idTransportType;
        return this;
    }

}
