package com.perfectorium.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {

	/**
	* Test maven approach twice
	*/
	public static Connection getConnection() {
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			final String username = "sa", password = "Root0000";
			final String url = "jdbc:jtds:sqlserver://localhost:1433;databaseName=MANAGEMENT";
			return DriverManager.getConnection(url, username, password);
		} catch (Exception ex) {
			Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
}
