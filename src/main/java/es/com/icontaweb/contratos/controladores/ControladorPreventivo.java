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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import org.primefaces.event.SelectEvent;

/**
 * Controlador para la gestión de la lista de Trabajos Preventivos
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
    
    private Date fechaDesde = new Date(120, 0, 1);
    public Date getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(Date fechaDesde) { this.fechaDesde = fechaDesde; }

    private Date fechaHasta = new Date();
    public Date getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(Date fechaHasta) { this.fechaHasta = fechaHasta; }
    
/*    
    public void onDateSelect(SelectEvent<LocalDate> event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", event.getObject().format(formatter)));
    }
*/
    
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

    private List<Visitas> datosPreventivo = new ArrayList<Visitas>();

    public List<Visitas> getDatosPreventivo() {

        em = entityManager();   
        try {
            datosPreventivo.clear();
            
            int idCliente = getControladorIntranet().getCliente().getId();
            String sqlquery  = "select v from Visitas v where v.clientes.id = :cliente ";
                   sqlquery += "and v.fecha >= :fechaDesde ";
                   sqlquery += "and v.fecha <= :fechaHasta ";
                   sqlquery += "and v.motivos.id = 1 ";
                   sqlquery += "and v.visible  = 1 ";
                   sqlquery += "order by v.fecha desc";
            Query q = em.createQuery(sqlquery);
            q.setParameter("cliente", idCliente);
            q.setParameter("fechaDesde", fechaDesde);
            q.setParameter("fechaHasta", fechaHasta);
            datosPreventivo = q.getResultList();
        } catch (Exception e) {
            System.out.println("INTRANET " + (new Date()).toString() + " " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
        
        return datosPreventivo;
    }

    public String Buscar()
    {
        this.getDatosPreventivo();
        return "";
    }
    
    
}
