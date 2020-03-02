//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import ch.cern.opendays.Enums.RegistrationProcessStatusCodes;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "REGISTRATION",
        indexes = {
            @Index(
                    name = "INDEX_REGISTRATION_STATUS_DATE",
                    columnList = "REGISTRATION_PROCESS_STATUS_CODE,LAST_UPDATE_DATE")
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "UNIQUE_REGISTARTION_CONTACT_MAIL_ADDRESS",
                    columnNames = {"CONTACT_MAIL_ADDRESS"})
        })
public class RegisteredPersonModelDAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_REGISTRATION", updatable = false, nullable = false)
    private Long idRegistration;

    @Column(name = "CONTACT_MAIL_ADDRESS")
    @NotNull
    private String contactMailAddress;

    @Column(name = "REGISTRATION_PROCESS_STATUS_CODE")
    @NotNull
    private Integer registrationProcessStatusCode;

    @Column(name = "LAST_UPDATE_DATE")
    @NotNull
    private LocalDateTime lastUpdateDate;

    @Column(name = "ID_PERSON") //auto generated GUID for post analysis
    private UUID idPerson;

    @Column(name = "REDUCED_MOBILITY_INFO_REQUIRED")
    private Boolean reducedMobilityInfoRequired;

    @Column(name = "POINT_OF_ORIGIN")
    private Integer pointOfOrigin;

    public RegisteredPersonModelDAO() {
    }

    public RegisteredPersonModelDAO(String contactMailAddress) {
        this.idPerson = UUID.randomUUID();
        this.registrationProcessStatusCode = RegistrationProcessStatusCodes.INTERESTED.getStatusCode();
        this.contactMailAddress = contactMailAddress;
        this.lastUpdateDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "RegisteredPersonModelDAO{" + "idRegistration=" + idRegistration + ", contactMailAddress=" + contactMailAddress + ", registrationProcessStatusCode=" + registrationProcessStatusCode + ", lastUpdateDate=" + lastUpdateDate + ", idPerson=" + getIdPerson() + '}';
    }

    public Long getIdRegistration() {
        return idRegistration;
    }

    public void setIdRegistration(Long idRegistration) {
        this.idRegistration = idRegistration;
    }

    public String getContactMailAddress() {
        return contactMailAddress;
    }

    public void setContactMailAddress(String contactMailAddress) {
        this.contactMailAddress = contactMailAddress;
    }

    public Integer getRegistrationProcessStatusCode() {
        return registrationProcessStatusCode;
    }

    public void setRegistrationProcessStatusCode(Integer registrationProcessStatusCode) {
        this.registrationProcessStatusCode = registrationProcessStatusCode;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public UUID getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(UUID idPerson) {
        this.idPerson = idPerson;
    }

    public Boolean getReducedMobilityInfoRequired() {
        return reducedMobilityInfoRequired;
    }

    public void setReducedMobilityInfoRequired(Boolean reducedMobilityInfoRequired) {
        this.reducedMobilityInfoRequired = reducedMobilityInfoRequired;
    }

    public Integer getPointOfOrigin() {
        return pointOfOrigin;
    }

    public void setPointOfOrigin(Integer pointOfOrigin) {
        this.pointOfOrigin = pointOfOrigin;
    }

}
