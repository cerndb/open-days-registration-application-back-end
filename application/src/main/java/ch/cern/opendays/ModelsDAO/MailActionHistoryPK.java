//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class MailActionHistoryPK implements Serializable {

    private Long idRegistration;
    private UUID idMail;
    private LocalDateTime timestampOfCreation;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.idRegistration);
        hash = 13 * hash + Objects.hashCode(this.idMail);
        hash = 13 * hash + Objects.hashCode(this.timestampOfCreation);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MailActionHistoryPK other = (MailActionHistoryPK) obj;
        if (!Objects.equals(this.idMail, other.idMail)) {
            return false;
        }
        if (!Objects.equals(this.idRegistration, other.idRegistration)) {
            return false;
        }
        if (!Objects.equals(this.timestampOfCreation, other.timestampOfCreation)) {
            return false;
        }
        return true;
    }

    public MailActionHistoryPK() {
    }

    public Long getIdRegistration() {
        return idRegistration;
    }

    public void setIdRegistration(Long idRegistration) {
        this.idRegistration = idRegistration;
    }

    public UUID getIdMail() {
        return idMail;
    }

    public void setIdMail(UUID idMail) {
        this.idMail = idMail;
    }

    public LocalDateTime getTimestampOfCreation() {
        return timestampOfCreation;
    }

    public void setTimestampOfCreation(LocalDateTime timestampOfCreation) {
        this.timestampOfCreation = timestampOfCreation;
    }

}
