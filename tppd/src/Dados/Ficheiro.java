package Dados;

import java.io.Serializable;

public class Ficheiro implements Serializable {
    private static long serialVersionUID=1L;

    private String username;
    private String caminho;
    private long tamanho;
    private int tipo;

    public Ficheiro(){
    }

    public Ficheiro(String user, String caminho, long tam, int t){
        this.username=user;//username do utilizador que enviou o ficheiro
        this.caminho=caminho;//caminho onde o ficheiro se encontra
        this.tamanho=tam;//tamanho em bytes do ficheiro
        this.tipo=t;//-1 ficheiro publico, 0 ficheiro para canal, 1 ficheiro para user -> ???
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public long getTamanho() {
        return tamanho;
    }

    public void setTamanho(long tamanho) {
        this.tamanho = tamanho;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
