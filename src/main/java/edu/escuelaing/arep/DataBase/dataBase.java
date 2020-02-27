package edu.escuelaing.arep.DataBase;

import java.sql.Connection;

public interface dataBase {
    public Connection connect();
    public String[] consultarUsuarios();


}