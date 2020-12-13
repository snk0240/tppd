package Client;

import Dados.Request;

public class EnviaFicheiroThread extends Thread {
    private Request request;
    private ClienteComm comunicacaoComServidor;
    public EnviaFicheiroThread(Request request,ClienteComm cli){
        this.request = request;
        this.comunicacaoComServidor = cli;
    }

    @Override
    public void run() {
        this.comunicacaoComServidor.getTransferenciaFicheiros().LoadFile(
                this.request.getFilename(),
                this.request.getIp(),
                this.request.getPorto(),
                System.getProperty("user.dir"));
    }
}
