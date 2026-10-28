package ec.comp.util;

public class AlphaNumericoUtil {

    // Métodos auxiliares legados (substituem regex e Character.isLetter)
    public static boolean isLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isDigito(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isAlphaNumerico(char c) {
        return isLetra(c) || isDigito(c);
    }

}
