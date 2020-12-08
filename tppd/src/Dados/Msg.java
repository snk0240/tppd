package Dados;

import java.io.Serializable;

public class Msg implements Serializable {
    private static long serialVersionUID = 1L;
    private String mensagem;
    private String source;
    private String destino;

    public Msg(){
    }

    public Msg(String msg,String source,String destino){
        mensagem=msg;//texto com mensagem
        this.source=source;//ip do que enviou
        this.destino=destino;//ip do que vai receber
    }

    public String getMensagem(){
        return mensagem;
    }
    public void setSource(String username){
        source=username;
    }
    public void setDestino(String destino){
        this.destino=destino;
    }
    public String getSource(){
        return source;
    }
    public String getDestino(){
        return destino;
    }
    public void setMsg(String msg){
        mensagem=msg;
    }
}
