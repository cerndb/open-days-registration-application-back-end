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
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ARRIVAL_POINT_TRANSPORT_RATE")
@IdClass(TransportTypeRatePK.class)
public class TransportTypeRateModelDAO implements Serializable {

    @Id
    @Column(name = "ID_TRANSPORT_TYPE")
    private Integer idTransportType;

    @Id
    @Column(name = "ID_ARRIVAL_POINT")
    private Integer idArrivalPoint;

    @NotNull
    @Column(name = "TRANSPORT_RATE")
    private Integer transportRate;


    public TransportTypeRateModelDAO() {
    }

    public Integer getIdTransportType() {
        return idTransportType;
    }

    public void setIdTransportType(Integer idTransportType) {
        this.idTransportType = idTransportType;
    }

    public Integer getIdArrivalPoint() {
        return idArrivalPoint;
    }

    public void setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
    }

    public Integer getTransportRate() {
        return transportRate;
    }

    public void setTransportRate(Integer transportRate) {
        this.transportRate = transportRate;
    }

}
