package pe.edu.utp.restful2.resources;

import pe.edu.utp.restful2.business.ReservaDAO;
import pe.edu.utp.restful2.model.RegistroEntrada;
import pe.edu.utp.restful2.model.RegistroSalida;
import pe.edu.utp.restful2.model.Salida;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Path("/salida")
public class SalidaResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RegistroSalida> getAllOutDrivers() throws IOException {
        ReservaDAO salida = new ReservaDAO();
        List<RegistroSalida> lista = salida.getRegistroSalida();
        return lista;
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, String> getOutDriver(@PathParam("id") String ids) {

        HashMap<String,String> resultado = new HashMap<String, String>();

        ReservaDAO salida = new ReservaDAO();

        try {

            int idEntrada = Integer.parseInt(ids);
            RegistroEntrada conductor = salida.getConductorporId(idEntrada);

            boolean lavado = false;
            if (conductor.getLavado() == "Sí") {
                lavado = true;
            } else if (conductor.getLavado() == "No") {
                lavado = false;
            }

            double total = ReservaDAO.getTotal(conductor.getFechaHoraEntrada(), lavado);
            Salida outDriver = new Salida(idEntrada, LocalDateTime.now(), total);

            salida.newRegistroSalida(outDriver);
            resultado.put("success", "Conductor ha finalizado su estacionamiento");
        } catch (IOException e) {
            resultado.put("error", "El conductor NO ha sido creado:" + e.getMessage());
        }

        return resultado;
    }

}
