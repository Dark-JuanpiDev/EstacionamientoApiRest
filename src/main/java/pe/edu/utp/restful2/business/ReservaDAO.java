package pe.edu.utp.restful2.business;

import pe.edu.utp.restful2.model.*;
import pe.edu.utp.restful2.util.AppConfig;
import pe.edu.utp.restful2.util.DataAccessMariaDB;
import pe.edu.utp.restful2.util.LogFile;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ReservaDAO {

    public List<RegistroEntrada> getRegistroEntrada() throws IOException {
        List<RegistroEntrada> lista_Entradas = new ArrayList<>();

        String strSQL = String.format("CALL pr_MostrarRegistrosEntrada()");
        LogFile.info(strSQL);
        try {
            Connection cnn = DataAccessMariaDB.getConnection(DataAccessMariaDB.TipoDA.DATASOURCE, AppConfig.getDatasource());
            ResultSet rst = cnn.createStatement().executeQuery(strSQL);
            while (rst.next()) {
                int idRegistroEntrada = rst.getInt("re.id_registroentrada");
                int idConductor = rst.getInt("re.id_conductor");
                int idVehiculo = rst.getInt("re.id_vehiculo");
                String nombreEstacionamiento = rst.getString("e.nombreestacionamiento");
                String nombreConductor = rst.getString("nombreconductor");
                String dni = rst.getString("dniconductor");
                String placa = rst.getString("placavehiculo");
                TipoVehiculo tipoVehiculo = TipoVehiculo.valueOf(rst.getString("tipovehiculo").toUpperCase());
                String lavado = rst.getString("lavadosolicitado");
                LocalDateTime FechaHoraEntrada = rst.getTimestamp("fechahoraentrada").toLocalDateTime();

                RegistroEntrada registroEntrada = new RegistroEntrada(idRegistroEntrada, idConductor, idVehiculo, nombreEstacionamiento, nombreConductor, dni, placa, tipoVehiculo, lavado, FechaHoraEntrada);
                lista_Entradas.add(registroEntrada);
            }
            cnn.close();
        } catch (Exception e) {
            String msg = String.format("Ocurrio una excepcion en MostrarRegistrosEntrada(): %s", e.getMessage());
            LogFile.error(msg);
            throw new IllegalStateException(msg);
        }

        return lista_Entradas;
    }

    public List<RegistroSalida> getRegistroSalida() throws IOException {
        List<RegistroSalida> lista_Salida = new LinkedList<>();

        String strSQL = String.format("CALL pr_MostrarRegistrosSalida()");
        LogFile.info(strSQL);
        try {
            Connection cnn = DataAccessMariaDB.getConnection(DataAccessMariaDB.TipoDA.DATASOURCE, AppConfig.getDatasource());
            ResultSet rst = cnn.createStatement().executeQuery(strSQL);
            while (rst.next()) {
                int idRegistroSalida = rst.getInt("rs.id_registrosalida");
                String NombreEstacionamiento = rst.getString("e.nombreestacionamiento");
                String NombreConductor = rst.getString("nombreconductor");
                String Placa = rst.getString("placavehiculo");
                TipoVehiculo tipoVehiculo = TipoVehiculo.valueOf(rst.getString("tipovehiculo").toUpperCase());
                String lavado = rst.getString("lavadosolicitado");
                LocalDateTime FechaHoraEntrada = rst.getTimestamp("fechahoraentrada").toLocalDateTime();
                LocalDateTime FechaHoraSalida = rst.getTimestamp("fechahorasalida").toLocalDateTime();
                double total = rst.getDouble("rs.total");

                RegistroSalida registroSalida = new RegistroSalida(idRegistroSalida, NombreEstacionamiento, NombreConductor, Placa, tipoVehiculo, lavado, FechaHoraEntrada, FechaHoraSalida, total);
                lista_Salida.add(registroSalida);

            }
            cnn.close();
        } catch (Exception e) {
            String msg = String.format("Ocurrio una excepcion en MostrarRegistrosSalida(): %s", e.getMessage());
            LogFile.error(msg);
            throw new IllegalStateException(msg);
        }

        return lista_Salida;
    }

    public RegistroEntrada getConductorporId(int ID) throws IOException {
        RegistroEntrada driver = null;

        String strSQL = String.format("CALL pr_MostrarConductorporId(%s)", ID);
        LogFile.info(strSQL);
        try {
            Connection cnn = DataAccessMariaDB.getConnection(DataAccessMariaDB.TipoDA.DATASOURCE, AppConfig.getDatasource());
            ResultSet rst = cnn.createStatement().executeQuery(strSQL);
            if (rst.next()) {
                int idRegistroEntrada = rst.getInt("re.id_registroentrada");
                int idConductor = rst.getInt("re.id_conductor");
                int idVehiculo = rst.getInt("re.id_vehiculo");
                String nombreEstacionamiento = rst.getString("e.nombreestacionamiento");
                String nombreConductor = rst.getString("nombreconductor");
                String dni = rst.getString("dniconductor");
                String placa = rst.getString("placavehiculo");
                TipoVehiculo tipoVehiculo = TipoVehiculo.valueOf(rst.getString("tipovehiculo").toUpperCase());
                String lavado = rst.getString("lavadosolicitado");
                LocalDateTime FechaHoraEntrada = rst.getTimestamp("fechahoraentrada").toLocalDateTime();

                driver = new RegistroEntrada(idRegistroEntrada, idConductor, idVehiculo, nombreEstacionamiento, nombreConductor, dni, placa, tipoVehiculo, lavado, FechaHoraEntrada);

            }
            cnn.close();
        } catch (Exception e) {
            String msg = String.format("Ocurrio una excepcion en MostrarRegistrosSalida(): %s", e.getMessage());
            LogFile.error(msg);
            throw new IllegalStateException(msg);
        }

        // Este método si o si debe retornar un libro
        if ( driver == null ){
            driver = new RegistroEntrada();
            driver.setNombreConductor("Conductor not found");
            driver.setPlaca("Please check the error logs");
        }

        return driver;
    }

    public void newConductor(Reserva reserva) throws IOException {

        String strSQL = String.format("CALL pr_RegistrarEntrada('%s','%s','%s','%s','%s', %s)",
                reserva.getNombreConductor(), reserva.getDni(), reserva.getTipoVehiculo(), reserva.getPlaca(),
                reserva.getFechaHoraEntrada(), reserva.isLavado());
        LogFile.info(strSQL);

        try {
            Connection cnn = DataAccessMariaDB.getConnection(DataAccessMariaDB.TipoDA.DATASOURCE, AppConfig.getDatasource());
            int ne = cnn.createStatement().executeUpdate(strSQL);
            cnn.close();
        } catch (Exception e) {
            String msg = String.format("Ocurrio una excepcion en pr_RegistrarEntrada(%s): %s", reserva.getNombreConductor(),  e.getMessage());
            LogFile.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    public static void newRegistroSalida(Salida salida) throws IOException {
        String strSQL = String.format("CALL pr_RegistroSalidaApi(%s, '%s', %.2f)", salida.getIdRegistroEntrada(), salida.getFechaHoraSalida(), salida.getTotal());
        LogFile.info(strSQL);

        try {
            Connection cnn = DataAccessMariaDB.getConnection(DataAccessMariaDB.TipoDA.DATASOURCE, AppConfig.getDatasource());
            int ne = cnn.createStatement().executeUpdate(strSQL);
            cnn.close();
        } catch (Exception e) {
            String msg = String.format("Ocurrio una excepcion en pr_RegistroSalidaApi(%d): %s", salida.getIdRegistroEntrada(),
                    e.getMessage());
            LogFile.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    public static double getTotal(LocalDateTime fechaEntrada, boolean lavado) throws IOException {
        double cobroPorHora = 5;
        double cobroPorDia = 120;
        Duration duracion = Duration.between(fechaEntrada, LocalDateTime.now());
        long diasDebiendo = duracion.toDays();
        long horas = duracion.minusDays(diasDebiendo).toHours();
        double total = (diasDebiendo * cobroPorDia) + (horas * cobroPorHora);
        if (lavado) {
            total += 20;
        }
        return total;
    }



}
