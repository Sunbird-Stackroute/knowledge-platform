package org.sunbird.meeting.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.sunbird.common.DateUtils;
import org.sunbird.common.JsonUtils;
import org.sunbird.meeting.constant.MeetingConstants.COLUMNS;
import org.sunbird.meeting.constant.MeetingConstants.TYPES;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class MeetingUtil {

    final static List<String> allowedFieldsInResponse;

    final static Map<String, String> columnTypes;

    static {
        allowedFieldsInResponse = new ArrayList<>() {{
            add(COLUMNS.ID);
            add(COLUMNS.HOST_ID);
            add(COLUMNS.CREATED_AT);
            add(COLUMNS.AGENDA);
            add(COLUMNS.ASSISTANT_ID);
            add(COLUMNS.CONTENT_ID);
            add(COLUMNS.CREATED_BY);
            add(COLUMNS.DURATION);
            add(COLUMNS.HOST_EMAIL);
            add(COLUMNS.OCCURRENCES);
            add(COLUMNS.PMI);
            add(COLUMNS.RECURRENCE);
            add(COLUMNS.SCHEDULE_FOR);
            add(COLUMNS.SETTINGS);
            add(COLUMNS.START_TIME);
            add(COLUMNS.STATUS);
            add(COLUMNS.TIMEZONE);
            add(COLUMNS.TOPIC);
            add(COLUMNS.TRACKING_FIELDS);
            add(COLUMNS.TYPE);
            add(COLUMNS.UPDATED_AT);
            add(COLUMNS.UUID);
        }};

        columnTypes = new HashMap<>() {{
            put(COLUMNS.ID, TYPES.BIGINT);
            put(COLUMNS.HOST_ID, TYPES.TEXT);
            put(COLUMNS.CREATED_AT, TYPES.TIMESTAMP);
            put(COLUMNS.AGENDA, TYPES.TEXT);
            put(COLUMNS.ASSISTANT_ID, TYPES.TEXT);
            put(COLUMNS.CONTENT_ID, TYPES.TEXT);
            put(COLUMNS.CREATED_BY, TYPES.TEXT);
            put(COLUMNS.DURATION, TYPES.INT);
            put(COLUMNS.ENCRYPTED_PASSWORD, TYPES.TEXT);
            put(COLUMNS.H_323_PASSWORD, TYPES.TEXT);
            put(COLUMNS.HOST_EMAIL, TYPES.TEXT);
            put(COLUMNS.JOIN_URL, TYPES.TEXT);
            put(COLUMNS.OCCURRENCES, TYPES.LIST);
            put(COLUMNS.PASSWORD, TYPES.TEXT);
            put(COLUMNS.PMI, TYPES.INT);
            put(COLUMNS.PSTN_PASSWORD, TYPES.TEXT);
            put(COLUMNS.RECURRENCE, TYPES.MAP);
            put(COLUMNS.REGISTRATION_URL, TYPES.TEXT);
            put(COLUMNS.SCHEDULE_FOR, TYPES.TEXT);
            put(COLUMNS.SETTINGS, TYPES.MAP);
            put(COLUMNS.START_TIME, TYPES.TIMESTAMP);
            put(COLUMNS.START_URL, TYPES.TEXT);
            put(COLUMNS.STATUS, TYPES.TEXT);
            put(COLUMNS.TIMEZONE, TYPES.TEXT);
            put(COLUMNS.TOPIC, TYPES.TEXT);
            put(COLUMNS.TRACKING_FIELDS, TYPES.LIST);
            put(COLUMNS.TYPE, TYPES.INT);
            put(COLUMNS.UPDATED_AT, TYPES.TIMESTAMP);
            put(COLUMNS.UUID, TYPES.TEXT);
        }};
    }

    public static Map<String, Object> filterDataForInsertion(Map<String, Object> data) {
        return data.entrySet().stream()
                .peek(entry -> {
                    if (columnTypes.containsKey(entry.getKey())) {
                        String columnType = columnTypes.get(entry.getKey());
                        try {
                            if (columnType.equals(TYPES.LIST) || columnType.equals(TYPES.MAP)) {
                                entry.setValue(JsonUtils.serialize(entry.getValue()));
                            } else if (columnType.equals(TYPES.TIMESTAMP) && entry.getValue() != null && !(entry.getValue() instanceof Date)) {
                                entry.setValue(DateUtils.parseISOFormattedDate(entry.getValue().toString()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            entry.setValue(null);
                        }
                    }
                }).filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Object> filterDataForResponse(Map<String, Object> data) {
        return data.entrySet().stream().filter(entry -> allowedFieldsInResponse.contains(entry.getKey())).peek(entry -> {
            if (columnTypes.containsKey(entry.getKey())) {
                String columnDataType = columnTypes.get(entry.getKey());
                try {
                    if (columnDataType.equals(TYPES.LIST) && entry.getValue() instanceof String) {
                        entry.setValue(JsonUtils.deserialize(entry.getValue().toString(), List.class));
                    } else if (columnDataType.equals(TYPES.MAP) && entry.getValue() instanceof String) {
                        entry.setValue(JsonUtils.deserialize(entry.getValue().toString(), Map.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static String getToken(String apiKey, String apiSecret) {
        Algorithm algorithm = Algorithm.HMAC256(apiSecret);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 15);
        return JWT.create()
                .withExpiresAt(calendar.getTime())
                .withIssuedAt(new Date())
                .withIssuer(apiKey)
                .sign(algorithm);
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
