package ec.comp;

import ec.comp.analisador.AnalisadorLexico;
import ec.comp.analisador.AnalisadorSintatico;
import ec.comp.model.TipoToken;
import ec.comp.model.Token;
import ec.comp.util.Teclado;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        String codigoC;
        String nomeArquivo = Teclado.lerString("Digite o nome do arquivo: ");
        String path = "src/main/java/ec/comp/" + nomeArquivo;

        try {
            codigoC = Files.readString(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        AnalisadorLexico lexico = new AnalisadorLexico(codigoC);
        Token token;
        token = lexico.nextToken();
        List<Token> tokens = new ArrayList<>();

        while (token.getTipoToken() != TipoToken.FIM) {
            if (token.getTipoToken() == TipoToken.INVALIDO) {
                System.out.println("\nCódigo inválido! Token inesperado: " + token);
                return;
            }
            System.out.println(token);
            tokens.add(token);
            token = lexico.nextToken();
        }
        tokens.add(token);
        System.out.println("\nCódigo validado com sucesso pelo Analisador Léxico!");

        AnalisadorSintatico parser = new AnalisadorSintatico(tokens);
        try {
            parser.parsePrograma();
        } catch (RuntimeException e) {
            System.err.println("\n" + e.getMessage());
        }

    }
}