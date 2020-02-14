//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.InterfacesDAO;

import ch.cern.opendays.ModelsDAO.ArrivalPointActivitiesModelDAO;
import ch.cern.opendays.ModelsDAO.ArrivalPointActivitiesPK;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ArrivalPointActivitiesRepository extends CrudRepository<ArrivalPointActivitiesModelDAO, ArrivalPointActivitiesPK> {

    @Query(value = "SELECT * FROM ARRIVAL_POINT_ACTIVITIES arrival_point_activities WHERE arrival_point_activities.VISIT_DAY = :visitDay", nativeQuery = true)
    public List<ArrivalPointActivitiesModelDAO> getArrivalPointActivities(@Param("visitDay") LocalDateTime visitDay);

}
