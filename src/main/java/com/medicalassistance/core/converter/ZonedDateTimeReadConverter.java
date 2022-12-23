package com.medicalassistance.core.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
@ReadingConverter
public class ZonedDateTimeReadConverter implements Converter<Long, ZonedDateTime> {
    @Override
    public ZonedDateTime convert(Long date) {
        return Instant.ofEpochMilli(date).atZone(ZoneOffset.UTC);
    }
}