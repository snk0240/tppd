package Dados;

import java.io.Serializable;

public class Ficheiro implements Serializable {
    private static long serialVersionUID=1L;
    private String nome;
    private Long tamanho;

    public Ficheiro(){
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }
}
