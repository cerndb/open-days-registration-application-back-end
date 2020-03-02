//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Enums.PrivilegeTypeCodes;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.InterfacesDAO.PrivilegedVisitorRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivilegedVisitorServiceDB implements PrivilegedVisitorServiceInterface {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(PrivilegedVisitorServiceDB.class);
    private PrivilegedVisitorRepository privilegedVisitorRepository;

    @Autowired
    public void setPrivilegedVisitorRepository(PrivilegedVisitorRepository privilegedVisitorRepository) {
        this.privilegedVisitorRepository = privilegedVisitorRepository;
    }

    @Override
    public Set<LocalDate> getPrivilegedDays(String mailAddress, Long idRegistration) throws DatabaseException {
        Set<LocalDate> privilegedDates = new HashSet<>();

        try {
            this.privilegedVisitorRepository.findAll().forEach((privilegCondition) -> {
                        // if individual and total match
                        if ((privilegCondition.getPrivilageTypeCode() == PrivilegeTypeCodes.INDIVIDUAL.getPrivilegeTypeCode())
                        && privilegCondition.getPrivilageVisitorIdentifier().equals(mailAddress)) {
                            privilegedDates.add(privilegCondition.getPrivilegeDay().toLocalDate());
                        }

                        if ((privilegCondition.getPrivilageTypeCode() == PrivilegeTypeCodes.DOMAIN.getPrivilegeTypeCode())
                        && mailAddress.contains(privilegCondition.getPrivilageVisitorIdentifier())) {
                            privilegedDates.add(privilegCondition.getPrivilegeDay().toLocalDate());
                        }

                    }
            );
        } catch (Exception ex) {
            logger.error(String.format("Failed to load privilaged days for this registration id: %1$d", idRegistration));
            throw new DatabaseException(String.format("Failed to load privilaged days for this registration id: %1$d", idRegistration), ex)
                    .setErrorCode(MessageStatusCodes.ARRIVAL_POINT_GET_AVAILABLE_DATES_LOAD_FAILED.getStatusCode());
        }

        return privilegedDates;
    }
}
