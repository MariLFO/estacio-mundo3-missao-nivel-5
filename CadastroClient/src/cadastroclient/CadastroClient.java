/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import model.Produto;

/**
 *
 * @author Mari
 */
public class CadastroClient {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        String serverAddress = "localhost"; // Endereço do servidor (pode ser substituído pelo IP)
        int serverPort = 4321;
        Socket socket = new Socket(serverAddress, serverPort);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // Login, passando usuário "op1"
        out.writeObject("op1");
        
        // Senha para o login usando "op1"
        out.writeObject("op1");
        
        // Lê resultado do login:
        System.out.println((String)in.readObject());

        // Lista produtos:
        out.writeObject("L");

        List<Produto> produtos = (List<Produto>) in.readObject();
        for (Produto produto : produtos) {
            System.out.println(produto.getNome());
        }

        out.close();
        in.close();
        socket.close();
    }
}
