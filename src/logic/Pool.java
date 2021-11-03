/*
 * Esta clase será la encargada de gestionar las conexiones con la base de datos
 */
package logic;

import exceptions.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonathan Camacho
 */
public class Pool {

    private static final Logger LOGGER = Logger.getLogger(Pool.class.getName());
    /**
     * Creamos un Stack, para poder almacenar las conexiones y así poder
     * controlarlas.
     */
    Stack<Connection> p = new Stack<>();

    private final ResourceBundle configFile;
    private final String driverBD;
    private final String urlBD;
    private final String userBD;
    private final String contraBD;
    /**
     * Utilizaremos el parametro conexion para abrir o cerrar la conexion.
     */
    private Connection conexion;

    /**
     * Utilizaremos el parametro pool para poder instanciar la clase cuando
     * vayamos a conectarnos a la base de datos.
     */
    private static Pool pool;

    /**
     * Metodo con el que haremos la conexion con la base de datos. Además,
     * añadiremos al Stack la conexión
     */
    private Pool() {

        LOGGER.info("Se obtienen los datos necesarios para abrir la conexion con la BD");

        this.configFile = ResourceBundle.getBundle("archives.config");
        this.driverBD = configFile.getString("driver");
        this.urlBD = configFile.getString("con");
        this.userBD = configFile.getString("DBUSER");
        this.contraBD = configFile.getString("DBPASS");

    }

    /**
     * Creamos un metodo para poder instanciar esta clase
     *
     * @return pool
     */
    public static synchronized Pool getInstance() {
        LOGGER.info("Se instancia la clase Pool");
        if (pool == null) {
            pool = new Pool();
            return pool;
        } else {
            return pool;
        }
    }

    /**
     * Metodo para inicializar la coneción con la base de datos
     *
     * @return conexion
     */
    public Connection getConnection() throws ConnectException {
        if (p.size() != 0) {
            LOGGER.info("Se obtiene una conexion ya existente");
            conexion = p.pop();
        } else {
            try {
                LOGGER.info("Se abre la conexion con la base de datos y se establece la conexion");
                conexion = (Connection) DriverManager.getConnection(this.urlBD, this.userBD, this.contraBD);
            } catch (SQLException e) {
                throw new ConnectException("Error al intentar abrir la BD");
            }

        }
        return conexion;
    }

    /**
     * Metodo para cerrar la conexion a la base de datos
     *
     * @throws SQLException
     */
    public void closeConnection() throws ConnectException {
        try {
            if (conexion != null) {
                p.push(conexion);
                LOGGER.info("Se guarda la conexion y se cierra con al base de datos");
                conexion.close();
            }
        } catch (SQLException e) {
            throw new ConnectException("Error al intentar cerrar la BD");
        }

    }
}
