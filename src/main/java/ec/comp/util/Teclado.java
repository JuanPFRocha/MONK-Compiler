package ec.comp.util;

import java.util.Scanner;

public class Teclado {

    private static final Scanner scanner = new Scanner(System.in);

    public static String lerString() {
        return scanner.nextLine();
    }

    public static String lerString(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }
}
