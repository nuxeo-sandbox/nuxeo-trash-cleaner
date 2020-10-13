/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.nuxeo.ecm.platform.cleanup.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


public final class DateUtils {

    private DateUtils() throws IllegalAccessException {
        throw new IllegalAccessException("This class cannot be instantiated.");
    }

    public static String toQueryFormat(LocalDate today) {
        return today.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static Calendar toCalendar(LocalDate today) {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth());
        return calendar;
    }

    public static Calendar toCalendar(LocalDateTime today) {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(
                today.getYear(),
                today.getMonthValue() - 1,
                today.getDayOfMonth(),
                today.getHour(),
                today.getMinute(),
                today.getSecond()
        );
        return calendar;
    }

    public static LocalDate toLocalDate(Calendar calendar) {
        return LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static LocalDateTime toLocalDateTime(Calendar calendar) {
        return LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
        );
    }

    public static boolean isAfterOrEqual(LocalDate reference, LocalDate toTest) {
        return reference.isAfter(toTest) || reference.isEqual(toTest);
    }

}
