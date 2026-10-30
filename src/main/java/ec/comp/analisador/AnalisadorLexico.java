package ec.comp.analisador;

import ec.comp.model.TipoToken;
import ec.comp.model.Token;
import ec.comp.util.AlphaNumericoUtil;

public class AnalisadorLexico {

    private String entrada;
    private int tamanho;
    private int pos;

    public AnalisadorLexico(String entrada) {
        this.entrada = entrada;
        this.tamanho = entrada.length();
        this.pos = 0;
    }

    // Retorna o próximo token do código-fonte
    public Token nextToken() {
        this.eliminaEspacoComentario();

        if (pos >= tamanho) {
            return new Token(TipoToken.FIM, "", 0, 0);
        }

        char ch = entrada.charAt(pos);

        // Identificadores e Palavras-chave ([a-zA-Z_][a-zA-Z0-9_]*)
        if (AlphaNumericoUtil.isAlphaNumerico(ch) || ch == '_') {
            int start = pos;
            while (pos < tamanho && (AlphaNumericoUtil.isAlphaNumerico(entrada.charAt(pos)) || AlphaNumericoUtil.isSymbol(entrada.charAt(pos)))) {
                pos++;
            }
            String lexeme = entrada.substring(start, pos);
            if (this.isPalavraReservada(lexeme)) {
                return new Token(TipoToken.PALAVRA_RESERVADA, lexeme, 0 , 0);
            }

            return new Token(TipoToken.IDENTIFICADOR, lexeme, 0, 0);
        }

        // Constantes Numéricas Inteiras ([0-9]+)
        if (AlphaNumericoUtil.isDigito(ch)) {
            int start = pos;
            while (pos < tamanho && AlphaNumericoUtil.isDigito(entrada.charAt(pos))) {
                pos++;
            }
            return new Token(TipoToken.NUMERO, entrada.substring(start, pos), 0, 0);
        }

        //incremento e decremento
        if(ch == '+' && entrada.charAt(pos + 1) == '+'){
            int start = pos;
            pos += 2;
            return new Token(TipoToken.OPERADOR, entrada.substring(start, pos), 0, 0);
        }
        else if(ch == '-' && entrada.charAt(pos+1) == '-'){
            int start = pos;
            pos += 2;
            return new Token(TipoToken.OPERADOR, entrada.substring(start, pos), 0, 0);
        }

        // Operadores compostos de dois caracteres (==, !=, <=, >=)
        if (ch == '=' || ch == '!' || ch == '<' || ch == '>' || ch == '+' || ch == '-' || ch == '*' || ch == '/') {
            int start = pos;
            pos++;
            if (pos < tamanho && entrada.charAt(pos) == '=') {
                pos++;
            }
            return new Token(TipoToken.OPERADOR, entrada.substring(start, pos), 0, 0);
        }



        // Operadores simples (+, -, *, /)
        if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
            pos++;
            return new Token(TipoToken.OPERADOR, String.valueOf(ch), 0, 0);
        }

        // Delimitadores e Pontuação
        if (ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == ';' || ch == ',') {
            pos++;
            return new Token(TipoToken.DELIMITADOR, String.valueOf(ch), 0, 0);
        }

        //Strings
        if(ch == '"'){
            int start = pos;
            pos++;
            while(pos < tamanho && entrada.charAt(pos) != '"' && entrada.charAt(pos) != '\n'){
                pos++;
            }
            if(pos < tamanho && entrada.charAt(pos) == '"') {
                pos++;
                String string = entrada.substring(start, pos);
                return new Token(TipoToken.STRING, string, 0, 0);
            }
            return new Token(TipoToken.INVALIDO, "", 0, 0);
        }

        // Caractere desconhecido/inválido
        pos++;
        return new Token(TipoToken.INVALIDO, String.valueOf(ch), 0, 0);
    }

    // Elimina espaços, tabulações, quebras de linha e comentários em C
    private void eliminaEspacoComentario() {
        while (pos < tamanho) {
            char ch = entrada.charAt(pos);

            // Espaços em branco e caracteres de formatacao
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                pos++;
                continue;
            }

            // Comentários: // ou /* ... */
            if (ch == '$' && pos + 1 < tamanho) {
                char anterior = entrada.charAt(pos + 1);

                // Comentário de linha única
                if (anterior != '<') {
                    pos += 2;
                    while (pos < tamanho && entrada.charAt(pos) != '\n') {
                        pos++;
                    }
                    continue;
                }

                // Comentário de bloco
                if (anterior == '<') {
                    pos += 2;
                    while (pos + 1 < tamanho && !(entrada.charAt(pos) == '*' && entrada.charAt(pos + 1) == '/')) {
                        pos++;
                    }
                    pos += 2; // Pula '*/'
                    continue;
                }
            }
            break;
        }
    }
    
    private boolean isPalavraReservada(String word) {
        String[] keywords = {"int", "string", "float", "char", "void", "if", "else", "while", "for", "return"};
        for (int i = 0; i < keywords.length; i++) {
            if (keywords[i].equals(word)) {
                return true;
            }
        }
        return false;
    }

}
