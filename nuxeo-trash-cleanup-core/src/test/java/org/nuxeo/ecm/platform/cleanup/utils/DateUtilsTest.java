package org.nuxeo.ecm.platform.cleanup.utils;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;


public class DateUtilsTest {

    private static final int EXPECTED_YEAR = 2019;
    private static final int YEAR_TWENTY_TWENTY = 2020;
    private static final int DAY_OF_MONTH_TWENTY = 20;
    private static final int HOUR_OF_DAY_SIXTEEN = 16;
    private static final int MINUTE_TWENTYTHREE = 23;
    private static final int SECOND_FIFTYSEVEN = 57;
    private static final int MONTH_FEB = 2;
    private static final int EXPECTED_DAY = 12;

    private static final LocalDate SOME_DAY = LocalDate.of(EXPECTED_YEAR, MONTH_FEB, EXPECTED_DAY);


    @Test
    public void todayCalendar() {
        // GIVEN
        // WHEN
        final Calendar result = DateUtils.toCalendar(SOME_DAY);
        // THEN
        assertThat(result.get(Calendar.YEAR)).isEqualTo(EXPECTED_YEAR);
        assertThat(result.get(Calendar.MONTH)).isEqualTo(Calendar.FEBRUARY);
        assertThat(result.get(Calendar.DAY_OF_MONTH)).isEqualTo(EXPECTED_DAY);
    }

    @Test
    public void todayToQueryFormat() {
        // GIVEN
        // WHEN
        final String result = DateUtils.toQueryFormat(SOME_DAY);
        // THEN
        assertThat(result).isEqualTo("2019-02-12");
    }

    @Test
    public void todayLocalDate() {
        // GIVEN
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(SOME_DAY.getYear(), Calendar.FEBRUARY, SOME_DAY.getDayOfMonth());
        // WHEN
        final LocalDate result = DateUtils.toLocalDate(calendar);
        // THEN
        assertThat(result).isEqualTo(SOME_DAY);
    }

    @Test
    public void todayLocalDateTime() {
        // GIVEN
        final LocalDateTime toTest = LocalDateTime.of(YEAR_TWENTY_TWENTY, MONTH_FEB, DAY_OF_MONTH_TWENTY, HOUR_OF_DAY_SIXTEEN, MINUTE_TWENTYTHREE, SECOND_FIFTYSEVEN);
        // WHEN
        final Calendar result = DateUtils.toCalendar(toTest);
        // THEN
        assertThat(result.get(Calendar.YEAR)).isEqualTo(YEAR_TWENTY_TWENTY);
        assertThat(result.get(Calendar.MONTH)).isEqualTo(Calendar.FEBRUARY);
        assertThat(result.get(Calendar.DAY_OF_MONTH)).isEqualTo(DAY_OF_MONTH_TWENTY);
        assertThat(result.get(Calendar.HOUR_OF_DAY)).isEqualTo(HOUR_OF_DAY_SIXTEEN);
        assertThat(result.get(Calendar.MINUTE)).isEqualTo(MINUTE_TWENTYTHREE);
        assertThat(result.get(Calendar.SECOND)).isEqualTo(SECOND_FIFTYSEVEN);
    }

    @Test
    public void isAfterOrEqualSameDate() {
        // GIVEN
        final LocalDate reference = LocalDate.of(YEAR_TWENTY_TWENTY, MONTH_FEB, DAY_OF_MONTH_TWENTY);
        final LocalDate toTest = LocalDate.of(YEAR_TWENTY_TWENTY, MONTH_FEB, DAY_OF_MONTH_TWENTY);
        // WHEN
        final boolean result = DateUtils.isAfterOrEqual(reference, toTest);
        // THEN
        assertThat(result).isTrue();
    }

    @Test
    public void isAfterOrEqualReferenceInPast() {
        // GIVEN
        final LocalDate reference = LocalDate.of(YEAR_TWENTY_TWENTY, 1, DAY_OF_MONTH_TWENTY);
        final LocalDate toTest = LocalDate.of(YEAR_TWENTY_TWENTY, MONTH_FEB, DAY_OF_MONTH_TWENTY);
        // WHEN
        final boolean result = DateUtils.isAfterOrEqual(reference, toTest);
        // THEN
        assertThat(result).isFalse();
    }

    @Test
    public void isAfterOrEqualReferenceInFuture() {
        // GIVEN
        final LocalDate reference = LocalDate.of(YEAR_TWENTY_TWENTY, 3, DAY_OF_MONTH_TWENTY);
        final LocalDate toTest = LocalDate.of(YEAR_TWENTY_TWENTY, MONTH_FEB, DAY_OF_MONTH_TWENTY);
        // WHEN
        final boolean result = DateUtils.isAfterOrEqual(reference, toTest);
        // THEN
        assertThat(result).isTrue();
    }

}