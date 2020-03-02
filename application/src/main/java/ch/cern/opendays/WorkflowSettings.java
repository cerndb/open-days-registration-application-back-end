//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "workflow.properties")
public class WorkflowSettings {

    private String registrationOpeningTime;

    public WorkflowSettings() {
    }

    public String getRegistrationOpeningTime() {
        return registrationOpeningTime;
    }

    public void setRegistrationOpeningTime(String registrationOpeningTime) {
        this.registrationOpeningTime = registrationOpeningTime;
    }

}
