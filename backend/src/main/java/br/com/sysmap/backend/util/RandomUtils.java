package br.com.sysmap.backend.util;

import lombok.experimental.UtilityClass;
import java.security.SecureRandom;

@UtilityClass
public class RandomUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * Gera um código numérico aleatório com o tamanho especificado.
     */
    public static String generateNumericCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("O tamanho do código deve ser maior que zero.");
        }
        
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(RANDOM.nextInt(10));
        }
        return code.toString();
    }

    /**
     * Gera um código alfanumérico aleatório com o tamanho especificado.
     */
    public static String generateAlphanumericCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("O tamanho do código deve ser maior que zero.");
        }

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return code.toString();
    }
}
