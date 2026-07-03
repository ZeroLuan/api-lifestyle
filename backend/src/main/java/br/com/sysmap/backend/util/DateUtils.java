package br.com.sysmap.backend.util;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtils {

    /**
     * Converte uma String no formato ISO para LocalDateTime.
     * Utilizado principalmente nos Mappers para tratar entradas de DTOs.
     */
    public LocalDateTime stringToLocalDateTime(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            // Em um cenário real, poderíamos logar o erro ou lançar uma BusinessException
            return null;
        }
    }

    /**
     * Converte LocalDateTime para String no formato ISO.
     */
    public String localDateTimeToString(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
