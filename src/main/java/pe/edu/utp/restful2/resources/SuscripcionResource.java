package pe.edu.utp.restful2.resources;

import pe.edu.utp.restful2.business.SuscripcionDAO;
import pe.edu.utp.restful2.model.Suscriptor;
import pe.edu.utp.restful2.service.Validator;

import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@Path("/suscripcion")
public class SuscripcionResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Suscriptor> getAllSubscription() throws IOException, SQLException, NamingException, NoSuchAlgorithmException {
        SuscripcionDAO suscripcion = new SuscripcionDAO();
        List<Suscriptor> lista = suscripcion.getSuscripciones();
        return lista;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, String> addSubscription(@QueryParam("dni") String dni,
                                             @QueryParam("placa") String placa,
                                             @QueryParam("tarjeta") String tarjeta,
                                             @QueryParam("plan") String plan) {

        HashMap<String,String> resultado = new HashMap<String, String>();

        try {
            SuscripcionDAO suscripcion = new SuscripcionDAO();

            Validator.validarSuscripcion(dni, placa, tarjeta);

            Suscriptor suscriptor = new Suscriptor(dni, placa, tarjeta, plan);

            suscripcion.newSuscripcion(suscriptor);
            resultado.put("success", "Suscriptor agregado");
        } catch (IOException e) {
            resultado.put("error", "La Suscripcion NO ha sido agregada:" + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return resultado;
    }

}
