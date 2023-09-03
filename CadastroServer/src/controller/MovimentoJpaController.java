/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Movimento;
import model.Pessoa;
import model.Produto;
import model.Usuario;

/**
 *
 * @author Mari
 */
public class MovimentoJpaController implements Serializable {

    public MovimentoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movimento movimento) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa pessoaidPessoa = movimento.getPessoaidPessoa();
            if (pessoaidPessoa != null) {
                pessoaidPessoa = em.getReference(pessoaidPessoa.getClass(), pessoaidPessoa.getIdPessoa());
                movimento.setPessoaidPessoa(pessoaidPessoa);
            }
            Produto produtoidProduto = movimento.getProdutoidProduto();
            if (produtoidProduto != null) {
                produtoidProduto = em.getReference(produtoidProduto.getClass(), produtoidProduto.getIdProduto());
                movimento.setProdutoidProduto(produtoidProduto);
            }
            Usuario usuarioidUsuario = movimento.getUsuarioidUsuario();
            if (usuarioidUsuario != null) {
                usuarioidUsuario = em.getReference(usuarioidUsuario.getClass(), usuarioidUsuario.getIdUsuario());
                movimento.setUsuarioidUsuario(usuarioidUsuario);
            }
            em.persist(movimento);
            if (pessoaidPessoa != null) {
                pessoaidPessoa.getMovimentoCollection().add(movimento);
                pessoaidPessoa = em.merge(pessoaidPessoa);
            }
            if (produtoidProduto != null) {
                produtoidProduto.getMovimentoCollection().add(movimento);
                produtoidProduto = em.merge(produtoidProduto);
            }
            if (usuarioidUsuario != null) {
                usuarioidUsuario.getMovimentoCollection().add(movimento);
                usuarioidUsuario = em.merge(usuarioidUsuario);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Movimento movimento) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimento persistentMovimento = em.find(Movimento.class, movimento.getIdMovimento());
            Pessoa pessoaidPessoaOld = persistentMovimento.getPessoaidPessoa();
            Pessoa pessoaidPessoaNew = movimento.getPessoaidPessoa();
            Produto produtoidProdutoOld = persistentMovimento.getProdutoidProduto();
            Produto produtoidProdutoNew = movimento.getProdutoidProduto();
            Usuario usuarioidUsuarioOld = persistentMovimento.getUsuarioidUsuario();
            Usuario usuarioidUsuarioNew = movimento.getUsuarioidUsuario();
            if (pessoaidPessoaNew != null) {
                pessoaidPessoaNew = em.getReference(pessoaidPessoaNew.getClass(), pessoaidPessoaNew.getIdPessoa());
                movimento.setPessoaidPessoa(pessoaidPessoaNew);
            }
            if (produtoidProdutoNew != null) {
                produtoidProdutoNew = em.getReference(produtoidProdutoNew.getClass(), produtoidProdutoNew.getIdProduto());
                movimento.setProdutoidProduto(produtoidProdutoNew);
            }
            if (usuarioidUsuarioNew != null) {
                usuarioidUsuarioNew = em.getReference(usuarioidUsuarioNew.getClass(), usuarioidUsuarioNew.getIdUsuario());
                movimento.setUsuarioidUsuario(usuarioidUsuarioNew);
            }
            movimento = em.merge(movimento);
            if (pessoaidPessoaOld != null && !pessoaidPessoaOld.equals(pessoaidPessoaNew)) {
                pessoaidPessoaOld.getMovimentoCollection().remove(movimento);
                pessoaidPessoaOld = em.merge(pessoaidPessoaOld);
            }
            if (pessoaidPessoaNew != null && !pessoaidPessoaNew.equals(pessoaidPessoaOld)) {
                pessoaidPessoaNew.getMovimentoCollection().add(movimento);
                pessoaidPessoaNew = em.merge(pessoaidPessoaNew);
            }
            if (produtoidProdutoOld != null && !produtoidProdutoOld.equals(produtoidProdutoNew)) {
                produtoidProdutoOld.getMovimentoCollection().remove(movimento);
                produtoidProdutoOld = em.merge(produtoidProdutoOld);
            }
            if (produtoidProdutoNew != null && !produtoidProdutoNew.equals(produtoidProdutoOld)) {
                produtoidProdutoNew.getMovimentoCollection().add(movimento);
                produtoidProdutoNew = em.merge(produtoidProdutoNew);
            }
            if (usuarioidUsuarioOld != null && !usuarioidUsuarioOld.equals(usuarioidUsuarioNew)) {
                usuarioidUsuarioOld.getMovimentoCollection().remove(movimento);
                usuarioidUsuarioOld = em.merge(usuarioidUsuarioOld);
            }
            if (usuarioidUsuarioNew != null && !usuarioidUsuarioNew.equals(usuarioidUsuarioOld)) {
                usuarioidUsuarioNew.getMovimentoCollection().add(movimento);
                usuarioidUsuarioNew = em.merge(usuarioidUsuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = movimento.getIdMovimento();
                if (findMovimento(id) == null) {
                    throw new NonexistentEntityException("The movimento with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimento movimento;
            try {
                movimento = em.getReference(Movimento.class, id);
                movimento.getIdMovimento();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimento with id " + id + " no longer exists.", enfe);
            }
            Pessoa pessoaidPessoa = movimento.getPessoaidPessoa();
            if (pessoaidPessoa != null) {
                pessoaidPessoa.getMovimentoCollection().remove(movimento);
                pessoaidPessoa = em.merge(pessoaidPessoa);
            }
            Produto produtoidProduto = movimento.getProdutoidProduto();
            if (produtoidProduto != null) {
                produtoidProduto.getMovimentoCollection().remove(movimento);
                produtoidProduto = em.merge(produtoidProduto);
            }
            Usuario usuarioidUsuario = movimento.getUsuarioidUsuario();
            if (usuarioidUsuario != null) {
                usuarioidUsuario.getMovimentoCollection().remove(movimento);
                usuarioidUsuario = em.merge(usuarioidUsuario);
            }
            em.remove(movimento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Movimento> findMovimentoEntities() {
        return findMovimentoEntities(true, -1, -1);
    }

    public List<Movimento> findMovimentoEntities(int maxResults, int firstResult) {
        return findMovimentoEntities(false, maxResults, firstResult);
    }

    private List<Movimento> findMovimentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Movimento.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Movimento findMovimento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movimento.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovimentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Movimento> rt = cq.from(Movimento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
