package Dados;

import java.io.Serializable;

public class FileRequest implements Serializable {
    private static long serialVersionUID = 1L;

    private String filename;

    public FileRequest(){
    }

    public FileRequest(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
