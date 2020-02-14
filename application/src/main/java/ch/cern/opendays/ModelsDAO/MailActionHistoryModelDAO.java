//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import ch.cern.opendays.Enums.IsActiveStatusCodes;
import ch.cern.opendays.Models.MailActionModel;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(MailActionHistoryPK.class)
@Table(name = "MAIL_ACTION_HISTORY", indexes = {
    @Index(name = "INDEX_SENDING_MAIL_IS_REQUIRED", columnList = "SENDING_MAIL_IS_REQUIRED")
})
public class MailActionHistoryModelDAO implements Serializable {

    @Id
    @Column(name = "ID_REGISTRATION")
    private Long idRegistration;

    @Id
    @Column(name = "ID_MAIL")
    private UUID idMail;

    @Id
    @Column(name = "TIMESTAMP_OF_CREATION")
    private LocalDateTime timestampOfCreation;

    @NotNull
    @Column(name = "SENDING_MAIL_IS_REQUIRED")
    private Integer sendingMailIsRequired;

    @NotNull
    @Column(name = "ACTION_TYPE")
    private Integer actionType;

    @Column(name = "ID_RESERVATION")
    private Long idReservation;

    @Column(name = "TIMESTAMP_OF_SEND")
    private Date timestampOfSend;

    public MailActionHistoryModelDAO() {
    }

    public MailActionHistoryModelDAO(MailActionModel mailAction) {
        this.idRegistration = mailAction.idRegistration;
        this.idReservation = mailAction.idReservation;
        this.actionType = mailAction.actionType;
        this.idMail = UUID.randomUUID();
        this.timestampOfCreation = LocalDateTime.now();
        this.sendingMailIsRequired = IsActiveStatusCodes.IS_ACTIVE.getIsActiveStatusCode();
    }

    public Long getIdRegistration() {
        return idRegistration;
    }

    public void setIdRegistration(Long idRegistration) {
        this.idRegistration = idRegistration;
    }


    public LocalDateTime getTimestampOfCreation() {
        return timestampOfCreation;
    }

    public void setTimestampOfCreation(LocalDateTime timestampOfCreation) {
        this.timestampOfCreation = timestampOfCreation;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }

    public Date getTimestampOfSend() {
        return timestampOfSend;
    }

    public void setTimestampOfSend(Date timestampOfSend) {
        this.timestampOfSend = timestampOfSend;
    }

    public Integer getSendingMailIsRequired() {
        return sendingMailIsRequired;
    }

    public void setSendingMailIsRequired(Integer sendingMailIsRequired) {
        this.sendingMailIsRequired = sendingMailIsRequired;
    }

    public UUID getIdMail() {
        return idMail;
    }

    public void setIdMail(UUID idMail) {
        this.idMail = idMail;
    }
}
