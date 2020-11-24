package Dados;

import java.io.Serializable;

public class TransferableObject implements Serializable {
    private static long serialVersionUID=1L;
    private byte[] bytes =null;
    private String filename;

    public TransferableObject(){

    }

    public TransferableObject(byte[] bytes, String filename) {
        this.bytes = bytes;
        this.filename = filename;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getfilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
