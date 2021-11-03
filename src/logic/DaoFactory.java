/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import classes.Signable;

/**
 * La factoria del lado servidor
 * @author Alain Cosgaya
 */
public class DaoFactory {
    /**
     * Metodo que devuelve la implementacion del dao.
     * @return La implementacion del dao
     */
    public  static Signable getDao(){
        Signable dao = new DaoImplementation();
        return dao;
    }
}
