package pe.edu.utp.restful2.business;


import pe.edu.utp.restful2.model.Suscripcion;
import pe.edu.utp.restful2.model.Suscriptor;
import pe.edu.utp.restful2.util.AppConfig;
import pe.edu.utp.restful2.util.DataAccessMariaDB;
import pe.edu.utp.restful2.util.LogFile;

import javax.naming.NamingException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SuscripcionDAO {
    private final Connection cnn;

    private static HashMap<String, Suscripcion> suscripciones;

    static  {
        suscripciones = new HashMap<>();
        suscripciones.put("GOLD", new Suscripcion("gold",50));
        suscripciones.put("DIAMOND", new Suscripcion("diamond",250));
        suscripciones.put("RADIO",new Suscripcion("radio",300));
    }

    public SuscripcionDAO() throws SQLException, NamingException, NoSuchAlgorithmException {
        this.cnn = DataAccessMariaDB.getConnection(DataAccessMariaDB.TipoDA.DATASOURCE, "java:/MariaDB" );
    }

    public void close() throws SQLException, NamingException {
        if (this.cnn != null) DataAccessMariaDB.closeConnection(this.cnn);
    }

    //conseguir las suscripciones :D
    public List<Suscriptor> getSuscripciones() throws IOException {
        List<Suscriptor> lista_suscriptor = new ArrayList<>();
        String strSQL = String.format("CALL mostrar_suscripciones");
        LogFile.info(strSQL);
        try{
            Connection cnn = DataAccessMariaDB.getConnection(DataAccessMariaDB.TipoDA.DATASOURCE, AppConfig.getDatasource());
            ResultSet rs = cnn.createStatement().executeQuery(strSQL);
            while (rs.next()) {
                String dni = rs.getString("suscripciones.dni");
                String placa = rs.getString("suscripciones.placa");
                String plan = rs.getString("suscripciones.plan");
                String codigo_sorteo = rs.getString("suscripciones.codigo_sorteo");

                Suscripcion suscripcion = suscripciones.get(plan.toUpperCase()).clone();
                Suscriptor suscriptor = new Suscriptor(dni,placa,suscripcion,codigo_sorteo);
                lista_suscriptor.add(suscriptor);
            }
            cnn.close();
        }catch (Exception e){
            String msg = String.format("Ocurrio una exepcion en Mostrar_suscripciones: %s", e.getMessage());
            LogFile.error(msg);
            throw new IOException(msg);
        }
        return lista_suscriptor;
    }
    public void newSuscripcion(Suscriptor suscriptor) throws IOException {
        String strSQL = String.format("CALL registrar_suscripcion('%s','%s','%s','%s')",
                suscriptor.getDni(),
                suscriptor.getPlaca(),
                MD5(suscriptor.getTarjeta()),
                suscriptor.getPlan());
        LogFile.info(strSQL);
        try{
            Connection cnn = DataAccessMariaDB.getConnection(DataAccessMariaDB.TipoDA.DATASOURCE, AppConfig.getDatasource());
            cnn.createStatement().execute(strSQL);
            cnn.close();
        } catch (Exception e){
            String msg = String.format("Ocurrio una excepcion en registrar_suscripcion(%s): %s", suscriptor.getDni(),
                    e.getMessage());
            LogFile.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    public static String MD5(String md5) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }


}
