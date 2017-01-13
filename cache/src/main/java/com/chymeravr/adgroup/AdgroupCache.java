package com.chymeravr.adgroup;

import com.google.common.util.concurrent.AbstractScheduledService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static com.chymeravr.serving_dao.Tables.ADVERTISER_ADGROUP;

/**
 * Created by rubbal on 12/1/17.
 */
public class AdgroupCache extends AbstractScheduledService {
    public static void main(String[] args) {
        AdgroupCache cache = new AdgroupCache();
        cache.startAsync();
    }

    public AdgroupCache() {

    }

    @Override
    protected void runOneIteration() throws Exception {
        load();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 5, TimeUnit.SECONDS);
    }

    private void load() throws SQLException {
        String userName = "ciportaluser";
        String password = "ciportal";
        String url = "jdbc:postgresql://13.93.217.168:5432/ciportal";
        try (Connection conn = DriverManager.getConnection(url, userName, password);
        ) {
            DSLContext create = DSL.using(conn);
            Result<Record> results = create.select().from(ADVERTISER_ADGROUP).fetch();
            for (Record record : results) {
                System.out.println(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
