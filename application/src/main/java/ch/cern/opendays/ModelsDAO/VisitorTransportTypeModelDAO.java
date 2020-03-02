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
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@IdClass(VisitorTransportTypePK.class)
@Table(name = "VISITOR_TRANSPORT_TYPE")
public class VisitorTransportTypeModelDAO implements Serializable {

    @Id
    @Column(name = "ID_RESERVATION")
    private Long idReservation;

    @Id
    @Column(name = "ID_TRANSPORT_TYPE")
    private Integer idTransportType;

    public VisitorTransportTypeModelDAO() {
    }

    @OneToOne
    @JoinColumn(
            name = "ID_TRANSPORT_TYPE",
            referencedColumnName = "ID_TRANSPORT_TYPE",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "FK_TRANSPORT_TYPE")
    )
    private TransportTypeModelDAO transportType;

    public Long getIdReservation() {
        return idReservation;
    }

    public VisitorTransportTypeModelDAO setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
        return this;
    }

    public Integer getIdTransportType() {
        return idTransportType;
    }

    public VisitorTransportTypeModelDAO setIdTransportType(Integer idTransportType) {
        this.idTransportType = idTransportType;
        return this;
    }

    public TransportTypeModelDAO getTransportType() {
        return transportType;
    }

    public VisitorTransportTypeModelDAO setTransportType(TransportTypeModelDAO transportType) {
        this.transportType = transportType;
        return this;
    }
}
