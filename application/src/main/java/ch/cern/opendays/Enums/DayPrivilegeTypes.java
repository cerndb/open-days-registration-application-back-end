//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Enums;

public enum DayPrivilegeTypes {
    IS_PRIVILEGED(1),
    IS_NORMAL(0);

    private final int privilegeTypeCode;

    private DayPrivilegeTypes(int privilegeTypeCode) {
        this.privilegeTypeCode = privilegeTypeCode;
    }

    public int getPrivilegeTypeCode() {
        return privilegeTypeCode;
    }
}
