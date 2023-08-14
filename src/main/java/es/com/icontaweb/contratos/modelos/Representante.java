package es.com.icontaweb.contratos.modelos;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the representantes database table.
 * 
 */
@Entity
@Table(name="representantes")
public class Representante implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String nombre;
        
        private boolean usuarioIntranet;
        private String usuario;
        private String clave;

    public Representante() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
        
        public boolean isUsuarioIntranet() {
            return usuarioIntranet;
        }

        public void setUsuarioIntranet(boolean usuarioIntranet) {
            this.usuarioIntranet = usuarioIntranet;
        }

        public String getUsuario() {
            return usuario;
        }

        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }

        public String getClave() {
            return clave;
        }

        public void setClave(String clave) {
            this.clave = clave;
        }
        

}