//Copyright (C) 2019, CERN
//This software is distributed under the terms of the GNU General Public
//Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
//In applying this license, CERN does not waive the privileges and immunities
//granted to it by virtue of its status as Intergovernmental Organization
//or submit itself to any jurisdiction.
package ch.cern.opendays.Providers;

import ch.cern.opendays.Enums.MessageStatusCodes;
import ch.cern.opendays.Exceptions.TokenException;
import ch.cern.opendays.Models.TokenModel;
import ch.cern.opendays.Models.TokenStoredRegistrationInformationModel;
import ch.cern.opendays.TokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class TokenProviderJWT implements TokenProviderInteface {

    private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    private String secretKey;
    private String issuer;
    private String subject;
    private Integer expirationTime;


    public TokenProviderJWT(TokenProperties tokenProperties) {
        this.secretKey = tokenProperties.getSecretKey();
        this.issuer = tokenProperties.getIssuer();
        this.subject = tokenProperties.getSubject();
        this.expirationTime = tokenProperties.getExpirationTime();
    }

    @Override
    public TokenModel generateRegistrationWorkflowToken(TokenStoredRegistrationInformationModel registartionInformation) {
        Date now = new Date();

        // We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(getSecretKey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, getSignatureAlgorithm().getJcaName());

        // set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setId(registartionInformation.getIdRegistration().toString()) // we need to change the id
                .setIssuedAt(now)
                .setSubject(getSubject())
                .claim("idReservation", registartionInformation.getIdReservation().toString())
                .setIssuer(getIssuer())
                .signWith(getSignatureAlgorithm(), signingKey);

        // add the expiration
        if (getExpirationTime() >= 0) {
            Date expiration = DateUtils.addMinutes(now, this.expirationTime);
            builder.setExpiration(expiration);
        }

        return new TokenModel(builder.compact());
    }

    private String removeBearerFromTokenString(String token) {
        //token starts with 'Bearer '
        return (token != null) ? token.substring(7) : "";
    }

    @Override
    public TokenStoredRegistrationInformationModel getTokenStoredWorkflowInformation(String receivedToken) throws TokenException {

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(getSecretKey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, getSignatureAlgorithm().getJcaName());

        try {
            // validate JWT token
            Claims tokenClaims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(removeBearerFromTokenString(receivedToken))
                    .getBody();

            // parse data from token
            return new TokenStoredRegistrationInformationModel()
                    .setIdRegistration(Long.parseLong(tokenClaims.getId()))
                    .setIdReservation(Long.parseLong((String) tokenClaims.get("idReservation")));

        } catch (Exception ex) {
            throw new TokenException("Token is invalid", ex).setErrorCode(MessageStatusCodes.TOKEN_PARSE_ERROR.getStatusCode());
        }
    }

    @Override
    public void validateToken(String receivedToken) throws TokenException {

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(getSecretKey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, getSignatureAlgorithm().getJcaName());

        // validate JWT token
        try {
            // validate JWT token
            Claims tokenClaims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(removeBearerFromTokenString(receivedToken))
                    .getBody();
        } catch (Exception ex) {
            throw new TokenException("Token is invalid", ex).setErrorCode(MessageStatusCodes.TOKEN_PARSE_ERROR.getStatusCode());
        }
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public TokenProviderJWT setSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public TokenProviderJWT setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getIssuer() {
        return issuer;
    }

    public TokenProviderJWT setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public TokenProviderJWT setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Integer getExpirationTime() {
        return expirationTime;
    }

    public TokenProviderJWT setExpirationTime(Integer expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }
}
