//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VisitorDetailModel {

    @JsonProperty("idVisitor")
    public Integer idVisitor;
    @JsonProperty("visitorAge")
    public Integer visitorAge;
    @JsonProperty("fastTrackSelected")
    public Boolean fastTrackSelected;

    public VisitorDetailModel() {
    }

    public VisitorDetailModel setIdVisitor(Integer idVisitor) {
        this.idVisitor = idVisitor;
        return this;
    }

    public VisitorDetailModel setVisitorAge(Integer visitorAge) {
        this.visitorAge = visitorAge;
        return this;
    }

    public VisitorDetailModel setFastTrackSelected(Boolean fastTrackSelected) {
        this.fastTrackSelected = fastTrackSelected;
        return this;
    }

    public Integer getIdVisitor() {
        return idVisitor;
    }

    public Integer getVisitorAge() {
        return visitorAge;
    }

    public Boolean getFastTrackSelected() {
        return fastTrackSelected;
    }

}
