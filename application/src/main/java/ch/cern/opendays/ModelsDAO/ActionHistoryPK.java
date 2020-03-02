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

public class ActionHistoryPK implements Serializable {

    private Integer idAction;
    private UUID idModifier;
    private String idChangeObject;
    private LocalDateTime changeDate;
    private Integer objectType;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.idAction);
        hash = 17 * hash + Objects.hashCode(this.idModifier);
        hash = 17 * hash + Objects.hashCode(this.idChangeObject);
        hash = 17 * hash + Objects.hashCode(this.changeDate);
        hash = 17 * hash + Objects.hashCode(this.objectType);
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
        final ActionHistoryPK other = (ActionHistoryPK) obj;
        if (!Objects.equals(this.idModifier, other.idModifier)) {
            return false;
        }
        if (!Objects.equals(this.idChangeObject, other.idChangeObject)) {
            return false;
        }
        if (!Objects.equals(this.idAction, other.idAction)) {
            return false;
        }
        if (!Objects.equals(this.changeDate, other.changeDate)) {
            return false;
        }
        if (!Objects.equals(this.objectType, other.objectType)) {
            return false;
        }
        return true;
    }

    public ActionHistoryPK() {
    }

    public Integer getIdAction() {
        return idAction;
    }

    public void setIdAction(Integer idAction) {
        this.idAction = idAction;
    }

    public UUID getIdModifier() {
        return idModifier;
    }

    public void setIdModifier(UUID idModifier) {
        this.idModifier = idModifier;
    }

    public String getIdChangeObject() {
        return idChangeObject;
    }

    public void setIdChangeObject(String idChangeObject) {
        this.idChangeObject = idChangeObject;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public Integer getObjectType() {
        return objectType;
    }

    public void setObjectType(Integer objectType) {
        this.objectType = objectType;
    }

}
