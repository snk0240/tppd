package Client;

import Dados.Request;

public class EnviaFicheiroThread extends Thread {
    private Request request;
    private ComunicacaoComServidor comunicacaoComServidor;

    public EnviaFicheiroThread(Request request,ComunicacaoComServidor comunicacaoComServidor){
        this.request = request;
        this.comunicacaoComServidor = comunicacaoComServidor;
    }

    //envia o Ficheiro para o Ip e Porto definidos em Request para o caminho onde o utilizador especificou
    @Override
    public void run() {
        comunicacaoComServidor.transferenciaFicheiros.LoadFile(
                request.getFilename(),
                request.getIp(),
                request.getPorto(),
                comunicacaoComServidor.utilizador.getCaminhoEnviar());
    }
}
