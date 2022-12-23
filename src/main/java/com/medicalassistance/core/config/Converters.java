package com.medicalassistance.core.config;

import com.medicalassistance.core.converter.ZonedDateTimeReadConverter;
import com.medicalassistance.core.converter.ZonedDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class Converters {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
                Arrays.asList(
                        new ZonedDateTimeWriteConverter(),
                        new ZonedDateTimeReadConverter()
                ));
    }
}