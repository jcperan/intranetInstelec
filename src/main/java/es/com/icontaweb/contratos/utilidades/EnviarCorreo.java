package es.com.icontaweb.contratos.utilidades;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;

import es.com.icontaweb.contratos.objetos.Contratos;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

public class EnviarCorreo {

    public EnviarCorreo() {
        this.EnviarMensaje("instelec@puertaAutomatica.es", null, false);
    }

    public EnviarCorreo(String destino, Contratos objeto, boolean swMantenimiento) {
        this.EnviarMensaje(destino, objeto, swMantenimiento);
    }

    public void EnviarMensaje(String destino, Contratos objeto, boolean swMantenimiento) {

        try {

            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            String realPath = (String) servletContext.getRealPath("/");

            // Propiedades de la conexión
            Properties propiedades = new Properties();
            propiedades.load(new FileInputStream(new File(realPath + "/WEB-INF/enviarCorreo.properties")));

            final String serv = propiedades.getProperty("servidor");
            final String user = propiedades.getProperty("usuario");
            final String pass = propiedades.getProperty("pass");

            Properties props = new Properties();
            props.setProperty("mail.smtp.host", serv);
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.username", user);
            props.setProperty("mail.smtp.password", pass);
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.localhost", "vs1075");

            // Preparamos la sesion
            Session session = Session.getDefaultInstance(props);

            // String aviso = objeto.getAvisos().getObservaciones();
            String aviso = objeto.getVisitas().getDescripcionAviso();
            if (aviso == null) aviso = "";

            // Se compone la parte del texto
            BodyPart texto = new MimeBodyPart();
            
            String TextoMensaje = "<html> "
                                + "     <head> "
                                + "         <meta charset='ISO-8859-1'>"
                                + "         <meta name='viewport' content='width=device-width, initial-scale=1.0'> "
                                + "         <style type='text/css'> "
                                + "             body {font-family: Arial, sans-serif;font-size: 16px;line-height: 1.5;color: #333; background-color: #ffffff; } "
                                + "             h1   {font-size: 20px;color: #006699;margin-top: 0;margin-bottom: 20px;} "
                                + "             h2   {font-size: 14px;color: #006699;margin-top: 20px;margin-bottom: 10px;} "
                                + "             h3   {font-size: 18px;color: #996600;margin-left: 30px;} "
                                + "             h4   {font-size: 12px;color: #669966;margin-left: 12px;margin-top: 0px; margin-bottom: 0px;} "
                                + "             p    {margin-left: 30px; margin-bottom: 20px;} "
                                + "         </style> "
                                + "     </head> "
                                + "     <body> "
                                + "         <h1>Descripción de Visita Realizada</h1> "
                                + "         <h3>" + objeto.getVisitas().getClientes().getNombre() + "</h2>"
                                + "         <h3>"  + objeto.getVisitas().getSp() + "</h3><br/>"
                                + "         <br/><br/>"
                                + "         <h1>Detalles del trabajo</h1> "
                                + "         <p>      En el dia " + new SimpleDateFormat("dd-MM-yyyy").format(objeto.getVisitas().getFecha()) + " hemos realizado una visita a su instalacion por </p>"
                                + "         <p>" + objeto.getVisitas().getMotivos().getMotivo() + "</p>"
                                + "         <p>con el siguiente trabajo</p>"
                                + "         <p>" + objeto.getVisitas().getTrabajos().getTrabajo()  + "</p>"
                                + "         <p>" + aviso + "</p>"
                                + "         <h2>Descripcion del trabajo Realizado</h2>" 
                                + "         <p> " + objeto.getVisitas().getObservaciones() + "</p>"
                                + "         Reciba un cordial saludo...<br/><br/>"
                                + "         <img src=\"cid:image\">"
                                + "         <br/>http://www.puertaautomatica.es<br/><br/>"
                                + "         <h4>AVISO DE CONFIDENCIALIDAD. La información incluida en este email, así como los posibles archivos adjuntos al mismo, son CONFIDENCIALES. Siendo para uso exclusivo de su destinatario. Si usted recibe este email y no es su destinatario, notifíquenos este hecho y elimine este mensaje de su sistema. Queda prohibida la copia, difusión o revelación de su contenido a terceros sin la debida autorización de Lorca INSTELEC S.L. En caso contrario vulnerará la legislación vigente.</h4>"
                                + "         <h4>PROTECCION DE DATOS. Sus datos personales, incluido su email, están incluidos en un fichero titularidad de Lorca INSTELEC S.L. cuya finalidad es la de mantener el contacto con Vd., quien podrá ejercer sus derechos de acceso, rectificación, cancelación u oposición mediante envio  a: ( www.puertaautomatica.es/contacto ), con el objeto de que el citado error no vuelva a producirse. Gracias.</h4><br/><br/>"
                                + "         <h4>Por favor, antes de imprimir piense si lo necesita.<h4><br/>"
                                + "         </body>"
                                + "</html>";


/**            
            String TextoMensaje = (new Date()).toString() + "<br/><br/>"
                    + "                    <strong>" + objeto.getVisitas().getClientes().getNombre() + "</strong><br/><br/>"
                    + objeto.getVisitas().getSp() + "<br/>"
                    + "En el dia " + new SimpleDateFormat("dd-MM-yyyy").format(objeto.getVisitas().getFecha()) + " hemos realizado una visita a su instalacion por <br/>"
                    + objeto.getVisitas().getMotivos().getMotivo() + "<br/>"
                    + "con el siguiente trabajo "
                    + objeto.getVisitas().getTrabajos().getTrabajo() + "<br/><br/>"
                    + aviso + "<br/><br/>"
                    + "Realizado" + "<br/><br/>"
                    + objeto.getVisitas().getObservaciones() + "<br/><br/><br/>"
                    + "Reciba un cordial saludo...<br/><br/>"
                    + "<img src=\"cid:image\">"
                    + "<br/>http://www.puertaautomatica.es<br/><br/>"
                    + "AVISO DE CONFIDENCIALIDAD. La información incluida en este email, así como los posibles archivos adjuntos al mismo, son CONFIDENCIALES. Siendo para uso exclusivo de su destinatario. Si usted recibe este email y no es su destinatario, notifíquenos este hecho y elimine este mensaje de su sistema. Queda prohibida la copia, difusión o revelación de su contenido a terceros sin la debida autorización de Lorca INSTELEC S.L. En caso contrario vulnerará la legislación vigente."
                    + "<br/><br/>"
                    + "PROTECCION DE DATOS. Sus datos personales, incluido su email, están incluidos en un fichero titularidad de Lorca INSTELEC S.L. cuya finalidad es la de mantener el contacto con Vd., quien podrá ejercer sus derechos de acceso, rectificación, cancelación u oposición mediante envio  a: ( www.puertaautomatica.es/contacto ), con el objeto de que el citado error no vuelva a producirse. Gracias.<br/><br/>"
                    + "Por favor, antes de imprimir piense si lo necesita.<br/>";
**/

            
            TextoMensaje = new String(TextoMensaje.getBytes("ISO-8859-1"));
            texto.setContent(TextoMensaje, "text/html");

            // Se compone el adjunto con la imagen
            MimeBodyPart imagen = new MimeBodyPart();
            imagen.attachFile(realPath + "/recursos/lorca-instelec.jpg");
            imagen.setHeader("Content-ID", "<image>");

            String flname = "justificante";
            String spname = ""; if (objeto.getVisitas().getSp() != null)  spname = objeto.getVisitas().getSp().trim();
            if (spname.equals("")) {
                
            } else {
                Path file1 = FileSystems.getDefault().getPath(realPath + "/informes/" + flname + ".pdf");
                Path file2 = FileSystems.getDefault().getPath(realPath + "/informes/" + spname + ".pdf");
                Files.copy(file1, file2, StandardCopyOption.REPLACE_EXISTING);
                flname = spname;
            }

            
            // Se compone el adjunto con el justificante
            BodyPart adjunto1 = new MimeBodyPart();
            adjunto1.setDataHandler(new DataHandler(new FileDataSource(realPath + "/informes/" + flname + ".pdf")));
            adjunto1.setFileName(flname + ".pdf");

            // Se compone el adjunto con el mantenimiento
            BodyPart adjunto2 = new MimeBodyPart();
            if (swMantenimiento) {
                adjunto2.setDataHandler(new DataHandler(new FileDataSource(realPath + "/informes/mantenimiento.pdf")));
                adjunto2.setFileName("mantenimiento.pdf");
            }

            // Una MultiParte para agrupar texto e imagen.
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
            multiParte.addBodyPart(imagen);
            multiParte.addBodyPart(adjunto1);
            if (swMantenimiento) multiParte.addBodyPart(adjunto2);

            // Construimos el mensaje
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
            if (spname.equals("")) {
                message.setSubject("Justificante " + objeto.getClientes().getNombre());
            } else {
                message.setSubject(spname);
            }
            message.setContent(multiParte);

            // Lo enviamos.
            Transport t = session.getTransport("smtp");
            t.connect(serv, user, pass);
            t.sendMessage(message, message.getAllRecipients());
            System.out.println("icontaweb : " + new Date().toString() + " - ENVIADO CORREO " + destino);

            // Cierre.
            t.close();
            
            // Eliminar arhivo pdf 
            if (!spname.equals("")) {
                Path file2 = FileSystems.getDefault().getPath(realPath + "/informes/" + spname + ".pdf");
                Files.delete(file2);
            }
            
        } catch (Exception e) {
            System.out.println("icontaweb : " + (new Date()).toString() + " Error Envio Correo " + e.getMessage());
        }
    }

}
