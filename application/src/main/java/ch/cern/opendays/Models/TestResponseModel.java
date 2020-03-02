//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestResponseModel {
    @JsonProperty("requestMailAddress")
    public String mailAddress;
    @JsonProperty("requestName")
    public String name;
    @JsonProperty("requestNumberOfTickets")
    public int numberOfTickets;
    @JsonProperty("processingHostname")
    public String hostName;
    @JsonProperty("processingHostIP")
    public String addressIP;

    public TestResponseModel(TestCallModel testCall) {
        this.mailAddress = testCall.mailAddress;
        this.name = testCall.name;
        this.numberOfTickets = testCall.numberOfTickets;

        try {
            InetAddress serverIdentifier = InetAddress.getLocalHost();
            this.addressIP = serverIdentifier.getHostAddress();
            this.hostName = serverIdentifier.getHostName();

        } catch (UnknownHostException e) {

            this.addressIP = "== There were some errors to get the IP==";
            this.hostName = "== There were some errors to get the hostname==";
        }


    }
}
