//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.WorkflowConstants;
import ch.cern.opendays.Enums.MailActionCodes;
import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Enums.ReservationStatusCodes;
import ch.cern.opendays.Exceptions.DatabaseException;
import ch.cern.opendays.InterfacesDAO.MailActionHistoryRepository;
import ch.cern.opendays.Models.MailActionModel;
import ch.cern.opendays.ModelsDAO.MailActionHistoryModelDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailActionHistoryServiceDB implements MailActionHistoryServiceInterface {

    private MailActionHistoryRepository mailRepository;
    private static final Logger logger = LogManager.getLogger(MailActionHistoryServiceDB.class);

    @Autowired
    public void setMailRepository(MailActionHistoryRepository mailRepository) {
        this.mailRepository = mailRepository;
    }

    @Override
    public void reuqestPasscodeMail(Long idRegistration) throws DatabaseException {

        MailActionModel mailAction = new MailActionModel()
                .setIdRegistration(idRegistration)
                .setActionType(MailActionCodes.LOGIN_PASSCODE.getMailActionCode())
                .setIdReservation(Long.parseLong(WorkflowConstants.ZERO_STRING));

        try {
            this.storeNewMailRequest(mailAction);
        } catch (DatabaseException ex) {
            logger.error(String.format("Failed to create new mail record for passcode request for this registration: %1$d", idRegistration));
            throw ex.setErrorCode(MessageStatusCodes.PASSCODE_CREATION_ERROR.getStatusCode());
        }

    }

    private void storeNewMailRequest(MailActionModel newMailAction) throws DatabaseException {
        MailActionHistoryModelDAO storeObject = new MailActionHistoryModelDAO(newMailAction);

        try {
            this.mailRepository.save(storeObject);
        } catch (Exception ex) {
            logger.error(String.format("Failed to create mail record with these informations: %1$s", storeObject.toString()), ex);
            throw new DatabaseException(String.format("Failed to create mail record with these informations: %1$s", storeObject.toString()), ex);
        }

    }

    @Override
    public void generateReservationFeedbackMail(Long idRegistration, Long idReservation, Integer feedbackReason) throws DatabaseException {
        Integer mailActionType = (feedbackReason == ReservationStatusCodes.FINAL.getReservationStatusCode()) ? MailActionCodes.RESERVATION_FINISHED.getMailActionCode() : MailActionCodes.RESERVATION_CANCELLED.getMailActionCode();
        this.insertReservationMailAction(idRegistration, idReservation, mailActionType);
    }

    @Override
    public void insertReservationMailAction(Long idRegistration, Long idReservation, Integer mailActionType) throws DatabaseException {

        MailActionModel mailAction = new MailActionModel(idRegistration, idReservation, mailActionType);

        this.storeNewMailRequest(mailAction);
    }

}
