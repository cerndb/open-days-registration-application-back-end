//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Constants;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ControllerConstants {

    public static final String EMPTY_STRING = "";
    public static final Long DEFAULT_USER_ID = Long.parseLong("-666");
    public static final String DEFAULT_DATE_STRING = "1900.01.01";
    public static final String DEFAULT_TIME_STRING = "0:00";
    public static final String PRIVILEGE_DAY = "2019.09.13";
    public static final DateTimeFormatter DATETIME_FORMAT_PATTERN = DateTimeFormatter.ofPattern("yyyy.MM.dd H:mm");
    public static final DateTimeFormatter DATE_FORMAT_PATTERN = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public static final DateTimeFormatter TIME_FORMAT_PATTERN = DateTimeFormatter.ofPattern("H:mm");
    public static final DateTimeFormatter TIME_FORMAT_PATTERN_HH = DateTimeFormatter.ofPattern("HH:mm");
    public static final Integer MAX_NUMBER_OF_RESERVATION_TICKETS = 6;
    public static final DateTimeFormatter DATE_DAY_OF_WEEK_DAY_MONTH_FORMAT_FR = DateTimeFormatter.ofPattern("EEEE dd MMMM", Locale.FRENCH);
    public static final DateTimeFormatter DATE_DAY_OF_WEEK_DAY_MONTH_FORMAT_EN = DateTimeFormatter.ofPattern("EEEE, dd MMMM",Locale.ENGLISH);

}
