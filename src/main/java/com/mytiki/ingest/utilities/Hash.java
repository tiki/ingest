package com.mytiki.ingest.utilities;

import com.mytiki.common.exception.ApiExceptionFactory;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    public static byte[] sha256(String plaintext) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        return digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));
    }
}
