package pe.gob.mml.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión JDBC a la base de datos (MySQL).
 * Usa los mismos datos que la conexión de DBeaver:
 * host, puerto, base de datos, usuario y contraseña.
 */
public class Conexion {

    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    private static final String BASE_DATOS = "bd_proyectos_mml";
    private static final String USUARIO = "root";
    private static final String CLAVE = "Astro01@";   // XAMPP: normalmente vacía ""

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima";

    static {
        // Se intenta primero el driver oficial de MySQL (si lo agregas a WEB-INF/lib)
        // la URL "jdbc:mysql://" funcionan con MySQL.
        if (!cargar("com.mysql.cj.jdbc.Driver") && !cargar("org.mariadb.jdbc.Driver")) {
            throw new RuntimeException("No se encontró ningún driver JDBC en WEB-INF/lib.");
        }
    }

    private static boolean cargar(String clase) {
        try {
            Class.forName(clase);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
