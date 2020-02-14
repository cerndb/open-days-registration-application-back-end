//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import ch.cern.opendays.Enums.IsActiveStatusCodes;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.RandomStringUtils;

@Entity
@IdClass(PasscodePK.class)
@Table(name = "PASSCODE",
        indexes = {
            @Index(name = "INDEX_PASSCODE_IS_ACTIVE", columnList = "PASSCODE_IS_ACTIVE")
        })
public class PasscodeModelDAO implements Serializable {

    @Id
    @Column(name = "ID_REGISTRATION")
    private Long idRegistration;

    @Id
    @Column(name = "TIMESTAMP_OF_CREATION")
    private LocalDateTime timestampOfCreation;

    @Id
    @Column(name = "ID_OPERATION")
    private Integer idOperation;

    @Column(name = "PASSCODE")
    @NotNull
    private String passcode;

    @Column(name = "PASSCODE_IS_ACTIVE")
    @NotNull
    private Integer passcodeIsActive;

    @Column(name = "TIMESTAMP_OF_USAGE")
    private LocalDateTime timestampOfUsage;

    public PasscodeModelDAO() {
    }

    public PasscodeModelDAO(Long idRegistration, Integer idOperation) {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = true;

        this.idOperation = idOperation;
        this.idRegistration = idRegistration;
        this.passcode = RandomStringUtils.random(length, useLetters, useNumbers);
        this.passcodeIsActive = IsActiveStatusCodes.IS_ACTIVE.getIsActiveStatusCode();
        this.timestampOfCreation = LocalDateTime.now();
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

    public Integer getIdOperation() {
        return idOperation;
    }

    public void setIdOperation(Integer idOperation) {
        this.idOperation = idOperation;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public LocalDateTime getTimestampOfUsage() {
        return timestampOfUsage;
    }

    public void setTimestampOfUsage(LocalDateTime timestampOfUsage) {
        this.timestampOfUsage = timestampOfUsage;
    }

    public Integer getPasscodeIsActive() {
        return passcodeIsActive;
    }

    public void setPasscodeIsActive(Integer passcodeIsActive) {
        this.passcodeIsActive = passcodeIsActive;
    }
}
