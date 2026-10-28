package ec.comp.model;

public class Token {
    private TipoToken tipoToken;
    private String token;
    private int linha;
    private int coluna;

    public Token(TipoToken tipoToken, String token, int linha, int coluna) {
        this.tipoToken = tipoToken;
        this.token = token;
        this.linha = linha;
        this.coluna = coluna;
    }

    
    public TipoToken getTipoToken() {
        return tipoToken;
    }


    public void setTipoToken(TipoToken tipoToken) {
        this.tipoToken = tipoToken;
    }


    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public int getLinha() {
        return linha;
    }


    public void setLinha(int linha) {
        this.linha = linha;
    }


    public int getColuna() {
        return coluna;
    }


    public void setColuna(int coluna) {
        this.coluna = coluna;
    }


    public String toString() {
            return "<" + tipoToken + ", \"" + token + "\">";
        }
}
