package com.medicalassistance.core.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@WritingConverter
public class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, Long> {
    @Override
    public Long convert(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }
}