//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import ch.cern.opendays.Enums.ObjectIds;
import ch.cern.opendays.ModelsDAO.RegisteredPersonModelDAO;
import java.time.LocalDateTime;
import java.util.UUID;

public class ActionHistoryModel {

    public Integer idAction;
    public UUID idModifier;
    public String idChangeObject;
    public LocalDateTime changeDate;
    public String changeComment;
    public Integer objectType;

    public ActionHistoryModel() {
    }

    public ActionHistoryModel(RegisteredPersonModelDAO registeredPerson) {
        this.idModifier = registeredPerson.getIdPerson();
        this.changeDate = LocalDateTime.now();
        this.idChangeObject = registeredPerson.getIdRegistration().toString();
        this.objectType = ObjectIds.REGISTRATION_OBJECT.getObjectId();
    }

    public ActionHistoryModel setIdAction(Integer idAction) {
        this.idAction = idAction;
        return this;
    }

    public ActionHistoryModel setIdModifier(UUID idModifier) {
        this.idModifier = idModifier;
        return this;
    }

    public ActionHistoryModel setIdChangeObject(String idChangeObject) {
        this.idChangeObject = idChangeObject;
        return this;
    }

    public ActionHistoryModel setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
        return this;
    }

    public ActionHistoryModel setChangeComment(String changeComment) {
        this.changeComment = changeComment;
        return this;
    }

    public ActionHistoryModel setObjectType(Integer objectType) {
        this.objectType = objectType;
        return this;
    }
}
