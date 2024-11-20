package com.trmo.floracare.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;
    @Value("${google.client-id}")
    private String CLIENT_ID;

    public GoogleTokenVerifierService() throws Exception {
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    public Optional<GoogleIdToken.Payload> verify(String idTokenString) {
        try {
            log.info(idTokenString);
            GoogleIdToken idToken = verifier.verify(idTokenString);
            log.info(String.valueOf(idToken));
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                return Optional.of(payload);
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
