/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import classes.MessageType;
import classes.User;
import classes.UserInfo;
import exceptions.ConnectException;
import exceptions.SignInException;
import exceptions.SignUpException;
import exceptions.UpdateException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static logic.DaoFactory.getDao;

/**
 *
 * @author 2dam
 */
public class HiloEntrada extends Thread {

    private static final Logger LOGGER = Logger.getLogger(HiloEntrada.class.getName());
    Socket so;

    public HiloEntrada(Socket so) {
        this.so = so;

    }

    @Override
    public void run() {
        UserInfo userResponse = new UserInfo();
        ObjectOutputStream out = null;
         ObjectInputStream in = null;
        try {
            UserInfo userInfo;
            User user = null;
            LOGGER.info("Prepara la lectura y escritura de objetos con el lado cliente");
            in = new ObjectInputStream(so.getInputStream());//recibir mensajes
            out = new ObjectOutputStream(so.getOutputStream());

            LOGGER.info("Recibe el objeto enviado desde el lado cliente");
            userInfo = (UserInfo) in.readObject();

            LOGGER.info("Comprueba la peticion hecha por el cliente");
            if (userInfo.getMessage() == MessageType.SIGNIN_REQUEST) {
                LOGGER.info("Peticion de inicio de sesion");
                user = getDao().signIn(userInfo.getUser());
                LOGGER.info("Peticion completada exitosamente");
                userResponse.setMessage(MessageType.SIGNIN_OK);

            }
            if (userInfo.getMessage() == MessageType.SIGNUP_REQUEST) {
                LOGGER.info("Peticion de inicio de sesion");
                user = getDao().signUp(userInfo.getUser());
                LOGGER.info("Peticion completada exitosamente");
                userResponse.setMessage(MessageType.SIGNUP_OK);

            }
            userResponse.setUser(user);

        } catch (IOException ex) {
            Logger.getLogger(HiloEntrada.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HiloEntrada.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConnectException ex) {
            
            userResponse.setMessage(MessageType.CONNECT_EXCEPTION);
            Logger.getLogger(HiloEntrada.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignInException ex) {
            userResponse.setMessage(MessageType.SIGNIN_EXCEPTION);
            Logger.getLogger(HiloEntrada.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UpdateException ex) {
            userResponse.setMessage(MessageType.UPDATE_EXCEPTION);
            Logger.getLogger(HiloEntrada.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignUpException ex) {
            userResponse.setMessage(MessageType.SIGNUP_EXCEPTION);
            Logger.getLogger(HiloEntrada.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            int disconnect = 1;
            App clientDisconnected = new App(disconnect);
            try {
                LOGGER.info("Envio de la respuesta del lado servidor al cliente");
                out.writeObject(userResponse);
                LOGGER.info("Cierre de escritura y lectura de objetos");
                out.close();
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(HiloEntrada.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
