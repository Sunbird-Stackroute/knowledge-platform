package org.sunbird.meeting.constant;

import org.sunbird.common.Platform;
import org.sunbird.meeting.util.MeetingUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeetingConstants {
    public final static String CREATE_MEETING = "createMeeting";
    public final static String GET_MEETING = "getMeeting";
    public final static String GENERATE_MEETING_SIGNATURE = "generateSignature";
    public final static String API_ENDPOINT = Platform.getString("meeting.api.endpoint", "");
    public final static String HOST_EMAIL = Platform.getString("meeting.host.email", "");
    public final static String CREATE_API_ENDPOINT = API_ENDPOINT + "v2/users/" + HOST_EMAIL + "/meetings";
    public final static String GET_MEETING_API_ENDPOINT = API_ENDPOINT + "v2/meetings/";
    public final static String API_KEY = Platform.getString("meeting.api.key", "");
    public final static String API_SECRET = Platform.getString("meeting.api.secret", "");

    public final static String KEYSPACE = "meeting_store";
    public final static String TABLE = "meeting_details";
    public final static List<String> PRIMARY_KEY = Arrays.asList(MeetingConstants.COLUMNS.ID, MeetingConstants.COLUMNS.HOST_ID, MeetingConstants.COLUMNS.CREATED_AT);
    public final static String MEETING_ID = "meetingId";
    public final static String ROLE = "role";
    public final static String SIGNATURE = "signature";

    public static Map<String, String> getApiHeaders() {
        return new HashMap<>() {{
            put("content-type", "application/json");
            put("authorization", "Bearer " + MeetingUtil.getToken(API_KEY, API_SECRET));
        }};
    }

    public static class TYPES {
        public final static String TEXT = "text";
        public final static String MAP = "map";
        public final static String TIMESTAMP = "timestamp";
        public final static String LIST = "list";
        public final static String BIGINT = "bigint";
        public final static String INT = "int";
    }

    public static class COLUMNS {
        public final static String ID = "id";
        public final static String HOST_ID = "host_id";
        public final static String CREATED_AT = "created_at";
        public final static String AGENDA = "agenda";
        public final static String ASSISTANT_ID = "assistant_id";
        public final static String CONTENT_ID = "content_id";
        public final static String CREATED_BY = "created_by";
        public final static String DURATION = "duration";
        public final static String ENCRYPTED_PASSWORD = "encrypted_password";
        public final static String H_323_PASSWORD = "h323_password";
        public final static String HOST_EMAIL = "host_email";
        public final static String JOIN_URL = "join_url";
        public final static String OCCURRENCES = "occurrences";
        public final static String PASSWORD = "password";
        public final static String PMI = "pmi";
        public final static String PSTN_PASSWORD = "pstn_password";
        public final static String RECURRENCE = "recurrence";
        public final static String REGISTRATION_URL = "registration_url";
        public final static String SCHEDULE_FOR = "schedule_for";
        public final static String SETTINGS = "settings";
        public final static String START_TIME = "start_time";
        public final static String START_URL = "start_url";
        public final static String STATUS = "status";
        public final static String TIMEZONE = "timezone";
        public final static String TOPIC = "topic";
        public final static String TRACKING_FIELDS = "tracking_fields";
        public final static String TYPE = "type";
        public final static String UPDATED_AT = "updated_at";
        public final static String UUID = "uuid";
    }
}
