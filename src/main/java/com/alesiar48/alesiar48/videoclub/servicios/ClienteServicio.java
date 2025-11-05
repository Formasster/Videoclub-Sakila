package com.alesiar48.alesiar48.videoclub.servicios;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alesiar48.alesiar48.videoclub.modelo.Cliente;

public class ClienteServicio {

    	public static List createListC(ResultSet resultSet) throws SQLException {

		List<Cliente> lista = new ArrayList<>();

		// Obtenemos la información de las columnas (opcional)
	    //ResultSetMetaData metadata = resultSet.getMetaData();
	    //int numberOfColumns = metadata.getColumnCount();
		
	    Integer i=0;
	    while (resultSet.next()) {
	        Cliente c = new Cliente();
	        
	        // Asignamos los valores de las columnas a los atributos del objeto
	        // Usa los nombres de columna reales de tu tabla (o índices si prefieres)
	        c.setId(resultSet.getInt("customer_id"));            // o el nombre que uses en tu tabla
	        c.setNombre(resultSet.getString("first_name"));
	        c.setApellido(resultSet.getString("last_name"));
	        c.setEmail(resultSet.getString("email"));
			c.setActivo(resultSet.getInt("active"));
	        //log.info(c.toString());
	        // Añadimos la película a la lista
	       	lista.add(c);
	        
	    }
	    //log.info("Tamaño de la lista "+lista.size());
	    return lista;
		
	}			

    
}
