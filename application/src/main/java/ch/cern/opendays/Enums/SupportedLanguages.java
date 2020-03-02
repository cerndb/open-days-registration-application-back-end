//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Enums;

public enum SupportedLanguages {
    en(1),
    fr(2);

    private final int supportedLanguage;

    private SupportedLanguages(int supportedLanguage) {
        this.supportedLanguage = supportedLanguage;
    }

    public int getSupportedLanguage() {
        return supportedLanguage;
    }

    public static SupportedLanguages getSupportedLanguage(String requestedLanguage) {

        // defult language is english
        SupportedLanguages found = en;
        for (SupportedLanguages supportedLanguage : values()) {
            if (supportedLanguage.toString().equals(requestedLanguage)) {
                found = supportedLanguage;
            }
        }

        return found;
    }

}
