//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "TRANSPORT_TYPE")
public class TransportTypeModelDAO implements Serializable {

    @Id
    @Column(name = "ID_TRANSPORT_TYPE")
    private Integer idTransportType;

    @NotNull
    @Column(name = "TRANSPORT_NAME_EN")
    private String transportNameEN;

    @NotNull
    @Column(name = "TRANSPORT_NAME_FR")
    private String transportNameFR;

    public TransportTypeModelDAO() {
    }

    public Integer getIdTransportType() {
        return idTransportType;
    }

    public TransportTypeModelDAO setIdTransportType(Integer idTransportType) {
        this.idTransportType = idTransportType;
        return this;
    }

    public String getTransportNameEN() {
        return transportNameEN;
    }

    public TransportTypeModelDAO setTransportNameEN(String transportNameEN) {
        this.transportNameEN = transportNameEN;
        return this;
    }

    public String getTransportNameFR() {
        return transportNameFR;
    }

    public TransportTypeModelDAO setTransportNameFR(String transportNameFR) {
        this.transportNameFR = transportNameFR;
        return this;
    }


}
