package ec.comp;
public class LexerC {

    // Enumeração simples para classificar os tokens
    public enum TokenType {
        PALAVRA_RESERVADA,
        IDENTIFICADOR,
        NUMERO,
        OPERADOR,
        DELIMITADOR,
        FIM,
        INVALIDO
    }

    // Estrutura básica para representar um token
    public static class Token {
        public TokenType type;
        public String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        public String toString() {
            return "<" + type + ", \"" + value + "\">";
        }
    }

    private String input;
    private int length;
    private int pos;

    public LexerC(String input) {
        this.input = input;
        this.length = input.length();
        this.pos = 0;
    }

    // Retorna o próximo token do código-fonte
    public Token nextToken() {
        skipWhitespaceAndComments();

        if (pos >= length) {
            return new Token(TokenType.FIM, "");
        }

        char ch = input.charAt(pos);

        // Identificadores e Palavras-chave ([a-zA-Z_][a-zA-Z0-9_]*)
        if (isAlpha(ch) || ch == '_') {
            int start = pos;
            while (pos < length && (isAlphaNumeric(input.charAt(pos)) || input.charAt(pos) == '_')) {
                pos++;
            }
            String lexeme = input.substring(start, pos);
            if (isKeyword(lexeme)) {
                return new Token(TokenType.PALAVRA_RESERVADA, lexeme);
            }
            return new Token(TokenType.IDENTIFICADOR, lexeme);
        }

        // Constantes Numéricas Inteiras ([0-9]+)
        if (isDigit(ch)) {
            int start = pos;
            while (pos < length && isDigit(input.charAt(pos))) {
                pos++;
            }
            return new Token(TokenType.NUMERO, input.substring(start, pos));
        }

        // Operadores compostos de dois caracteres (==, !=, <=, >=)
        if (ch == '=' || ch == '!' || ch == '<' || ch == '>') {
            int start = pos;
            pos++;
            if (pos < length && input.charAt(pos) == '=') {
                pos++;
            }
            return new Token(TokenType.OPERADOR, input.substring(start, pos));
        }

        // Operadores simples (+, -, *, /)
        if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
            pos++;
            return new Token(TokenType.OPERADOR, String.valueOf(ch));
        }

        // Delimitadores e Pontuação
        if (ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == ';' || ch == ',') {
            pos++;
            return new Token(TokenType.DELIMITADOR, String.valueOf(ch));
        }

        // Caractere desconhecido/inválido
        pos++;
        return new Token(TokenType.INVALIDO, String.valueOf(ch));
    }

    // Ignora espaços, tabulações, quebras de linha e comentários em C
    private void skipWhitespaceAndComments() {
        while (pos < length) {
            char ch = input.charAt(pos);

            // Espaços em branco e caracteres de formatacao
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                pos++;
                continue;
            }

            // Comentários: // ou /* ... */
            if (ch == '/' && pos + 1 < length) {
                char next = input.charAt(pos + 1);

                // Comentário de linha única
                if (next == '/') {
                    pos += 2;
                    while (pos < length && input.charAt(pos) != '\n') {
                        pos++;
                    }
                    continue;
                }

                // Comentário de bloco
                if (next == '*') {
                    pos += 2;
                    while (pos + 1 < length && !(input.charAt(pos) == '*' && input.charAt(pos + 1) == '/')) {
                        pos++;
                    }
                    pos += 2; // Pula '*/'
                    continue;
                }
            }

            break;
        }
    }

    // Métodos auxiliares legados (substituem regex e Character.isLetter)
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isKeyword(String word) {
        String[] keywords = {"int", "float", "char", "void", "if", "else", "while", "for", "return"};
        for (int i = 0; i < keywords.length; i++) {
            if (keywords[i].equals(word)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String codigoC = "int main() {\n" +
                         "    // Exemplo de código C\n" +
                         "    int x = 10;\n" +
                         "    if (x >= 5) {\n" +
                         "        return x + 1;\n" +
                         "    }\n" +
                         "    return 0;\n" +
                         "}";

        LexerC lexer = new LexerC(codigoC);
        Token token;

        while ((token = lexer.nextToken()).type != TokenType.FIM) {
            System.out.println(token);
        }
    }
}