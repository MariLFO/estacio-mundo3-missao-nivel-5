/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
/**
 *
 * @author Mari
 */
public class CadastroServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        int serverPort = 4321; // Porta na qual o servidor irá ouvir as conexões
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        ProdutoJpaController ctrl = new ProdutoJpaController(emf);
        UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
        MovimentoJpaController ctrlMov = new MovimentoJpaController(emf);
        PessoaJpaController ctrlPessoa = new PessoaJpaController(emf);
        ServerSocket serverSocket = new ServerSocket(serverPort); // Cria um socket de servidor que escuta na porta especificada por conexões recebidas

        System.out.println("Servidor aguardando conexões...");
        
        // Loop infinito para continuamente aceitar e processar conexões de clientes recebidas
        while (true) {
            // Aguarda um cliente se conectar e aceita a conexão (chamada bloqueante)
            Socket clienteSocket = serverSocket.accept();
            System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());
            
            // CadastroThread V1:
            // CadastroThread thread = new CadastroThread(ctrl, ctrlUsu, clienteSocket);
            
            // CadastroThread V2:
            CadastroThreadV2 thread = new CadastroThreadV2(ctrl, ctrlUsu, ctrlMov, ctrlPessoa, clienteSocket);
            
            thread.start();
            System.out.println("Aguardando nova conexão...");
        }
    }
}
