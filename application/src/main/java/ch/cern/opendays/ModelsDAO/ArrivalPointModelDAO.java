//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.ModelsDAO;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ARRIVAL_POINT")
public class ArrivalPointModelDAO implements Serializable {
    @Id
    @Column(name = "ID_ARRIVAL_POINT")
    private Integer idArrivalPoint;

    @NotNull
    @Column(name = "POINT_NAME")
    private String pointName;

    @Column(name = "POINT_DESCRIPTION_EN")
    private String pointDescriptionEN;

    @Column(name = "POINT_DESCRIPTION_FR")
    private String pointDescriptionFR;

    @Column(name = "POINT_MAP_URL")
    private String pointMapURL;

    @NotNull
    @Column(name = "SITE_ACTIVITIES_INFO_URL_EN")
    private String siteActivitiesInfoURL_EN;

    @NotNull
    @Column(name = "SITE_ACTIVITIES_INFO_URL_FR")
    private String siteActivitiesInfoURL_FR;

    @NotNull
    @Column(name = "SITE_ACCESSIBILITY_INFO_URL_EN")
    private String accessibilityInfoURL_EN;

    @NotNull
    @Column(name = "SITE_ACCESSIBILITY_INFO_URL_FR")
    private String accessibilityInfoURL_FR;

    public ArrivalPointModelDAO() {
    }

    public Integer getIdArrivalPoint() {
        return idArrivalPoint;
    }

    public void setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getPointMapURL() {
        return pointMapURL;
    }

    public void setPointMapURL(String pointMapURL) {
        this.pointMapURL = pointMapURL;
    }

    public String getSiteActivitiesInfoURL_EN() {
        return siteActivitiesInfoURL_EN;
    }

    public void setSiteActivitiesInfoURL_EN(String siteActivitiesInfoURL_EN) {
        this.siteActivitiesInfoURL_EN = siteActivitiesInfoURL_EN;
    }

    public String getSiteActivitiesInfoURL_FR() {
        return siteActivitiesInfoURL_FR;
    }

    public void setSiteActivitiesInfoURL_FR(String siteActivitiesInfoURL_FR) {
        this.siteActivitiesInfoURL_FR = siteActivitiesInfoURL_FR;
    }

    public String getAccessibilityInfoURL_EN() {
        return accessibilityInfoURL_EN;
    }

    public void setAccessibilityInfoURL_EN(String accessibilityInfoURL_EN) {
        this.accessibilityInfoURL_EN = accessibilityInfoURL_EN;
    }

    public String getAccessibilityInfoURL_FR() {
        return accessibilityInfoURL_FR;
    }

    public void setAccessibilityInfoURL_FR(String accessibilityInfoURL_FR) {
        this.accessibilityInfoURL_FR = accessibilityInfoURL_FR;
    }

    public String getPointDescriptionEN() {
        return pointDescriptionEN;
    }

    public void setPointDescriptionEN(String pointDescriptionEN) {
        this.pointDescriptionEN = pointDescriptionEN;
    }

    public String getPointDescriptionFR() {
        return pointDescriptionFR;
    }

    public void setPointDescriptionFR(String pointDescriptionFR) {
        this.pointDescriptionFR = pointDescriptionFR;
    }

}
