//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Constants;

import java.util.regex.Pattern;

public class TokenConstants {
 public static final String GOOGLE_RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";
 public static Pattern RECAPTCHA_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
}
