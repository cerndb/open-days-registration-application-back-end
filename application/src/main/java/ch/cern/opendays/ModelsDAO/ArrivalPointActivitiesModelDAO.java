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
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(ArrivalPointActivitiesPK.class)
@Table(name = "ARRIVAL_POINT_ACTIVITIES")
public class ArrivalPointActivitiesModelDAO implements Serializable {

    @Id
    @Column(name = "ID_ARRIVAL_POINT")
    private Integer idArrivalPoint;

    @Id
    @Column(name = "VISIT_DAY")
    private LocalDateTime visitDay;

    @Column(name = "NUMBER_OF_SURFACE_ACTIVITIES")
    private Integer numberOfSurfaceActivities;

    @Column(name = "NUMBER_OF_UNDERGROUND_ACTIVITIES")
    private Integer numberOfUndergroundActivities;

    public ArrivalPointActivitiesModelDAO() {
    }

    public Integer getIdArrivalPoint() {
        return idArrivalPoint;
    }

    public void setIdArrivalPoint(Integer idArrivalPoint) {
        this.idArrivalPoint = idArrivalPoint;
    }

    public LocalDateTime getVisitDay() {
        return visitDay;
    }

    public void setVisitDay(LocalDateTime visitDay) {
        this.visitDay = visitDay;
    }

    public Integer getNumberOfSurfaceActivities() {
        return numberOfSurfaceActivities;
    }

    public void setNumberOfSurfaceActivities(Integer numberOfSurfaceActivities) {
        this.numberOfSurfaceActivities = numberOfSurfaceActivities;
    }

    public Integer getNumberOfUndergroundActivities() {
        return numberOfUndergroundActivities;
    }

    public void setNumberOfUndergroundActivities(Integer numberOfUndergroundActivities) {
        this.numberOfUndergroundActivities = numberOfUndergroundActivities;
    }

}
