package Client;

import Dados.Login;
import Dados.Utilizador;

import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientMain {

    private ClienteComm cli;
    private TransferenciaFicheiros transferenciaFicheiros;
    private Login login;

    ClientMain(ClienteComm cli, TransferenciaFicheiros transferenciaFicheiros){
        this.cli = cli;
        this.transferenciaFicheiros = transferenciaFicheiros;
        this.login = new Login();
    }

    public Login fazlogin(){
        Scanner scan = new Scanner(System.in);
        scan.nextLine();
        System.out.print("Introduza o seu username: ");
        this.login.setUsername(scan.nextLine());
        System.out.print("Introduza a sua password: ");
        this.login.setPassword(scan.nextLine());
        return this.login;
    }

    public Utilizador regista(){
        int portoTCP = this.cli.getTcp_port_server();
        int portoUDP = this.cli.getUdp_port_server();
        try {
            InetAddress ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String password, username, nome , imagem;
        Utilizador util = new Utilizador();
        Scanner scanner = new Scanner(System.in);

        password = username = nome = imagem = null;
        scanner.nextLine();
        try {
            username = scanner.nextLine();
            System.out.println("Insira o username:\n");
            username = scanner.nextLine();

            System.out.println("Insira a password:\n");
            password = scanner.nextLine();

            System.out.println("Insira o nome:\n");
            nome = scanner.nextLine();

            System.out.println("Insira o caminho da imagem:\n");
            imagem = scanner.nextLine();

        } catch (InputMismatchException e){
            System.err.println("Some port declaration error.");
        } catch (Exception e){
            System.err.println("Some declaration error.");
        }

        util.setPortoTCP(portoTCP);
        util.setPortoUDP(portoUDP);
        util.setUsername(username);
        util.setPassword(password);
        util.setImagem(imagem);
        util.setNome(nome);
        util.setAtivo(true);

        this.cli.getDatabase();
        return util;
    }

    public void comeca()
    {
        Scanner scan = new Scanner(System.in);
        int escolha;
        do {
            System.out.println("Selecione uma das opções:\n" +
                    "1-Login\n" +
                    "2-Registar\n" +
                    "3-Sair\n> ");
            escolha = scan.nextInt();
            switch (escolha) {
                case 1:
                    /*cli.login(fazlogin());
                    if(cli.autenticado)
                        System.out.println("Utilizador autenticado com sucesso!\n");
                    else
                        System.out.println("Login falhou! Tente outra vez...\n");*/
                        break;
                case 2:
                    this.cli.registo(regista());
                    if(this.cli.isAutenticado())
                        System.out.println("Utilizador registado com sucesso!\n");
                    else
                        System.out.println("Registo falhou! Tente outra vez...\n");
                        break;
                case 3:
                    System.exit(0);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while(/*!cli.autenticado*/escolha!=5);

        boolean sair = false;
        do
        {
            System.out.println("Selecione uma das opções:\n" +
                    "1-Enviar Mensagem\n" +
                    "2-Listar Utilizadores\n" +
                    "3-Listar Ficheiros\n" +
                    "4-Escolher ficheiro para download\n" +
                    "5-Atualizar base de dados com nova informação\n" +
                    "6-Obter database\n" +
                    "7-Mostrar Database\n" +
                    "8-sair");
            escolha = scan.nextInt();
            switch(escolha) {
                case 1:
                    //cli.enviamensagem(getmensagem());
                    break;
                case 2:
                    //cli.listautilizadores();
                    break;
                case 3:
                    //cli.listaficheiros();
                    break;
                case 4:
                    //cli.downloadficheiro(escolheficheiro());
                    break;
                case 5:
                    //cli.updateDatabase();
                    break;
                case 6:
                    //cli.getDatabase();
                    break;
                case 7:
                    //cli.mostraDatabase();
                    break;
                case 8:
                    System.exit(0);
                    break;
            }
        }while(!sair);
    }

    public static void main(String[] args) throws UnknownHostException {
        if(args.length != 2){
            System.err.println("Sintaxe: java Client <IP_SERVIDOR> <UDP_PORT_SERVIDOR>");
            return;
        } System.out.println("Server IP is "+args[0]+" and Server UDP Port is " + args[1] + "\n");

        TransferenciaFicheiros transferenciaFicheiros = new TransferenciaFicheiros();
        ClienteComm cli = new ClienteComm(InetAddress.getByName(args[0]), Integer.parseInt(args[1]), transferenciaFicheiros);
        cli.start();
        //COMECA O MENU DO CLIENTE
        ClientMain cli_main = new ClientMain(cli, transferenciaFicheiros);
        cli_main.comeca();
    }
}