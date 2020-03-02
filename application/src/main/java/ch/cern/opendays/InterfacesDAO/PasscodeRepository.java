//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.InterfacesDAO;

import ch.cern.opendays.ModelsDAO.PasscodeModelDAO;
import ch.cern.opendays.ModelsDAO.PasscodePK;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PasscodeRepository extends CrudRepository<PasscodeModelDAO, PasscodePK> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE PASSCODE passcode SET passcode.PASSCODE_IS_ACTIVE = :updateStatus WHERE passcode.ID_REGISTRATION = :idRegistration", nativeQuery = true)
    int updatePasscodeSetPasscodeStatus(@Param("updateStatus") Integer updateStatus, @Param("idRegistration") Long idRegistration);

    @Query(value = "SELECT * FROM PASSCODE passcode WHERE passcode.ID_REGISTRATION = :idRegistration AND passcode.PASSCODE_IS_ACTIVE = :passcodeStatus", nativeQuery = true)
    public List<PasscodeModelDAO> getPasscodeByRegistrationIdAndStatus(@Param("idRegistration") Long idRegistration, @Param("passcodeStatus") Integer passcodeStatus);

}
