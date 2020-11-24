package Dados;

import java.io.Serializable;
import java.net.InetAddress;

public class Request implements Serializable {

    private static long serialVersionUID=1L;
    private InetAddress ip;
    private int porto;
    private String filename;

    public Request(){
    }

    public Request(InetAddress ip, int porto, String filename) {
        this.ip = ip;
        this.porto = porto;
        this.filename = filename;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPorto() {
        return porto;
    }

    public void setPorto(int porto) {
        this.porto = porto;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
