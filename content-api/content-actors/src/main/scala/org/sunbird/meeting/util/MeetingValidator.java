package org.sunbird.meeting.util;

import org.sunbird.common.exception.ClientException;
import org.sunbird.common.exception.ResponseCode;

import java.util.List;
import java.util.Map;

public class MeetingValidator {
    public static void validateRequest(Map<String, Object> request, List<String> fields) {
        for (String field:fields) {
            if (request.get(field) == null || request.get(field).toString().isBlank()) {
                throw new ClientException(ResponseCode.CLIENT_ERROR.name(), "Missing info for " + field);
            }
        }
    }
}
