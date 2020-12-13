package Server;

import java.io.Serializable;

class MsgMulticast implements Serializable {
    protected String tipoMsg;
    protected String msg;
    protected int port;

    public MsgMulticast(String tipo, String msg) {
        this.tipoMsg = tipo;
        this.msg = msg;
    }

    public MsgMulticast(String tipo, int port) {
        this.tipoMsg = tipo;
        this.port = port;
    }

    public String getTipoMsg() {
        return tipoMsg;
    }

    public String getMsg() {
        return msg;
    }
}
