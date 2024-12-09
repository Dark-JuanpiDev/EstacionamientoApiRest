package pe.edu.utp.restful2.resources;

import pe.edu.utp.restful2.business.ReservaDAO;
import pe.edu.utp.restful2.model.RegistroEntrada;
import pe.edu.utp.restful2.model.Reserva;
import pe.edu.utp.restful2.model.TipoVehiculo;
import pe.edu.utp.restful2.service.Validator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Path("/entrada")
public class RegistroResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RegistroEntrada> getAllDrivers() throws IOException {
        ReservaDAO entrada = new ReservaDAO();
        List<RegistroEntrada> lista = entrada.getRegistroEntrada();
        return lista;
    }

    @GET @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RegistroEntrada getDriver(@PathParam("id") String ids) throws IOException {
        ReservaDAO entrada = new ReservaDAO();
        int id = Integer.parseInt(ids);
        RegistroEntrada conductor = entrada.getConductorporId(id);
        return conductor;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, String> addDriver(@QueryParam("nombre") String nombre,
                                             @QueryParam("dni") String dni,
                                             @QueryParam("tipo") String tipo,
                                             @QueryParam("placa") String placa,
                                             @QueryParam("fecha") String fecha,
                                             @QueryParam("lavadito") String lavadito) {

        HashMap<String,String> resultado = new HashMap<String, String>();

        try {
            ReservaDAO entrada = new ReservaDAO();

            TipoVehiculo tipoVehiculo = TipoVehiculo.valueOf(tipo.toUpperCase());
            LocalDateTime fechaEntrada = LocalDateTime.parse(fecha);

            Validator.validarConductor(nombre, dni, placa, tipoVehiculo, fechaEntrada);

            Reserva reserva = null;
            if (lavadito == "SI" || lavadito == "si"){
                System.out.println(">>>>>>>>>>>>>>>>> Reserva regular");
                reserva = crearReservaRegular(nombre, dni, placa, tipoVehiculo, fechaEntrada);
            }else{
                System.out.printf(">>>>>>>>>>>>>>>>> Pedido Total (lavado:%s)%n", lavadito);
                reserva = crearReservaConLavado(nombre, dni, placa, tipoVehiculo, fechaEntrada, lavadito);
            }

            entrada.newConductor(reserva);
            resultado.put("success", "Conductor agregado");
        } catch (IOException e) {
            resultado.put("error", "El conductor NO ha sido creado:" + e.getMessage());
        }

        return resultado;
    }

    public Reserva crearReservaRegular(String nombre, String dni, String placa, TipoVehiculo tipoVehiculo, LocalDateTime entrada){
        int DNI = Integer.parseInt(dni);
        return new Reserva.Builder(nombre, DNI, tipoVehiculo, placa, entrada).build();
    }

    public Reserva crearReservaConLavado(String nombre, String dni, String placa, TipoVehiculo tipoVehiculo, LocalDateTime entrada, String lavado){
        int DNI = Integer.parseInt(dni);
        return new Reserva.Builder(nombre, DNI, tipoVehiculo, placa, entrada).withLavado().build();
    }


}
