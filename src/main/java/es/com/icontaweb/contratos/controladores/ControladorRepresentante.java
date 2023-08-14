package es.com.icontaweb.contratos.controladores;

import es.com.icontaweb.contratos.modelos.Representante;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;

import es.com.icontaweb.contratos.objetos.Contratos;
import es.com.icontaweb.modelos.Usuarios;
import es.com.icontaweb.rutinas.Rutinas;
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

@ManagedBean(name = "controladorRepresentante")
@SessionScoped
public class ControladorRepresentante implements Serializable {

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
    
    private Representante representante;

    public Representante getRepresentante() {
        return representante;
    }

    public void setRepresentante(Representante representante) {
        this.representante = representante;
    }
    
    
    @ManagedProperty(value = "#{controladorUsuarios}")
    private ControladorUsuarios controladorUsuarios = new ControladorUsuarios();
    public void setControladorUsuarios(ControladorUsuarios controladorUsuarios) { this.controladorUsuarios = controladorUsuarios; }
    public ControladorUsuarios getControladorUsuarios() { return controladorUsuarios; }
    
    
    private boolean swNuevo = false;
    
    public void representantesEditar(ActionEvent event) {
        
        Integer id = new Integer(((UIParameter) event.getComponent().getFacet("id")).getValue().toString());
        swNuevo = false;
        em = entityManager();
        try {
            representante = em.find(Representante.class, id);
        } catch (Exception e) {
            System.out.println("icontaweb " + (new Date()).toString() + " " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }
    
    public String representantesNuevo() {
        this.representante = new Representante();
        swNuevo = true;
        return "/contratos/tablas/representantesDatos.xhtml";
    }

    public String representantesGrabar() {

        em = entityManager();
        try {
            em.getTransaction().begin();
            
            usuarioBorrar();
            
            if (swNuevo) {
                em.persist(representante);
                em.flush();
            } else {
                Integer id = representante.getId();
                Representante representanteModificar = em.find(Representante.class, id);
                representanteModificar = representante;
                em.merge(representanteModificar);
            }

            if (representante.isUsuarioIntranet()) usuarioCrear();

            em.getTransaction().commit();
            
        } catch (Exception e) {
            System.out.println("icontaweb " + (new Date()).toString() + " " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
        
        return "/contratos/tablas/representantesLista.xhtml";
    }
    
    public void representantesBorrar() {
        em = entityManager();
        try {
            usuarioBorrar();
            em.getTransaction().begin();
            Integer id = representante.getId();
            representante = em.find(Representante.class, id);
            em.remove(representante);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("icontaweb " + (new Date()).toString() + " " + e.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
        
    }

    
    public void usuarioBorrar() {
        
        try {
            if (representante.getId() != 0)
            {
                Representante representanteAnterior = em.find(Representante.class, representante.getId());
                Usuarios usuario = em.find(Usuarios.class, representanteAnterior.getUsuario());
                if (usuario != null) {
                    getControladorUsuarios().setUsuario(usuario);
                    getControladorUsuarios().usuariosBorrar();
                }
            }
        } catch (Exception e) {
            System.out.println("icontaweb " + (new Date()).toString() + " " + e.getMessage());
        }
            
    }
    
    public void usuarioCrear() {
        
        Usuarios usuario = new Usuarios();
        usuario.setUsuario(representante.getUsuario());
        usuario.setClave(representante.getClave());
        usuario.setNivel(9);
        usuario.setIdRpresentante(representante.getId());
        usuario.setNombre(representante.getNombre());
        usuario.setProceso("intranet/listaComunidades.xhtml");
        em.persist(usuario);
        em.flush();

    }
    
    
    
    boolean swModificar = false;

    /**
     * Objeto para el acceso a los datos y funciones de acceso a la base de
     * datos.
     */
    private Contratos objeto = new Contratos();

    public void setObjeto(Contratos objeto) {
        this.objeto = objeto;
    }

    public Contratos getObjeto() {
        return objeto;
    }

    /**
     * Lista de Rutas
     */
    private List<Representante> listaRepresentantes = new ArrayList<>();

    public void setListaRepresentantes(List<Representante> listaRepresentantes) {
        this.listaRepresentantes = listaRepresentantes;
    }

    public List<Representante> getListaRepresentantes() {

        em = entityManager();
        try {
            listaRepresentantes.clear();

            String sqlquery = "select r from Representante r";
            Query q = em.createQuery(sqlquery);
            listaRepresentantes = q.getResultList();
        } catch (Exception e) {
            System.out.println("icontaweb " + (new Date()).toString() + " " + e.getMessage());
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }

        return listaRepresentantes;
    }

    /**
     * Ver datos del registro actual
     *
     * @param event
     */
    public void cmdVerRepresentante(ActionEvent event) {

        Integer codigo = new Integer(((UIParameter) event.getComponent().getFacet("codigo")).getValue().toString());
        if (objeto.LeerRepresentante(codigo)) {
            Rutinas.MuestraMensaje("Introduzca Datos del Representante y pulse Grabar...");
            swModificar = true;
        } else {
            Rutinas.MuestraMensaje("Error al acceder al dato");
        }
    }

    /**
     * Generar nuevo
     *
     * @param event
     */
    public void cmdNuevoRepresentante(ActionEvent event) {
        if (objeto.NuevoRepresentante()) {
            Rutinas.MuestraMensaje("Introduzca Datos del Representante y pulse Grabar...");
            swModificar = false;
        } else {
            Rutinas.MuestraMensaje("Error al Crear nuevo Representante");
        }
    }

    /**
     * Grabar nuevo motivo
     *
     * @param event
     */
    public void cmdGrabaRepresentante(ActionEvent event) {
        if (swModificar) {
            objeto.ActualizaRepresentante(objeto.getRepresentante().getId(), objeto.getRepresentante().getNombre());
        } else {
            objeto.GrabaRepresentante();
        }
    }

    /**
     * Borrar el motivo actual
     *
     * @param event
     */
    public void cmdBorraRepresentante(ActionEvent event) {
        objeto.BorraRepresentante(objeto.getRepresentante().getId());
    }

}
