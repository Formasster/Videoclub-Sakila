package com.alesiar48.alesiar48.videoclub.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class MainController {
	@Autowired
	   JdbcTemplate jdbcTemplate;
	   private PeliculaServicio peliculaServicio;

	private static final Logger log= LoggerFactory.getLogger(VideoclubV1Application.class);
	
	@GetMapping("/catalogo")
	public String catalogoPeliculas (@RequestParam (required=false) String pelicula, Model model) {

		// 1. Creamos el objeto para la conexión
		Connection connection = null; 
		log.info("1. creamos lista ");

		try { // Importante trabajar con excepciones 

		   // 2. Establecemos la conexión
		   connection = jdbcTemplate.getDataSource().getConnection();
		   
		   // 3. Creamos un objeto statement para trabajar con el Servidor de bases de datos
		   Statement st=connection.createStatement(); 
		   
		   // 4. Ejecutamos la consulta en el Servidor de bases de datos
		   st.execute("select film_id, title, description, length, rating from film"); 
		   
		   // 5. Guardamos en pelis el resultado de la consulta. Es un ResultSet = Cursor
		   ResultSet pelis=st.getResultSet(); 

		   // 6. Necesitamos un método auxiliar "createList" para pasar de ResultSet a Lista.
		   // Pasamos a la plantilla la lista de películas
		   
		   model.addAttribute("listapelis", createList(pelis));
		   
		   } catch (SQLException e) { 
		   model.addAttribute("error", e.getMessage()); // Añadimos el mensaje de error a la plantilla
		   } 

		   return "catalogo"; // Devolvemos la plantilla
	}

	//Pelicula

	@GetMapping("/numPeliculas")
	public String obtenerNumeroPeliculas(Model model){

		Integer numpelis = PeliculaServicio.numPeliculas(listapelis);
		model.addAttribute("resultado", numpelis);
		return "vista1"; //hacer la vista 
	}
	
	@GetMapping("/peliculas")
	public String seleccionPeliculas(@RequestParam (required = false) String titulo, Model model) {

		Connection connection = null; // Creamos el objeto para la conexión

		try { // Importante trabajar con excepciones 
		   // Con el jdbcTemplate de spring se encapsulan las operaiones con JDBC

		   connection = jdbcTemplate.getDataSource().getConnection(); // Establecemos la conexión

		   PreparedStatement ps=connection.prepareStatement("select film_id, title, description, length, rating from film WHERE title LIKE ?"); // Creamos  consulta
		   ps.setString(1,titulo + "%");; // Pasamos el parámetro a la consulta 
		   ResultSet pelis=ps.executeQuery(); // Ejecutamos la consulta parametrizada
		   List<Pelicula> lista = new ArrayList<>();
		   lista = createList(pelis);
		   log.info("tamaño "+lista.size());
		    model.addAttribute("listaPelis",lista ); 
		  

		// Asignamos a la etiqueta de la plantilla “listaPelis.” un arrayList
		} catch (SQLException e) { 
		model.addAttribute("error", e.getMessage()); // Añadimos el mensaje de error a la plantilla
		} 

		return "peliculas"; // Devolvemos la plantilla

   }

   

	@SuppressWarnings("deprecation")
	@GetMapping("/detallepelicula/{id_peli}")
	public String detallePelicula(@PathVariable Integer id_peli, Model model) {
		log.info("Detalle pelicula "+ id_peli);
		Pelicula peli = new Pelicula();
		String sql="SELECT film_id, title, description, release_year, length, rating FROM film WHERE film_id = ?";
		try {
			peli = jdbcTemplate.query(
			        sql,
			        (rs, rowNum) -> new Pelicula(rs.getInt("film_id"),
				            rs.getString("title"),
				            rs.getString("description"),
				            rs.getInt("length"),
				            rs.getString("rating"),
				            rs.getInt("release_year"),
				            null
				        ), id_peli)
			    .getFirst();
			  
		
				}catch (InvalidResultSetAccessException e)
		{
			model.addAttribute("error",e.getMessage());
		}
		catch (DataAccessException e)
		{
			model.addAttribute("error",e.getMessage());
		}
		
		log.info("Pelicula recuperada "+peli.getTitulo() );
		
		model.addAttribute("pelicula",peli);
		
		return "detallepeli";
	}
		

	@GetMapping("/contacto")
	public String mostrarContacto(Model model) {
		return "contacto";
	}
	

	//Cliente

   	@GetMapping("/clientes")
	public String seleccionClientes(@RequestParam (required = false) String email, Model model) {
		if (email == null) email = "";
    	if (jdbcTemplate.getDataSource() == null) {
        	model.addAttribute("error", "No DataSource configured");
        	return "clientes";
    	}

		Connection connection = null; // Creamos el objeto para la conexión

		try { // Importante trabajar con excepciones 
		   // Con el jdbcTemplate de spring se encapsulan las operaiones con JDBC

		   connection = jdbcTemplate.getDataSource().getConnection(); // Establecemos la conexión

		   PreparedStatement ps=connection.prepareStatement("SELECT customer_id, first_name, last_name, email, active from customer WHERE email LIKE ?"); // Creamos  consulta
		   ps.setString(1,email + "%"); // Pasamos el parámetro a la consulta 
		   ResultSet clientes=ps.executeQuery(); // Ejecutamos la consulta parametrizada
		   List<Cliente> lista = createListC(clientes);
		   log.info("tamaño "+lista.size());
		   model.addAttribute("listaClientes", lista ); 
		   model.addAttribute("email", email == null ? "" : email);
		   model.addAttribute(email, lista);
		  

		// Asignamos a la etiqueta de la plantilla “listaPelis.” un arrayList
		} catch (SQLException e) { 
		model.addAttribute("error", e.getMessage()); // Añadimos el mensaje de error a la plantilla
		} 

		return "clientes"; // Devolvemos la plantilla

   }
	
	

	@SuppressWarnings("deprecation")
	@GetMapping("/detallecliente/{id_cli}")
	public String detalleCliente(@PathVariable Integer id_cli, Model model) {
		log.info("Detalle cliente "+ id_cli);
		Cliente c = new Cliente();
		String sql="SELECT customer_id, title, description, release_year, length, rating FROM film WHERE film_id = ?";
		try {
			c = jdbcTemplate.query(
			        sql,
			        (rs, rowNum) -> new Cliente(rs.getInt("customer_id"),
	        				rs.getString("first_name"),
	        				rs.getString("last_name"),
	        				rs.getString("email"),
							rs.getInt("active")),id_cli).getFirst();
			  
		
				}catch (InvalidResultSetAccessException e)
		{
			model.addAttribute("error",e.getMessage());
		}
		catch (DataAccessException e)
		{
			model.addAttribute("error",e.getMessage());
		}
		
		model.addAttribute("cliente",id_cli);
		
		return "detallecliente";
	}

	@GetMapping("/historial/{id_cli}")
	public String historial(@PathVariable Integer id_cli, Model model) {
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
				model.addAttribute("historial", historial);
				model.addAttribute("clienteId", id_cli);
   		 } catch (DataAccessException e) {
        		model.addAttribute("error", e.getMessage());
    		}

		
		model.addAttribute("cliente",id_cli);
		
		return "historial";
	}
	//historial: rental > inventory > film > title 
}




