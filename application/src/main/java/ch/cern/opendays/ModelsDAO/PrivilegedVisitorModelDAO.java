//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PRIVILEGED_VISIT")
public class PrivilegedVisitorModelDAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRIVILEGE", updatable = false, nullable = false)
    private Integer idPrivilege;

    @NotNull
    @Column(name = "PRIVILEGE_VISITOR_IDENTIFIER")
    private String privilageVisitorIdentifier;

    @NotNull
    @Column(name = "PRIVILEGE_DAY")
    private LocalDateTime privilegeDay;

    @NotNull
    @Column(name = "PRIVILEGE_TYPE_CODE")
    private Integer privilageTypeCode;

    public PrivilegedVisitorModelDAO() {
    }

    public Integer getIdPrivilege() {
        return idPrivilege;
    }

    public String getPrivilageVisitorIdentifier() {
        return privilageVisitorIdentifier;
    }

    public LocalDateTime getPrivilegeDay() {
        return privilegeDay;
    }

    public Integer getPrivilageTypeCode() {
        return privilageTypeCode;
    }

    public PrivilegedVisitorModelDAO setIdPrivilege(Integer idPrivilege) {
        this.idPrivilege = idPrivilege;
        return this;
    }

    public PrivilegedVisitorModelDAO setPrivilageVisitorIdentifier(String privilageVisitorIdentifier) {
        this.privilageVisitorIdentifier = privilageVisitorIdentifier;
        return this;
    }

    public PrivilegedVisitorModelDAO setPrivilegeDay(LocalDateTime privilegeDay) {
        this.privilegeDay = privilegeDay;
        return this;
    }

    public PrivilegedVisitorModelDAO setPrivilageTypeCode(Integer privilageTypeCode) {
        this.privilageTypeCode = privilageTypeCode;
        return this;
    }



}
