package pe.edu.utp.restful2;

import pe.edu.utp.restful2.resources.RegistroResource;
import pe.edu.utp.restful2.resources.SalidaResource;
import pe.edu.utp.restful2.resources.SuscripcionResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ServiceApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> h = new HashSet<Class<?>>();

        // Registramos las clases
        h.add(RegistroResource.class);
        h.add(SalidaResource.class);
        h.add(SuscripcionResource.class);
        
        return h;
    }

}