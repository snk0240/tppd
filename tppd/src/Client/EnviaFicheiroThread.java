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
        comunicacaoComServidor.transferenciaFicheiros.LoadFile(
                request.getFilename(),
                request.getIp(),
                request.getPorto(),
                System.getProperty("user.dir"));
    }
}
