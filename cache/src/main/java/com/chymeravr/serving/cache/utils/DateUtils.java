package com.chymeravr.serving.cache.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rubbal on 17/2/17.
 */
@Slf4j
public class DateUtils {
    private DateUtils() {
    }

    /**
     * @param startDate
     * @param endDate
     * @return if current time is between the start and end date (both inclusive)
     */
    public static boolean isTodayWithinDates(Date startDate, Date endDate) {
        Date currentDate = org.apache.commons.lang.time.DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        Date truncatedStartDate = org.apache.commons.lang.time.DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);
        Date truncatedEndDate = org.apache.commons.lang.time.DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH);
        return !(currentDate.before(truncatedStartDate) || currentDate.after(truncatedEndDate));
    }
}
