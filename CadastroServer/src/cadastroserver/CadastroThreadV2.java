/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import controller.exceptions.NonexistentEntityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Usuario;
import model.Movimento;
import model.Produto;

/**
 *
 * @author Mari
 */
public class CadastroThreadV2 extends Thread {
    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private MovimentoJpaController ctrlMov;
    private PessoaJpaController ctrlPessoa;
    private Socket s1;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public CadastroThreadV2(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.s1 = s1;
    }

    @Override
    public void run() {
        String login = "anonimo";
        try {
            out = new ObjectOutputStream(s1.getOutputStream());
            in = new ObjectInputStream(s1.getInputStream());

            System.out.println("Cliente conectado, aguardando login e senha.");
            
            login = (String) in.readObject();
            String senha = (String) in.readObject();

            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                System.out.println("Usuário inválido. Login="+ login +", Senha="+ senha);
                out.writeObject("Usuário inválido.");
                return;
            }

            System.out.println("Usuário "+ login +" conectado com sucesso.");
            out.writeObject("Usuário conectado com sucesso.");
            
            while (true) {
                System.out.println("Aguardando comandos...");
                Character comando = in.readChar();
                
                if (comando == 'L') {
                    System.out.println("Comando recebido, listando produtos.");
                    out.writeObject(ctrl.findProdutoEntities());
                } else if (comando == 'E' || comando == 'S') {
                    int idPessoa = (int) in.readObject();
                    int idProduto = (int) in.readObject();
                    int quantidade = (int) in.readObject();
                    long valorUnitario = (long) in.readObject();

                    Produto produto = ctrl.findProduto(idProduto);
                    if (produto == null) {
                        out.writeObject("Produto inválido.");
                        continue;
                    }

                    Movimento movimento = new Movimento();
                    movimento.setTipo(comando);
                    movimento.setUsuarioidUsuario(usuario);
                    movimento.setPessoaidPessoa(ctrlPessoa.findPessoa(idPessoa));
                    movimento.setProdutoidProduto(produto);
                    movimento.setQuantidade(quantidade);
                    movimento.setValorUnitario(valorUnitario);

                    ctrlMov.create(movimento);

                    if (comando.equals('E')) {
                        produto.setQuantidade(produto.getQuantidade() + quantidade);
                    } else if (comando.equals('S')) {
                        produto.setQuantidade(produto.getQuantidade() - quantidade);
                    }

                    ctrl.edit(produto);

                    out.writeObject("Movimento registrado com sucesso.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
