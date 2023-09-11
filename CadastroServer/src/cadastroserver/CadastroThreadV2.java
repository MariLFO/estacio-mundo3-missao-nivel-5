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
    private Usuario usuario;

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
            usuario = ctrlUsu.findUsuario(login, senha);

            if (usuario == null) {
                System.out.println("Usuário inválido. Login="+ login +", Senha="+ senha);
                out.writeObject("Usuário inválido.");
                return;
            }

            System.out.println("Usuário "+ login +" conectado com sucesso.");
            out.writeObject("Usuário conectado com sucesso.");
            out.flush();
            
            Boolean continuaProcesso = true;
            while (continuaProcesso) {
                continuaProcesso = processaComando();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            System.out.println("Conexão com " + login +" finalizada.");
        }
    }

    private Boolean processaComando() throws Exception {
        System.out.println("Aguardando comandos...");
        Character comando = in.readChar();

        switch (comando) {
            case 'L':
                System.out.println("Comando recebido, listando produtos.");
                out.writeObject(ctrl.findProdutoEntities());
                return true;
            case 'E':
            case 'S':
                System.out.println("Comando Movimento tipo ["+ comando +"] recebido.");
                int idPessoa = in.readInt();
                int idProduto = in.readInt();
                int quantidade = in.readInt();
                long valorUnitario = in.readLong();

                Produto produto = ctrl.findProduto(idProduto);
                if (produto == null) {
                    out.writeObject("Produto inválido.");
                    return true;
                }
                
                if (comando.equals('E')) {
                    produto.setQuantidade(produto.getQuantidade() + quantidade);
                } else if (comando.equals('S')) {
                    produto.setQuantidade(produto.getQuantidade() - quantidade);
                }

                ctrl.edit(produto);

                Movimento movimento = new Movimento();
                movimento.setTipo(comando);
                movimento.setUsuarioidUsuario(usuario);
                movimento.setPessoaidPessoa(ctrlPessoa.findPessoa(idPessoa));
                movimento.setProdutoidProduto(produto);
                movimento.setQuantidade(quantidade);
                movimento.setValorUnitario(valorUnitario);

                ctrlMov.create(movimento);
                out.writeObject("Movimento registrado com sucesso.");
                out.flush();
                System.out.println("Movimento registrado com sucesso.");
                return true;
            case 'X':
                return false;
            default:
                System.out.println("Opção inválida!");
                return true;
        }
    }
    
    private void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (s1 != null) {
                s1.close();
            }
        } catch (IOException ex) {
            System.out.println("Falha ao fechar conexão.");
        }
    }
}
