/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.com.icontaweb.contratos.controladores;

/**
 *
 * @author jcperan
 */

import es.com.icontaweb.contratos.modelos.Visitas;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

/**
 * Controlador para la gestión de la agenda
 * @author jcperan
 */
@ManagedBean(name = "ControladorPreventivo")
@SessionScoped
public class ControladorPreventivo implements Serializable {
    
    /**
     * Objetos de la Persistencia de Datos
     */
    @PersistenceUnit
    protected EntityManagerFactory emf;

    @PersistenceContext
    protected EntityManager em;

    public EntityManager entityManager() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("contratos");
        }
        return emf.createEntityManager();
    }

    @ManagedProperty(value = "#{ControladorIntranet}")
    private ControladorIntranet controladorIntranet = new ControladorIntranet();
    public void setControladorIntranet(ControladorIntranet controladorIntranet) { this.controladorIntranet = controladorIntranet; }    
    public ControladorIntranet getControladorIntranet() { return controladorIntranet; }
    
    private List<Visitas> listaPreventivo = new ArrayList<Visitas>();

    public List<Visitas> getListaPreventivo() {

        em = entityManager();   
        try {
            listaPreventivo.clear();
            
            int idCliente = getControladorIntranet().getCliente().getId();
            String sqlquery  = "select v from Visitas v where v.clientes.id = :cliente ";
                   sqlquery += "and v.motivos.id = 1 ";
                   sqlquery += "and v.visible  = 1 ";
                   sqlquery += "order by v.fecha desc";
            Query q = em.createQuery(sqlquery);
            q.setParameter("cliente", idCliente);
            q.setMaxResults(5);
            listaPreventivo = q.getResultList();
        } catch (Exception e) {
            System.out.println("INTRANET " + (new Date()).toString() + " " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
        
        return listaPreventivo;
    }

}
