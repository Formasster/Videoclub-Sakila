package com.alesiar48.alesiar48.videoclub.servicios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;

import com.alesiar48.alesiar48.videoclub.modelo.Cliente;

@Service

public class ClienteServicio {
	    private static final Logger log = LoggerFactory.getLogger(ClienteServicio.class);

		@Autowired
		private JdbcTemplate jdbcTemplate;

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

	public List<Cliente> seleccionClientes(String email) {

		Connection connection = null; // Creamos el objeto para la conexión

		   // Con el jdbcTemplate de spring se encapsulan las operaiones con JDBC

		   connection = jdbcTemplate.getDataSource().getConnection(); // Establecemos la conexión

		   PreparedStatement ps=connection.prepareStatement("SELECT customer_id, first_name, last_name, email, active from customer WHERE email LIKE ?"); // Creamos  consulta
		   ps.setString(1,email + "%"); // Pasamos el parámetro a la consulta 
		   ResultSet clientes=ps.executeQuery(); // Ejecutamos la consulta parametrizada
		   List<Cliente> lista = createListC(clientes);
		   
		   log.info("tamaño "+lista.size());

		  

		// Asignamos a la etiqueta de la plantilla “listaPelis.” un arrayList
	}
		
	
	public String detalleCliente(Integer id_cli) {
		log.info("Detalle cliente "+ id_cli);
		Cliente c = new Cliente();

		String sql="SELECT customer_id, first_name, last_name, email, active FROM customer WHERE customer_id = ?";
		try {
			c = jdbcTemplate.query(
			        sql,
			        (rs, rowNum) -> c(rs.getInt("customer_id"),
	        				rs.getString("first_name"),
	        				rs.getString("last_name"),
	        				rs.getString("email"),
							rs.getInt("active")),id_cli).getFirst();
			  
		
				}catch (InvalidResultSetAccessException e)
		{
		
		return c;
		}
	}	

	public List<Map<String, Object>> historial(Integer id_cli) {
		
		Connection connection = null; // Creamos el objeto para la conexión

		   // Con el jdbcTemplate de spring se encapsulan las operaiones con JDBC

		   connection = jdbcTemplate.getDataSource().getConnection(); // Establecemos la conexión
		   
		log.info("Historial cliente "+ id_cli);
		Cliente c = new Cliente();
		String sql = """
						SELECT 
							c.customer_id, 
							CONCAT(c.first_name, ' ', c.last_name) AS cliente, 
							f.title AS pelicula, 
							r.rental_date AS fecha_alquiler, 
							r.return_date AS fecha_devolucion, 
							p.amount AS monto_pagado, 
							p.payment_date AS fecha_pago
						FROM rental r
						INNER JOIN customer c ON r.customer_id = c.customer_id
						INNER JOIN inventory i ON r.inventory_id = i.inventory_id
						INNER JOIN film f ON i.film_id = f.film_id
						LEFT JOIN payment p ON r.rental_id = p.rental_id
						WHERE c.customer_id = ?
						ORDER BY r.rental_date DESC;
					""";

		 try {
				List<Map<String, Object>> historial = jdbcTemplate.queryForList(sql, id_cli);

   		 } catch (DataAccessException e) {

    		}

				return historial;
	}
    
}
