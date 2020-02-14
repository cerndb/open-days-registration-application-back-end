//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

public class ReservedTicketsModel {
    private Long idReservation;
    private Integer fastTrackTickets;
    private Integer standardTickets;

    public ReservedTicketsModel() {
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public ReservedTicketsModel setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
        return this;
    }

    public Integer getFastTrackTickets() {
        return fastTrackTickets;
    }

    public ReservedTicketsModel setFastTrackTickets(Integer fastTrackTickets) {
        this.fastTrackTickets = fastTrackTickets;
        return this;
    }

    public Integer getStandardTickets() {
        return standardTickets;
    }

    public ReservedTicketsModel setStandardTickets(Integer standardTickets) {
        this.standardTickets = standardTickets;
        return this;
    }

}
