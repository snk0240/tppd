package Dados;

import java.io.Serializable;

public class FileRequest implements Serializable {
    private static long serialVersionUID = 1L;
    private String filename;
    private String usernameSource; //username do user q tem o ficheiro
    private String usernameRequest; // username do user q pede o ficheiro

    public FileRequest(){

    }

    public FileRequest(String filename, String usernameSource, String usernameRequest) {
        this.filename = filename;
        this.usernameSource = usernameSource;
        this.usernameRequest = usernameRequest;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUsernameSource() {
        return usernameSource;
    }

    public void setUsernameSource(String usernameSource) {
        this.usernameSource = usernameSource;
    }

    public String getUsernameRequest() {
        return usernameRequest;
    }

    public void setUsernameRequest(String usernameRequest) {
        this.usernameRequest = usernameRequest;
    }
}
