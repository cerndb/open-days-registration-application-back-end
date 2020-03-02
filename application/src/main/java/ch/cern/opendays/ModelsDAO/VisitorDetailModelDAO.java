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
@IdClass(VisitorDetailPK.class)
@Table(name = "VISITOR_DETAIL")
public class VisitorDetailModelDAO implements Serializable {

    @Id
    @Column(name = "ID_VISITOR")
    private Integer idVisitor;

    @Id
    @Column(name = "ID_RESERVATION")
    private Long idReservation;

    @Column(name = "REQUESTED_FAST_TRACK")
    private boolean requestedFastTrack;

    @NotNull
    @Column(name = "AGE")
    private Integer age;

    public Integer getIdVisitor() {
        return idVisitor;
    }

    public VisitorDetailModelDAO setIdVisitor(Integer idVisitor) {
        this.idVisitor = idVisitor;
        return this;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public VisitorDetailModelDAO setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
        return this;
    }

    public boolean getRequestedFastTrack() {
        return requestedFastTrack;
    }

    public VisitorDetailModelDAO setRequestedFastTrack(boolean requestedFastTrack) {
        this.requestedFastTrack = requestedFastTrack;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public VisitorDetailModelDAO setAge(Integer age) {
        this.age = age;
        return this;
    }
}
