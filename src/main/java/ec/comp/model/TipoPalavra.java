package ec.comp.model;
import java.util.Map;
import java.util.HashMap;

public enum TipoPalavra {
    INT("int"),
    STRING("string"),
    REAL("real"),
    CHAR("char"),
    VOID("void"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    DO("do"),
    FOR("for"),
    RETURN("return"),
    SPEAK("speak"),
    LISTEN("listen"),
    BOOL("bool"),
    RITUAL("ritual"),
    STRUCT("Struct"),
    CONST("const"),
    SWITCH("switch"),
    BIT("bit"),
    BYTE("byte"),
    NULL("NULL"),
    TRUE("True"),
    FALSE("False");


    private String lexeme;

    TipoPalavra(String lexeme){
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }

    private static final Map<String, TipoPalavra> PALAVRA_CHAVE = new HashMap<>();


    // Coloca no mapa todas as palavras chaves
    static {
          for(TipoPalavra p : values()){
              PALAVRA_CHAVE.put(p.lexeme, p);
          }
    }

    // Verifica se faz parte do Enum
    public static boolean isPalavra(String p){
        return PALAVRA_CHAVE.containsKey(p);
    }
}
