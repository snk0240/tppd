package Client;
import Dados.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    ComunicacaoComServidor comunicacaoComServidor;
    TransferenciaFicheiros transferenciaFicheiros;
    Scanner scanner;
    Login login;
    Database database = new Database();

    public Client(ComunicacaoComServidor comunicacaoComServidor,TransferenciaFicheiros transferenciaFicheiros) {

        this.comunicacaoComServidor = comunicacaoComServidor;
        this.transferenciaFicheiros = transferenciaFicheiros;

        scanner = new Scanner(System.in);
        login = new Login();
    }

    public void corre()
    {
        int escolha;
        comunicacaoComServidor.comecaThread();

        do {
            System.out.println(
                    "Selecione uma das opções:\n" +
                    "1-Login\n" +
                    "2-Registar\n" +
                    "3-Sair");
            escolha = scanner.nextInt();
            switch (escolha)
            {
                case 1:
                    comunicacaoComServidor.login(fazlogin());
                    if(comunicacaoComServidor.autenticado)
                        System.out.println("Utilizador autenticado com sucesso!\n");
                    else
                        //System.out.println("Login falhou! Tente outra vez...\n");
                        break;
                case 2:
                    comunicacaoComServidor.registo(regista());
                    if(comunicacaoComServidor.autenticado)
                        System.out.println("Utilizador registado com sucesso!\n");
                    else
                        //System.out.println("Registo falhou! Tente outra vez...\n");
                        break;
                case 3:
                    System.exit(0);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(!comunicacaoComServidor.autenticado);

        String str;
        boolean sair = false;
        do
        {
            System.out.println(
                    "Selecione uma das opções:\n" +
                    "1-Enviar Mensagem\n" +
                    "2-Listar Utilizadores\n" +
                    "3-Listar Ficheiros\n" +
                    "4-Escolher ficheiro para download\n" +
                    "5-Atualizar base de dados com nova informação\n" +
                    "6-Obter database\n" +
                    "7-Mostrar Database\n" +
                    "8-sair");
            escolha = scanner.nextInt();
            switch(escolha)
            {
                case 1:
                    comunicacaoComServidor.enviamensagem(getmensagem());
                    break;
                case 2:
                    comunicacaoComServidor.listautilizadores();
                    break;
                case 3:
                    comunicacaoComServidor.listaficheiros();
                    break;
                case 4:
                    comunicacaoComServidor.downloadficheiro(escolheficheiro());
                    break;
                case 5:
                    comunicacaoComServidor.updateDatabase();
                    break;
                case 6:
                    comunicacaoComServidor.getDatabase();
                    break;
                case 7:
                    comunicacaoComServidor.mostraDatabase();
                    break;
                case 8:
                    System.exit(0);
                    break;
            }

        }while(!sair);

    }
    public Msg getmensagem(){
        Msg msg = new Msg();
        String destino,mensagem;

        msg.setSource(comunicacaoComServidor.utilizador.getUsername());
        try {
            System.out.println("Indique o utilizador para o qual quer enviar mensagem:\n");
            destino = scanner.nextLine();
            System.out.println("Escreva a mensagem:\n");
            mensagem= scanner.nextLine();
            msg.setDestino(destino);
            msg.setMsg(mensagem);
        } catch (InputMismatchException e){
            System.out.println("Gerou um erro a declarar os portos.");
        } catch (Exception e){
            System.out.println("Gerou um erro a declarar qualquer coisa.");
        }
        return msg;
    }
    public FileRequest escolheficheiro(){
        FileRequest fr = new FileRequest();
        String filename,source;

        fr.setUsernameRequest(comunicacaoComServidor.utilizador.getUsername());

        try {
            System.out.println("Indique o ficheiro  transferir:\n");
            filename = scanner.nextLine();

            System.out.println("Insira o user que tem o ficheiro:\n");
            source = scanner.nextLine();
            fr.setUsernameSource(source);
            fr.setFilename(filename);
        } catch (InputMismatchException e){
            System.out.println("Gerou um erro a declarar os portos.");
        } catch (Exception e){
            System.out.println("Gerou um erro a declarar qualquer coisa.");
        }
        return fr;
    }
    public Utilizador regista(){
        int portoTCP = 5555, portoUDP = 5555;
        String password, username, caminhoEnviar, caminhoReceber, nome;
        Utilizador util = new Utilizador();

        password = username = caminhoEnviar = caminhoReceber = nome = null;
        scanner.nextLine();
        try {
            System.out.println("Insira o porto TCP:\n");
            portoTCP = scanner.nextInt();
            if(portoTCP < 1023 || portoTCP > 49152)     // portos válidos
                portoTCP = 55555;

            System.out.println("Insira o porto UDP:\n");
            portoUDP = scanner.nextInt();
            if(portoUDP < 1023 || portoUDP > 49152)     // portos válidos
                portoUDP = 55555;

            username = scanner.nextLine();
            System.out.println("Insira o username:\n");
            username = scanner.nextLine();

            System.out.println("Insira a password:\n");
            password = scanner.nextLine();

            System.out.println("Insira o caminho para onde enviar:\n");
            caminhoEnviar = scanner.nextLine();
            caminhoEnviar = caminhoEnviar.replace("\\","\\\\");

            System.out.println("Insira o caminho para onde receber:\n");
            caminhoReceber = scanner.nextLine();
            caminhoReceber = caminhoReceber.replace("\\","\\\\");

            System.out.println("Insira o nome:\n");
            nome = scanner.nextLine();

        } catch (InputMismatchException e){
            System.out.println("Gerou um erro a declarar os portos.");
        } catch (Exception e){
            System.out.println("Gerou um erro a declarar qualquer coisa.");
        }

        util.setPortoTCP(portoTCP);
        util.setPortoUDP(portoUDP);
        util.setUsername(username);
        util.setPassword(password);
        util.setCaminhoEnviar(caminhoEnviar);
        util.setCaminhoReceber(caminhoReceber);
        util.setNome(nome);

        //comunicacaoComServidor.getDatabase();
        return util;
    }
    public Login fazlogin(){
        scanner.nextLine();
        System.out.print("Introduza o seu username: ");
        login.setUsername(scanner.nextLine());
        System.out.print("Introduza a sua password: ");
        login.setPassword(scanner.nextLine());

        return login;
    }

    public static void main(String[] args)
    {
        Socket socketServidor;

        if(args.length != 2){
            System.out.println("Sintaxe: java Client <IP> <portoTCPServidor>");
            return;
        }

        try {
            InetAddress serverAddr = InetAddress.getByName(args[0]);
            socketServidor = new Socket(serverAddr,Integer.parseInt(args[1]));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        TransferenciaFicheiros transferenciaFicheiros = new TransferenciaFicheiros();
        ComunicacaoComServidor comunicacaoComServidor = new ComunicacaoComServidor(socketServidor,transferenciaFicheiros);

        Client cli = new Client(comunicacaoComServidor,transferenciaFicheiros);
        cli.corre();
    }
}