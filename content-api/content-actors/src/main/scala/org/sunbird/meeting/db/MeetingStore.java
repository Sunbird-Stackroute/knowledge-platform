package org.sunbird.meeting.db;

import com.datastax.driver.core.Row;
import org.sunbird.cassandra.CassandraStore;
import org.sunbird.meeting.util.MeetingUtil;
import org.sunbird.meeting.constant.MeetingConstants;

import java.util.*;

public class MeetingStore extends CassandraStore {

    public MeetingStore() {
        super(MeetingConstants.KEYSPACE, MeetingConstants.TABLE, MeetingConstants.PRIMARY_KEY);
    }

    public void insertMeeting(Map<String, Object> data) {
        Map<String, Object> filteredData = MeetingUtil.filterDataForInsertion(data);
        upsertRecord(filteredData);
    }

    public List<Row> getMeetings(Map<String, Object> properties) {
        return getRecordsByProperties(properties);
    }
}
