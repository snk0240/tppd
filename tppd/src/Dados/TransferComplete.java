package Dados;


import java.io.Serializable;

public class TransferComplete implements Serializable {
    private static long serialVersionUID =1L;
    private String sender;
    private String receiver;
    private String ficheiro;

    public TransferComplete(){

    }
    public TransferComplete(String sender,String receiver,String ficheiro){
        this.sender= sender;
        this.receiver = receiver;
        this.ficheiro=ficheiro;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getFicheiro(){
        return ficheiro;
    }

    public void setFicheiro(String ficheiro){
        this.ficheiro=ficheiro;
    }
}
