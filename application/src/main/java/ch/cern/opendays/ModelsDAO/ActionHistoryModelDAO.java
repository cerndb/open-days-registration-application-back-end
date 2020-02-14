//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import ch.cern.opendays.Models.ActionHistoryModel;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(ActionHistoryPK.class)
@Table(name = "ACTION_HISTORY")
public class ActionHistoryModelDAO implements Serializable {

    @Id
    @Column(name = "ID_ACTION")
    private Integer idAction;

    @Id
    @Column(name = "ID_MODIFIER")
    private UUID idModifier;

    @Id
    @Column(name = "ID_CHANGE_OBJECT")
    private String idChangeObject;

    @Id
    @Column(name = "CHANGE_DATE")
    private LocalDateTime changeDate;

    @Id
    @Column(name = "OBJECT_TYPE")
    private Integer objectType;

    @Column(name = "CHANGE_COMMENT")
    private String changeComment;

    public ActionHistoryModelDAO() {
    }

    public ActionHistoryModelDAO(ActionHistoryModel historyAction) {
        this.idAction = historyAction.idAction;
        this.idModifier = historyAction.idModifier;
        this.idChangeObject = historyAction.idChangeObject;
        this.changeDate = historyAction.changeDate;
        this.changeComment = historyAction.changeComment;
        this.objectType = historyAction.objectType;
    }

    @Override
    public String toString() {
        return "ActionHistoryModelDAO{" + "idAction=" + idAction + ", idModifier=" + idModifier + ", idChangeObject=" + idChangeObject + ", changeDate=" + changeDate + ", objectType=" + objectType + ", changeComment=" + changeComment + '}';
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

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeComment() {
        return changeComment;
    }

    public void setChangeComment(String changeComment) {
        this.changeComment = changeComment;
    }

    public String getIdChangeObject() {
        return idChangeObject;
    }

    public void setIdChangeObject(String idChangeObject) {
        this.idChangeObject = idChangeObject;
    }

    public Integer getObjectType() {
        return objectType;
    }

    public void setObjectType(Integer objectType) {
        this.objectType = objectType;
    }

}
