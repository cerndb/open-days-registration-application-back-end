//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Services;

import ch.cern.opendays.Constants.TokenConstants;
import ch.cern.opendays.Exceptions.InvalidReCaptchaException;
import ch.cern.opendays.Models.GoogleResponse;
import ch.cern.opendays.UserValidationSettings;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaServiceGoogle implements CaptchaServiceInterface {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserValidationSettings userValidationSettings;



    @Override
    public boolean validateCaptcha(String validationRequiredToken, String clientAddressIP) throws InvalidReCaptchaException {

        URI verifyUri = URI.create(String.format(TokenConstants.GOOGLE_RECAPTCHA_URL, userValidationSettings.getCaptchaServerkey(), validationRequiredToken, clientAddressIP));

        boolean requestorIsNotRobot = false;
        // validate captcha token with Google
        try {
            GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
            requestorIsNotRobot = googleResponse.isSuccess();
        } catch (Exception ex) {
            throw new InvalidReCaptchaException("Failed to get captcha infromation from Google");
        }

        if (!requestorIsNotRobot) {
            throw new InvalidReCaptchaException("reCaptcha was not successfully validated");
        }

        return requestorIsNotRobot;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Do any additional configuration here
        return builder.build();
    }



}
