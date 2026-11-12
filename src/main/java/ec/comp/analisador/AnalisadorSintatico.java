package ec.comp.analisador;

import ec.comp.model.TipoPalavra;
import ec.comp.model.TipoToken;
import ec.comp.model.Token;

import java.util.ArrayList;
import java.util.List;

public class AnalisadorSintatico {
    private final List<Token> tokens;
    private int pos = 0;

    public AnalisadorSintatico(List<Token> tokens){
        this.tokens = tokens;
    }

    private Token tokenAtual() {
        if(pos >= tokens.size()){
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(pos);
    }

    private Token consumirPorTipo(TipoToken tipoEsperado){
        Token token = tokenAtual();
        if(token.getTipoToken() == tipoEsperado){
            pos++;
            return token;
        }
        throw new RuntimeException(String.format(
           "Erro Sintático [Linha " + token.getLinha() + ", Coluna " + token.getColuna() +
           "]: Token esperado: " + tipoEsperado
        ));
    }

    private Token consumirPalavra(String palavra){
        Token token = tokenAtual();
        if(token.getTipoToken() == TipoToken.PALAVRA_RESERVADA && token.getToken().equals(palavra)){
                pos++;
                return token;
        }
        throw new RuntimeException(String.format(
                "Erro Sintático [Linha " + token.getLinha() + ", Coluna " + token.getColuna() +
                        "]: Token esperado: " + palavra
        ));
    }

    private Token consumirSimbolo(String sim){
        Token token = tokenAtual();

        if(token.getToken().equals(sim)){
            pos++;
            return token;
        }
        throw new RuntimeException(String.format(
                "Erro Sintático [Linha " + token.getLinha() + ", Coluna " + token.getColuna() +
                        "]: Token esperado: " + sim
        ));
    }

    private Token consumirTipo(){
        Token token = tokenAtual();
        if(token.getTipoToken() == TipoToken.PALAVRA_RESERVADA && TipoPalavra.isTipo(token.getToken())){
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

    private boolean verificaPalavra(String p){
        return TipoPalavra.isPalavra(tokenAtual().getToken()) && tokenAtual().getToken().equals(p);
    }

    private void imprimirErro(){
        throw new RuntimeException(String.format(
                "Erro Sintático [Linha " + tokenAtual().getLinha() + ", Coluna" + tokenAtual().getColuna() +
                        "]"
        ));
    }

    public void parsePrograma(){
        while(!verificaTipo(TipoToken.FIM)){
            if(verificaProcessoGlobal()){
                parseProcessoGlobal();
            }
            else if(verificaDeclaracao()){
                parseDeclaracao();
            }
            else if(verificaRitual()){
                parseRitual();
            }
            else {imprimirErro();}


        }
        consumirPorTipo(TipoToken.FIM);
        // CÓDIGO ANALISADO COM SUCESSO!
    }

    private boolean verificaProcessoGlobal(){
        return (verificaBiblioteca() || verificaPalavra(TipoPalavra.CONST.getLexeme()));
    }

    public void parseProcessoGlobal(){
        if(verificaBiblioteca()){
            parseBiblioteca();
        }
        else if(verificaPalavra(TipoPalavra.CONST.getLexeme())){
            parseConstante();
        }
        else {imprimirErro();}
    }

    private boolean verificaBiblioteca(){
        return verificaPalavra(TipoPalavra.INCLUDE.getLexeme());
    }
    public void parseBiblioteca(){
        consumirPalavra(TipoPalavra.INCLUDE.getLexeme());
        consumirSimbolo("<");
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        consumirSimbolo(">");
    }

    public void parseConstante(){
        consumirPalavra(TipoPalavra.CONST.getLexeme());
        consumirTipo();
        consumirPorTipo(TipoToken.IDENTIFICADOR);
        parseLiteral();
        consumirSimbolo(";");

    }

    private boolean verificaLiteral(){
        return (verificaTipo(TipoToken.OPERADOR) && tokenAtual().getToken().equals("-")
                || verificaTipo(TipoToken.NUMERO) || verificaTipo(TipoToken.STRING)
                || verificaPalavra(TipoPalavra.TRUE.getLexeme())
                || verificaPalavra(TipoPalavra.FALSE.getLexeme())
                || verificaPalavra(TipoPalavra.NULL.getLexeme()));
    }

    public void parseLiteral(){
        Token token = tokenAtual();

        if (verificaTipo(TipoToken.OPERADOR) && tokenAtual().getToken().equals("-")) {
            consumirPorTipo(TipoToken.OPERADOR);
            consumirPorTipo(TipoToken.NUMERO);
        }
        else if (verificaTipo(TipoToken.NUMERO)) {
            consumirPorTipo(TipoToken.NUMERO);
        }
        else if(verificaTipo(TipoToken.STRING)){
            consumirPorTipo(TipoToken.STRING);
        }
        else if(verificaPalavra(TipoPalavra.TRUE.getLexeme())){
            consumirPalavra(TipoPalavra.TRUE.getLexeme());
        }
        else if(verificaPalavra(TipoPalavra.FALSE.getLexeme())){
            consumirPalavra(TipoPalavra.FALSE.getLexeme());
        }
        else if(verificaPalavra(TipoPalavra.NULL.getLexeme())){
            consumirPalavra(TipoPalavra.NULL.getLexeme());
        }

        else{imprimirErro();}
    }

}
