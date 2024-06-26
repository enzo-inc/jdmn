/*
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gs.dmn.feel.lib.type.time.xml;

import com.gs.dmn.feel.lib.type.time.BaseDateTimeLib;
import com.gs.dmn.feel.lib.type.time.DateTimeLib;
import com.gs.dmn.runtime.DMNRuntimeException;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Calendar;
import java.util.List;
import java.util.function.Predicate;

public class DefaultDateTimeLib extends BaseDateTimeLib implements DateTimeLib<BigDecimal, XMLGregorianCalendar, XMLGregorianCalendar, XMLGregorianCalendar, Duration> {
    //
    // Conversion functions
    //
    @Override
    public XMLGregorianCalendar date(String literal) {
        if (StringUtils.isBlank(literal)) {
            return null;
        }

        XMLGregorianCalendar calendar = FEELXMLGregorianCalendar.makeXMLCalendar(this.dateTemporalAccessor(literal));
        return this.isValidDate(calendar) ? calendar : null;
    }

    @Override
    public XMLGregorianCalendar date(BigDecimal year, BigDecimal month, BigDecimal day) {
        if (year == null || month == null || day == null) {
            return null;
        }

        XMLGregorianCalendar calendar = FEELXMLGregorianCalendar.makeDate(year.toBigInteger(), month.intValue(), day.intValue());
        return this.isValidDate(calendar) ? calendar : null;
    }

    @Override
    public XMLGregorianCalendar date(Object fromObj) {
        if (fromObj == null) {
            return null;
        }

        XMLGregorianCalendar from = (XMLGregorianCalendar) fromObj;
        FEELXMLGregorianCalendar calendar = (FEELXMLGregorianCalendar) from.clone();
        calendar.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
        calendar.setZoneID(null);
        return this.isValidDate(calendar) ? calendar : null;
    }

    @Override
    public XMLGregorianCalendar time(String literal) {
        if (literal == null) {
            return null;
        }

        literal = this.fixDateTimeFormat(literal);
        XMLGregorianCalendar calendar = FEELXMLGregorianCalendar.makeXMLCalendar(this.timeTemporalAccessor(literal));
        return this.isValidTime(calendar) ? calendar : null;
    }

    @Override
    public XMLGregorianCalendar time(BigDecimal hour, BigDecimal minute, BigDecimal second, Duration offset) {
        if (hour == null || minute == null || second == null) {
            return null;
        }

        XMLGregorianCalendar calendar;
        BigDecimal secondFraction = second.subtract(BigDecimal.valueOf(second.intValue()));
        if (offset != null) {
            String sign = offset.getSign() < 0 ? "-" : "+";
            int seconds = offset.getSeconds();
            String zoneId;
            if (seconds == 0) {
                zoneId = String.format("%s%02d:%02d", sign, offset.getHours(), offset.getMinutes());
            } else {
                zoneId = String.format("%s%02d:%02d:%02d", sign, offset.getHours(), offset.getMinutes(), seconds);
            }
            calendar = FEELXMLGregorianCalendar.makeTime(hour.intValue(), minute.intValue(), second.intValue(), secondFraction, zoneId);
        } else {
            calendar = FEELXMLGregorianCalendar.makeTime(hour.intValue(), minute.intValue(), second.intValue(), secondFraction, null);
        }
        return this.isValidTime(calendar) ? calendar : null;
    }

    @Override
    public XMLGregorianCalendar time(Object fromObj) {
        if (fromObj == null) {
            return null;
        }

        XMLGregorianCalendar from = (XMLGregorianCalendar) fromObj;
        FEELXMLGregorianCalendar calendar = (FEELXMLGregorianCalendar) from.clone();
        if (from.getXMLSchemaType() == DatatypeConstants.DATE) {
            calendar.setYear(DatatypeConstants.FIELD_UNDEFINED);
            calendar.setMonth(DatatypeConstants.FIELD_UNDEFINED);
            calendar.setDay(DatatypeConstants.FIELD_UNDEFINED);
            calendar.setHour(0);
            calendar.setMinute(0);
            calendar.setSecond(0);
            calendar.setZoneID("Z");
        } else if (from.getXMLSchemaType() == DatatypeConstants.DATETIME) {
            calendar.setYear(DatatypeConstants.FIELD_UNDEFINED);
            calendar.setMonth(DatatypeConstants.FIELD_UNDEFINED);
            calendar.setDay(DatatypeConstants.FIELD_UNDEFINED);
        }
        return this.isValidTime(calendar) ? calendar : null;
    }

    @Override
    public XMLGregorianCalendar dateAndTime(String literal) {
        if (literal == null) {
            return null;
        }

        literal = this.fixDateTimeFormat(literal);
        XMLGregorianCalendar calendar = FEELXMLGregorianCalendar.makeXMLCalendar(this.dateTimeTemporalAccessor(literal));
        return this.isValidDateTime(calendar) ? calendar : null;
    }

    @Override
    public XMLGregorianCalendar dateAndTime(Object dateObj, Object timeObj) {
        if (dateObj == null || timeObj == null) {
            return null;
        }

        XMLGregorianCalendar date = (XMLGregorianCalendar) dateObj;
        XMLGregorianCalendar time = (XMLGregorianCalendar) timeObj;
        XMLGregorianCalendar calendar = FEELXMLGregorianCalendar.makeDateTime(
                BigInteger.valueOf(date.getYear()), date.getMonth(), date.getDay(),
                time.getHour(), time.getMinute(), time.getSecond(), time.getFractionalSecond(),
                ((FEELXMLGregorianCalendar) time).getZoneID()
        );
        return this.isValidDateTime(calendar) ? calendar : null;
    }

    //
    // Date properties
    //
    @Override
    public Integer year(Object date) {
        if (date == null) {
            return null;
        }

        return ((XMLGregorianCalendar) date).getYear();
    }

    @Override
    public Integer month(Object date) {
        if (date == null) {
            return null;
        }

        return ((XMLGregorianCalendar) date).getMonth();
    }

    @Override
    public Integer day(Object date) {
        if (date == null) {
            return null;
        }

        return ((XMLGregorianCalendar) date).getDay();
    }

    @Override
    public Integer weekday(Object date) {
        if (date == null) {
            return null;
        }

        return ((XMLGregorianCalendar) date).toGregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1;
    }

    //
    // Time properties
    //
    @Override
    public Integer hour(Object date) {
        if (date == null) {
            return null;
        }

        return ((XMLGregorianCalendar) date).getHour();
    }

    @Override
    public Integer minute(Object date) {
        if (date == null) {
            return null;
        }

        return ((XMLGregorianCalendar) date).getMinute();
    }

    @Override
    public Integer second(Object date) {
        if (date == null) {
            return null;
        }

        return ((XMLGregorianCalendar) date).getSecond();
    }

    @Override
    public Duration timeOffset(Object date) {
        if (date == null) {
            return null;
        }

        int secondsOffset = ((XMLGregorianCalendar) date).getTimezone();
        if (secondsOffset == DatatypeConstants.FIELD_UNDEFINED) {
            return null;
        } else {
            return XMLDurationFactory.INSTANCE.dayTimeFromValue(secondsOffset);
        }
    }

    @Override
    public String timezone(Object date) {
        if (date == null) {
            return null;
        }

        return ((FEELXMLGregorianCalendar) date).getZoneID();
    }

    @Override
    public XMLGregorianCalendar now() {
        return FEELXMLGregorianCalendar.makeXMLCalendar(ZonedDateTime.now());
    }

    @Override
    public XMLGregorianCalendar today() {
        return FEELXMLGregorianCalendar.makeXMLCalendar(LocalDate.now());
    }

    //
    // Temporal functions
    //
    @Override
    public Integer dayOfYear(Object date) {
        if (date == null) {
            return null;
        }

        return ((XMLGregorianCalendar) date).toGregorianCalendar().get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public String dayOfWeek(Object date) {
        if (date == null) {
            return null;
        }

        int dow = ((XMLGregorianCalendar) date).toGregorianCalendar().get(Calendar.DAY_OF_WEEK);
        return DAY_NAMES[dow];
    }

    @Override
    public Integer weekOfYear(Object date) {
        if (date == null) {
            return null;
        }

        XMLGregorianCalendar xmlDate = ((XMLGregorianCalendar) date);
        LocalDate localDate = LocalDate.of(xmlDate.getYear(), xmlDate.getMonth(), xmlDate.getDay());
        return localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    @Override
    public String monthOfYear(Object date) {
        if (date == null) {
            return null;
        }

        int moy = ((XMLGregorianCalendar) date).getMonth();
        return MONTH_NAMES[moy - 1];
    }

    //
    // Extra conversion functions
    //
    @Override
    public XMLGregorianCalendar toDate(Object from) {
        if (!(from instanceof XMLGregorianCalendar)) {
            return null;
        }

        XMLGregorianCalendar calendar = (XMLGregorianCalendar) from;
        return date(calendar);
    }

    @Override
    public XMLGregorianCalendar toTime(Object from) {
        if (!(from instanceof XMLGregorianCalendar)) {
            return null;
        }

        return time(from);
    }

    @Override
    public XMLGregorianCalendar toDateTime(Object from) {
        if (!(from instanceof XMLGregorianCalendar)) {
            return null;
        }

        return dateAndTime(toDate(from), toTime(from));
    }

    @Override
    public <T> T min(List<T> list) {
        return minMax(list, DefaultXMLCalendarComparator.COMPARATOR, DefaultDurationComparator.COMPARATOR, x -> x > 0);
    }

    @Override
    public <T> T max(List<T> list) {
        return minMax(list, DefaultXMLCalendarComparator.COMPARATOR, DefaultDurationComparator.COMPARATOR, x -> x < 0);
    }

    private <T> T minMax(List<T> list, DefaultXMLCalendarComparator dateTimeComparator, DefaultDurationComparator durationComparator, Predicate<Integer> condition) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        T result = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            T x = list.get(i);
            if (dateTimeComparator.isDate(result) && dateTimeComparator.isDate(x)) {
                if (condition.test(dateTimeComparator.compareTo((XMLGregorianCalendar) result, (XMLGregorianCalendar) x))) {
                    result = x;
                }
            } else if (dateTimeComparator.isTime(result) && dateTimeComparator.isTime(x)) {
                if (condition.test(dateTimeComparator.compareTo((XMLGregorianCalendar) result, (XMLGregorianCalendar) x))) {
                    result = x;
                }
            } else if (dateTimeComparator.isDateTime(result) && dateTimeComparator.isDateTime(x)) {
                if (condition.test(dateTimeComparator.compareTo((XMLGregorianCalendar) result, (XMLGregorianCalendar) x))) {
                    result = x;
                }
            } else if (durationComparator.isYearsAndMonthsDuration(result) && durationComparator.isYearsAndMonthsDuration(x)) {
                if (condition.test(durationComparator.compareTo((Duration) result, (Duration) x))) {
                    result = x;
                }
            } else if (durationComparator.isDaysAndTimeDuration(result) && durationComparator.isDaysAndTimeDuration(x)) {
                if (condition.test(durationComparator.compareTo((Duration) result, (Duration) x))) {
                    result = x;
                }
            } else {
                throw new DMNRuntimeException(String.format("Cannot compare '%s' and '%s'", result, x));
            }
        }
        return result;
    }

    public TemporalAccessor dateTemporalAccessor(String literal) {
        if (literal == null) {
            return null;
        }

        if (!BEGIN_YEAR.matcher(literal).find()) {
            throw new DMNRuntimeException(String.format("Illegal year in '%s'", literal));
        }
        try {
            return LocalDate.from(FEEL_DATE.parse(literal));
        } catch (DateTimeException e) {
            throw new RuntimeException("Parsing exception in date literal", e);
        }
    }

    public TemporalAccessor timeTemporalAccessor(String literal) {
        if (literal == null) {
            return null;
        }

        if (this.hasZoneOffset(literal) && this.hasZoneId(literal)) {
            throw new DMNRuntimeException(String.format("Time literal '%s' has both a zone offset and zone id", literal));
        }
        try {
            TemporalAccessor parsed = FEEL_TIME.parse(literal);

            if (parsed.query(TemporalQueries.offset()) != null) {
                return parsed.query(OffsetTime::from);
            } else if (parsed.query(TemporalQueries.zone()) == null) {
                return parsed.query(LocalTime::from);
            }

            return parsed;
        } catch (DateTimeException e) {
            throw new DMNRuntimeException("Parsing exception in time literal", e);
        }
    }

    public TemporalAccessor dateTimeTemporalAccessor(String literal) {
        if (literal == null) {
            return null;
        }

        if (!BaseDateTimeLib.BEGIN_YEAR.matcher(literal).find()) {
            throw new DMNRuntimeException(String.format("Illegal year in '%s'", literal));
        }
        if (this.hasZoneOffset(literal) && this.hasZoneId(literal)) {
            throw new DMNRuntimeException(String.format("Time literal '%s' has both a zone offset and zone id", literal));
        }
        try {
            if (literal.contains("T")) {
                return FEEL_DATE_TIME.parseBest(literal, ZonedDateTime::from, OffsetDateTime::from, LocalDateTime::from);
            } else {
                LocalDate value = DateTimeFormatter.ISO_DATE.parse(literal, LocalDate::from);
                return LocalDateTime.of(value, LocalTime.of(0, 0));
            }
        } catch (Exception e) {
            throw new RuntimeException("Parsing exception in date and time literal", e);
        }
    }

    private boolean isValidDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return false;
        }

        long year = calendar.getYear();
        BigInteger eonAndYear = calendar.getEonAndYear();
        if (eonAndYear != null) {
            year = eonAndYear.intValue();
        }
        return
                isValidDate(year, calendar.getMonth(), calendar.getDay())
                        && isUndefined(calendar.getHour())
                        && isUndefined(calendar.getMinute())
                        && isUndefined(calendar.getSecond())
                ;
    }

    private boolean isValidTime(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return false;
        }

        return
                isValidTime(calendar.getHour(), calendar.getMinute(), calendar.getSecond(), calendar.getTimezone())
                        && isUndefined(calendar.getYear())
                        && isUndefined(calendar.getMonth())
                        && isUndefined(calendar.getDay())
                ;
    }

    private boolean isValidDateTime(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return false;
        }

        return isValidDateTime(
                calendar.getYear(), calendar.getMonth(), calendar.getDay(),
                calendar.getHour(), calendar.getMinute(), calendar.getSecond(), calendar.getTimezone());
    }
}
