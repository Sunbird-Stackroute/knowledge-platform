package org.sunbird.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class MeetingUtil {

    public static String getToken(String apiKey, String apiSecret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 30);
            return JWT.create()
                    .withExpiresAt(calendar.getTime())
                    .withIssuedAt(new Date())
                    .withIssuer(apiKey)
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            throw exception;
        }
    }

    public static String generateSignature(String apiKey, String apiSecret, String meetingNumber, Integer role) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hasher = Mac.getInstance("HmacSHA256");
        String ts = Long.toString(System.currentTimeMillis() - 30000);
        String msg = String.format("%s%s%s%d", apiKey, meetingNumber, ts, role);
        hasher.init(new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256"));
        String message = Base64.getEncoder().encodeToString(msg.getBytes());
        byte[] hash = hasher.doFinal(message.getBytes());
        String hashBase64Str = Base64.getEncoder().encodeToString(hash);
        String tmpString = String.format("%s.%s.%s.%d.%s", apiKey, meetingNumber, ts, role, hashBase64Str);
        String encodedString = Base64.getEncoder().encodeToString(tmpString.getBytes());
        return encodedString.replaceAll("\\=+$", "");
    }
}
