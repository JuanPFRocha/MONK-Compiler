package ec.comp.analisador;

import ec.comp.model.TipoPalavra;
import ec.comp.model.TipoToken;
import ec.comp.model.Token;

import java.util.ArrayList;
import java.util.List;

public class AnalisadorSintatico {
    private final List<Token> tokens;
    private int pos = 0;

    public AnalisadorSintatico(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token tokenAtual() {
        if (pos >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(pos);
    }

    private Token tokenAdiante() {
        if (pos + 1 >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(pos + 1);
    }

    private Token consumirPorTipo(TipoToken tipoEsperado) {
        Token token = tokenAtual();
        if (token.getTipoToken() == tipoEsperado) {
            pos++;
            return token;
        }
        throw new RuntimeException(String.format(
                "Erro Sintático [Linha " + token.getLinha() + ", Coluna " + token.getColuna() +
                        "]: Token esperado: " + tipoEsperado
        ));
    }

    private Token consumirPalavra(String palavra) {
        Token token = tokenAtual();
        if (token.getTipoToken() == TipoToken.PALAVRA_RESERVADA && token.getToken().equals(palavra)) {
            pos++;
            return token;
        }
        throw new RuntimeException(String.format(
                "Erro Sintático [Linha " + token.getLinha() + ", Coluna " + token.getColuna() +
                        "]: Token esperado: " + palavra
        ));
    }

    private Token consumirSimbolo(String sim) {
        Token token = tokenAtual();

        if (token.getToken().equals(sim)) {
            pos++;
            return token;
        }
        throw new RuntimeException(String.format(
                "Erro Sintático [Linha " + token.getLinha() + ", Coluna " + token.getColuna() +
                        "]: Token esperado: " + sim
        ));
    }

    private Token consumirTipo() {
        Token token = tokenAtual();
        if (token.getTipoToken() == TipoToken.PALAVRA_RESERVADA && TipoPalavra.isTipo(token.getToken())) {
            pos++;
            return token;
        }
        throw new RuntimeException(String.format(
                "Erro Sintático [Linha " + token.getLinha() + ", Coluna " + token.getColuna() +
                        "]: Token esperado: int || real..."
        ));
    }

    private boolean verificaTipo(TipoToken tipo) {
        return tokenAtual().getTipoToken() == tipo;
    }

    private boolean verificaPalavra(String p) {
        return TipoPalavra.isPalavra(tokenAtual().getToken()) && tokenAtual().getToken().equals(p);
    }

    private void imprimirErro() {
        throw new RuntimeException(String.format(
                "Erro Sintático [Linha " + tokenAtual().getLinha() + ", Coluna" + tokenAtual().getColuna() +
                        "]"
        ));
    }

    public void parsePrograma() {
        while (!verificaTipo(TipoToken.FIM)) {
            if (verificaProcessoGlobal()) {
                parseProcessoGlobal();
            } else if (verificaDeclaracao()) {
                parseDeclaracao();
            } else if (verificaRitual()) {
                parseRitual();
            } else {
                imprimirErro();
            }


        }
        consumirPorTipo(TipoToken.FIM);
        // CÓDIGO ANALISADO COM SUCESSO!
    }

    private boolean verificaProcessoGlobal() {
        return (verificaBiblioteca() || verificaPalavra(TipoPalavra.CONST.getLexeme()));
    }

    public void parseProcessoGlobal() {
        if (verificaBiblioteca()) {
            parseBiblioteca();
        } else if (verificaPalavra(TipoPalavra.CONST.getLexeme())) {
            parseConstante();
        } else {
            imprimirErro();
        }
    }

    private boolean verificaBiblioteca() {
        return verificaPalavra(TipoPalavra.INCLUDE.getLexeme());
    }

    public void parseBiblioteca() {
        consumirPalavra(TipoPalavra.INCLUDE.getLexeme());
        consumirSimbolo("<");
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        consumirSimbolo(">");
    }

    public void parseConstante() {
        consumirPalavra(TipoPalavra.CONST.getLexeme());
        consumirTipo();
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        parseLiteral();
        consumirSimbolo(";");

    }

    private boolean verificaLiteral() {
        return (verificaTipo(TipoToken.OPERADOR) && tokenAtual().getToken().equals("-")
                || verificaTipo(TipoToken.NUMERO) || verificaTipo(TipoToken.STRING)
                || verificaPalavra(TipoPalavra.TRUE.getLexeme())
                || verificaPalavra(TipoPalavra.FALSE.getLexeme())
                || verificaPalavra(TipoPalavra.NULL.getLexeme()));
    }

    public void parseLiteral() {
        Token token = tokenAtual();

        if (verificaTipo(TipoToken.OPERADOR) && tokenAtual().getToken().equals("-")) {
            consumirPorTipo(TipoToken.OPERADOR);
            consumirPorTipo(TipoToken.NUMERO);
        } else if (verificaTipo(TipoToken.NUMERO)) {
            consumirPorTipo(TipoToken.NUMERO);
        } else if (verificaTipo(TipoToken.STRING)) {
            consumirPorTipo(TipoToken.STRING);
        } else if (verificaPalavra(TipoPalavra.TRUE.getLexeme())) {
            consumirPalavra(TipoPalavra.TRUE.getLexeme());
        } else if (verificaPalavra(TipoPalavra.FALSE.getLexeme())) {
            consumirPalavra(TipoPalavra.FALSE.getLexeme());
        } else if (verificaPalavra(TipoPalavra.NULL.getLexeme())) {
            consumirPalavra(TipoPalavra.NULL.getLexeme());
        } else {
            imprimirErro();
        }
    }

    private boolean verificaDeclaracao() {
        return (verificaStruct() || verificaDeclaracaoVar());
    }

    public void parseDeclaracao() {
        if (verificaStruct()) {
            parseStruct();
        } else if (verificaDeclaracaoVar()) {
            parseDeclaracaoVar();
        } else {
            imprimirErro();
        }
    }

    private boolean verificaStruct() {
        return (verificaPalavra(TipoPalavra.STRUCT.getLexeme()));
    }

    public void parseStruct() {
        consumirPalavra(TipoPalavra.STRUCT.getLexeme());
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        consumirSimbolo("{");
        while (verificaCampo()) {
            parseCampo();
        }
        consumirSimbolo("}");
    }

    private boolean verificaCampo() {
        return (TipoPalavra.isTipo(tokenAtual().getToken()) && (tokenAdiante().getToken().equals("*") || tokenAdiante().getTipoToken() == TipoToken.IDENTIFICADOR));
    }

    public void parseCampo() {
        consumirTipo();
        if (tokenAtual().getToken().equals("*")) {
            consumirSimbolo("*");
        }
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        while (verificaDimensao()) {
            parseDimensao();
        }
        consumirSimbolo(";");
    }

    private boolean verificaDimensao() {
        return (tokenAtual().getToken().equals("["));
    }

    public void parseDimensao() {
        consumirSimbolo("[");
        if (verificaExpressao()) {
            parseExpressao();
        }
        consumirSimbolo("]");
    }

    private boolean verificaExpressao() {
        return verificaTermoE();
    }

    public void parseExpressao() {
        parseTermoE();
        while (tokenAtual().getToken().equals("||")) {
            consumirPorTipo(TipoToken.OPERADOR);
            parseTermoE();

        }
    }

    private boolean verificaTermoE() {
        return verificaComparacao();
    }

    public void parseTermoE() {
        parseComparacao();
        while (tokenAtual().getToken().equals("&&")) {
            consumirPorTipo(TipoToken.OPERADOR);
            parseComparacao();
        }
    }

    private boolean verificaComparacao() {
        return (verificaSoma());
    }

    public void parseComparacao() {
        parseSoma();
        if(verificaOpRel()){
            parseOpRel();
            parseSoma();
        }
    }

    private boolean verificaSoma() {
        return verificaProduto();
    }

    public void parseSoma() {
        parseProduto();
        while(tokenAtual().getToken().equals("+") || tokenAtual().getToken().equals("-")){
            consumirPorTipo(TipoToken.OPERADOR);
            parseProduto();
        }
    }

    private boolean verificaProduto(){return verificaUnario();}

    public void parseProduto(){
        parseUnario();
        while(tokenAtual().getToken().equals("*")
            || tokenAtual().getToken().equals("/")
            || tokenAtual().getToken().equals("%")){
            consumirPorTipo(TipoToken.OPERADOR);
            parseUnario();
        }
    }

    private boolean verificaUnario() {
        if (tokenAtual().getToken().equals("!")
                || tokenAtual().getToken().equals("-")
                || tokenAtual().getToken().equals("&")
                || tokenAtual().getToken().equals("*")){
                    return true;
                }
        return false;
    }

    public void parseUnario(){
        while(tokenAtual().getToken().equals("!")
                || tokenAtual().getToken().equals("-")
                || tokenAtual().getToken().equals("&")
                || tokenAtual().getToken().equals("*")){
            if(tokenAtual().getToken().equals("!")){
                consumirSimbolo("!");
            }
            else {
                consumirPorTipo(TipoToken.OPERADOR);
            }
        }
        parseFator();
    }

    public void parseFator() {
        if (verificaTipo(TipoToken.NUMERO)) {
            consumirPorTipo(TipoToken.NUMERO);
        } else if (verificaTipo(TipoToken.STRING)) {
            consumirPorTipo(TipoToken.STRING);
        } else if (verificaPalavra(TipoPalavra.TRUE.getLexeme())) {
            consumirPalavra(TipoPalavra.TRUE.getLexeme());
        } else if (verificaPalavra(TipoPalavra.FALSE.getLexeme())) {
            consumirPalavra(TipoPalavra.FALSE.getLexeme());
        } else if (verificaPalavra(TipoPalavra.NULL.getLexeme())) {
            consumirPalavra(TipoPalavra.NULL.getLexeme());
        } else if (verificaChamada()) {
            parseChamada();
        } else if (verificaTipo(TipoToken.IDENTIFICADOR)) {
            consumirPorTipo(TipoToken.IDENTIFICADOR);
        } else if (verificaExpressao()) {
            consumirSimbolo("(");
            parseExpressao();
            consumirSimbolo(")");
        } else {
            throw new RuntimeException(
                    "Erro Sintático [Linha " + tokenAtual().getLinha() + ", Coluna " + tokenAtual().getColuna() +
                            "]: Esperado fator mas encontrado: " + tokenAtual().getToken());
        }

        while (verificaSufixo()) {
            parseSufixo();
        }
    }

    private boolean verificaChamada(){
        return tokenAtual().getTipoToken() == TipoToken.IDENTIFICADOR && tokenAdiante().getToken().equals("(");
    }

    public void parseChamada(){
        consumirPorTipo(TipoToken.OPERADOR);
        consumirSimbolo("(");
        if(verificaExpressao()){
            parseExpressao();
            while(tokenAtual().getToken().equals(".")){
                consumirSimbolo(",");
                parseExpressao();
            }
        }
        consumirSimbolo(")");
    }

    private boolean verificaSufixo(){return tokenAtual().getToken().equals("[") || tokenAtual().getToken().equals(".");}

    public void parseSufixo(){
        if(tokenAtual().getToken().equals("[")){
            consumirSimbolo("[");
            parseExpressao();
            consumirSimbolo("]");
        }
        else {
            consumirSimbolo(".");
            consumirPorTipo(TipoToken.IDENTIFICADOR);
        }
    }

    private boolean verificaOpRel(){
        return (
                   tokenAtual().getToken().equals("==")
                || tokenAtual().getToken().equals("!=")
                || tokenAtual().getToken().equals("<")
                || tokenAtual().getToken().equals("<=")
                || tokenAtual().getToken().equals(">")
                || tokenAtual().getToken().equals(">=")
                );
    }

    public void parseOpRel(){
        consumirPorTipo(TipoToken.OPERADOR);
    }

    private boolean verificaDeclaracaoVar(){
        return TipoPalavra.isTipo(tokenAtual().getToken());
    }
    public void parseDeclaracaoVar(){
        consumirTipo();
        if(tokenAtual().getToken().equals("*")){
            consumirSimbolo("*");
        }
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        while(verificaDimensao()){
            parseDimensao();
        }
        if(tokenAtual().getToken().equals("=")){
            consumirSimbolo("=");
            if(verificaInicializador()) {
                parseInicializador();
            }
            else {
                imprimirErro();
            }
        }
        consumirSimbolo(";");
    }

    private boolean verificaInicializador(){return verificaExpressao();}

    public void parseInicializador(){
        if(verificaExpressao()){
            parseExpressao();
        }
        else if(tokenAtual().getToken().equals("{")){
            consumirSimbolo("{");
            parseInicializador();
            while(tokenAtual().getToken().equals(",")){
                consumirSimbolo(",");
                parseInicializador();
            }
            consumirSimbolo("}");
        }
    }
}

