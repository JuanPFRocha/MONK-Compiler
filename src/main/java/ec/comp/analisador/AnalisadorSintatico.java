package ec.comp.analisador;

import ec.comp.model.TipoPalavra;
import ec.comp.model.TipoToken;
import ec.comp.model.Token;
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

    private Token tokenDepois() {
        return tokens.get(Math.min(pos + 2, tokens.size() - 1));
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
        if (ehTipo()) {
            pos++;
            return token;
        }
        throw new RuntimeException(String.format(
                "Erro Sintático [Linha " + token.getLinha() + ", Coluna " + token.getColuna() +
                        "]: Token esperado: struct criada || int || real..."
        ));
    }
    private void consumirTipoRetorno() {
        if (verificaTipoRetorno()) {
            pos++;
            return;
        }
        imprimirErro();
    }

    private boolean verificaTipo(TipoToken tipo) {
        return tokenAtual().getTipoToken() == tipo;
    }
    private boolean verificaTipoStruct() {
        if (!verificaTipo(TipoToken.IDENTIFICADOR)) return false;
        Token prox = tokenAdiante();
        return prox.getTipoToken() == TipoToken.IDENTIFICADOR
                || (prox.getToken().equals("*")
                && tokenDepois().getTipoToken() == TipoToken.IDENTIFICADOR);
    }

    private boolean verificaPalavra(String p) {
        return TipoPalavra.isPalavra(tokenAtual().getToken()) && tokenAtual().getToken().equals(p);
    }

    private boolean ehTipo() {
        return (verificaTipo(TipoToken.PALAVRA_RESERVADA)
                && TipoPalavra.isTipo(tokenAtual().getToken()))
                || verificaTipoStruct();
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
        consumirSimbolo("=");
        parseLiteral();
        consumirSimbolo(";");

    }


    public void parseLiteral() {

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
        return (ehTipo() && (tokenAdiante().getToken().equals("*") || tokenAdiante().getTipoToken() == TipoToken.IDENTIFICADOR));
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
        return (   tokenAtual().getToken().equals("!")
                || tokenAtual().getToken().equals("-")
                || tokenAtual().getToken().equals("&")
                || tokenAtual().getToken().equals("*") || verificaFator());
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

    private boolean verificaFator() {
        return verificaTipo(TipoToken.NUMERO)
                || verificaTipo(TipoToken.STRING)
                || verificaPalavra(TipoPalavra.TRUE.getLexeme())
                || verificaPalavra(TipoPalavra.FALSE.getLexeme())
                || verificaPalavra(TipoPalavra.NULL.getLexeme())
                || verificaTipo(TipoToken.IDENTIFICADOR)
                || tokenAtual().getToken().equals("(");
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
        } else if (tokenAtual().getToken().equals("(")) {
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
        if(inSpeak && verificaFormato()){
            parseFormato();
        }
    }

    private boolean verificaChamada(){
        return tokenAtual().getTipoToken() == TipoToken.IDENTIFICADOR && tokenAdiante().getToken().equals("(");
    }

    public void parseChamada(){
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        consumirSimbolo("(");
        if(verificaExpressao()){
            parseExpressao();
            while(tokenAtual().getToken().equals(",")){
                consumirSimbolo(",");
                parseExpressao();
            }
        }
        consumirSimbolo(")");
    }

    private boolean verificaSufixo() {
        return tokenAtual().getToken().equals("[")
                || (tokenAtual().getToken().equals(".")
                && tokenAdiante().getTipoToken() == TipoToken.IDENTIFICADOR);
    }

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
        return ehTipo();
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

    private boolean verificaInicializador(){
        return verificaExpressao() || tokenAtual().getToken().equals("{");

    }

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
        else {
            imprimirErro();
        }
    }

    private boolean verificaRitual(){
        return verificaPalavra(TipoPalavra.RITUAL.getLexeme());
    }

    public void parseRitual(){
        consumirPalavra(TipoPalavra.RITUAL.getLexeme());
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        consumirSimbolo("(");
        if(verificaListaParam()){
            parseListaParam();
        }
        consumirSimbolo(")");
        parseBlocoCodigo();
    }

    private boolean verificaListaParam(){return verificaParam();}

    public void parseListaParam(){
        parseParam();
        while(tokenAtual().getToken().equals(",")){
            consumirSimbolo(",");
            parseParam();
        }
    }

    private boolean verificaParam(){
        return (verificaPalavra(TipoPalavra.CONST.getLexeme()) || ehTipo());
    }

    public void parseParam(){
        if(verificaPalavra(TipoPalavra.CONST.getLexeme())){
            consumirPalavra(TipoPalavra.CONST.getLexeme());
        }
        consumirTipo();
        if(tokenAtual().getToken().equals("*")){
            consumirPorTipo(TipoToken.OPERADOR);
        }
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        if(tokenAtual().getToken().equals("[")){
            consumirSimbolo("[");
            consumirSimbolo("]");
        }
    }

    private boolean verificaComando(){
        return (
                   verificaDeclaracaoVar()
                || verificaAlvo()
                || verificaChamada()
                || verificaSpeak()
                || verificaListen()
                || verificaReturn()
                || verificaFor()
                || verificaWhile()
                || verificaDoWhile()
                || verificaIf()
                || verificaSwitch()
                );
    }

    public void parseComando(){
        if(verificaDeclaracaoVar()){
            parseDeclaracaoVar();
        }
        else if(verificaChamada()){
            parseChamada();
            consumirSimbolo(";");
        }
        else if (verificaAlvo()) {
            parseAtribuicaoOuIncremento();
            consumirSimbolo(";");
        }
        else if(verificaSpeak()){
            parseSpeak();
            consumirSimbolo(";");
        }
        else if(verificaListen()){
            parseListen();
            consumirSimbolo(";");
        }
        else if(verificaReturn()){
            parseReturn();
        }
        else if(verificaFor()){
            parseFor();
        }
        else if(verificaWhile()){
            parseWhile();
        }
        else if(verificaDoWhile()){
            parseDoWhile();
        }
        else if(verificaIf()){
            parseIf();
        }
        else if(verificaSwitch()){
            parseSwitch();
        }
        else {
            imprimirErro();
        }
    }

    public void parseAtribuicaoOuIncremento() {
        parseAlvo();
        if (tokenAtual().getToken().equals("++") || tokenAtual().getToken().equals("--")) {
            consumirPorTipo(TipoToken.OPERADOR);
        } else {
            parseOpAtrib();
            parseExpressao();
        }
    }

    private boolean verificaAlvo(){
        return (tokenAtual().getToken().equals("*") || verificaAcesso());
    }

    public void parseAlvo(){
        while(tokenAtual().getToken().equals("*")){
            consumirSimbolo("*");
        }
        parseAcesso();
    }

    private boolean verificaAcesso(){
        return verificaTipo(TipoToken.IDENTIFICADOR);
    }

    public void parseAcesso(){
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        while(verificaSufixo()){
            parseSufixo();
        }
    }

    public void parseOpAtrib(){
        if(tokenAtual().getToken().equals("=")
            || tokenAtual().getToken().equals("+=")
            || tokenAtual().getToken().equals("-=")
            || tokenAtual().getToken().equals("*=")
            || tokenAtual().getToken().equals("/=")
        ){
            consumirPorTipo(TipoToken.OPERADOR);
        }
        else {
            imprimirErro();
        }
    }


    private boolean verificaSpeak(){return verificaPalavra(TipoPalavra.SPEAK.getLexeme());}

    private boolean inSpeak = false;
    public void parseSpeak(){
        consumirPalavra(TipoPalavra.SPEAK.getLexeme());
        consumirSimbolo("(");
        inSpeak = true;
        parseExpressao();
        inSpeak = false;
        consumirSimbolo(")");
    }

    private boolean verificaFormato() {
        return tokenAtual().getToken().equals("{");
    }

    public void parseFormato() {
        consumirSimbolo("{");
        consumirSimbolo(":");
        consumirSimbolo(".");
        consumirPorTipo(TipoToken.NUMERO);
        consumirSimbolo("}");
    }

    private boolean verificaListen() {return verificaPalavra(TipoPalavra.LISTEN.getLexeme());}

    public void parseListen(){
        consumirPalavra(TipoPalavra.LISTEN.getLexeme());
        consumirSimbolo("(");
        parseAlvo();
        consumirSimbolo(")");
    }

    private boolean verificaReturn(){return verificaPalavra(TipoPalavra.RETURN.getLexeme());}

    public void parseReturn() {
        consumirPalavra(TipoPalavra.RETURN.getLexeme());
        if (verificaTipoRetorno()) {
            consumirTipoRetorno();
            parseExpressao();
        }
        consumirSimbolo(";");
    }
    private boolean verificaTipoRetorno() {
        return verificaTipo(TipoToken.PALAVRA_RESERVADA)
                && TipoPalavra.isTipo(tokenAtual().getToken());
    }



    private boolean verificaFor(){return verificaPalavra(TipoPalavra.FOR.getLexeme());}

    public void parseFor(){
        consumirPalavra(TipoPalavra.FOR.getLexeme());
        consumirSimbolo("(");
        if(verificaInitFor()){
            parseInitFor();
        }
        consumirSimbolo(";");
        if(verificaExpressao()){
            parseExpressao();
        }
        consumirSimbolo(";");
        if(verificaAlvo()) {
            parseAtribuicaoOuIncremento();
        }
        consumirSimbolo(")");
        parseBlocoOuComando();
    }

    public void parseBlocoOuComando(){
        if(verificaComando()){
            parseComando();
        }
        else if(verificaBlocoCodigo()){
            parseBlocoCodigo();
        }
        else {
            imprimirErro();
        }
    }

    private boolean verificaBlocoCodigo(){return tokenAtual().getToken().equals("{");}

    public void parseBlocoCodigo(){
        consumirSimbolo("{");
        while(verificaComando()){
            parseComando();
        }
        consumirSimbolo("}");
    }

    private boolean verificaInitFor() {
        return verificaDeclaracaoVar() || verificaAlvo();
    }
    public void parseInitFor() {
        if (verificaDeclaracaoVar()) {
            consumirTipo();
            if (tokenAtual().getToken().equals("*")) consumirSimbolo("*");
            consumirPorTipo(TipoToken.IDENTIFICADOR);
            consumirSimbolo("=");
            parseExpressao();
        } else {
            parseAlvo();
            parseOpAtrib();
            parseExpressao();
        }
    }
    private boolean verificaWhile(){return verificaPalavra(TipoPalavra.WHILE.getLexeme());}

    public void parseWhile(){
        consumirPalavra(TipoPalavra.WHILE.getLexeme());
        consumirSimbolo("(");
        parseExpressao();
        consumirSimbolo(")");
        parseBlocoOuComando();
    }

    private boolean verificaDoWhile(){return verificaPalavra(TipoPalavra.DO.getLexeme());}

    public void parseDoWhile(){
        consumirPalavra(TipoPalavra.DO.getLexeme());
        parseBlocoOuComando();
        consumirPalavra(TipoPalavra.WHILE.getLexeme());
        consumirSimbolo("(");
        parseExpressao();
        consumirSimbolo(")");
        consumirSimbolo(";");
    }
    private boolean verificaIf(){return verificaPalavra(TipoPalavra.IF.getLexeme());}

    public void parseIf(){
        consumirPalavra(TipoPalavra.IF.getLexeme());
        consumirSimbolo("(");
        parseExpressao();
        consumirSimbolo(")");
        parseBlocoOuComando();
        if(verificaPalavra(TipoPalavra.ELSE.getLexeme())){
            consumirPalavra(TipoPalavra.ELSE.getLexeme());
            parseBlocoOuComando();
        }
    }
    private boolean verificaSwitch(){return verificaPalavra(TipoPalavra.SWITCH.getLexeme());}

    public void parseSwitch(){
        consumirPalavra(TipoPalavra.SWITCH.getLexeme());
        consumirSimbolo("(");
        parseExpressao();
        consumirSimbolo(")");
        consumirSimbolo("{");

        while(verificaPalavra(TipoPalavra.CASE.getLexeme())){
            consumirPalavra(TipoPalavra.CASE.getLexeme());
            consumirSimbolo("(");
            parseLiteral();
            consumirSimbolo(")");
            parseBlocoCodigo();
        }
        if(verificaPalavra(TipoPalavra.DEFAULT.getLexeme())){
            consumirPalavra(TipoPalavra.DEFAULT.getLexeme());
            parseBlocoCodigo();
        }
        consumirSimbolo("}");

    }
}

