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

import com.alesiar48.alesiar48.videoclub.Application;
import com.alesiar48.alesiar48.videoclub.modelo.Cliente;
import com.alesiar48.alesiar48.videoclub.modelo.Pelicula;
import com.alesiar48.alesiar48.videoclub.servicios.ClienteServicio;
import com.alesiar48.alesiar48.videoclub.servicios.PeliculaServicio;


@Controller
public class MainController {
	@Autowired
	   JdbcTemplate jdbcTemplate;
	   private PeliculaServicio peliculaServicio;
	   private ClienteServicio clienteServicio;

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
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

	@GetMapping("/peliculas")
	public String seleccionPeliculas(@RequestParam (required = false) String titulo, Model model) throws SQLException {
 
			List<Pelicula> lista = peliculaServicio.buscaPeliTitulo(titulo);
			model.addAttribute("listapelis", lista);

		return "peliculas"; // Devolvemos la plantilla

   	}

   

	@SuppressWarnings("deprecation")
	@GetMapping("/detallepelicula/{id_peli}")
	public String detallePelicula(@PathVariable Integer id_peli, Model model) {
		log.info("Detalle pelicula "+ id_peli);

		Pelicula peli = new Pelicula();

		peli = peliculaServicio.detallepeliculaAux(id_peli);
		
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

		List<Cliente> listaClientes = clienteServicio.seleccionClientes(email);
		model.addAttribute("listaclientes", listaClientes);

		return "clientes"; // Devolvemos la plantilla

   }
	
	

	@SuppressWarnings("deprecation")
	@GetMapping("/detallecliente/{id_cli}")
	public Cliente detalleCliente(@PathVariable Integer id_cli, Model model) {
		log.info("Detalle cliente "+ id_cli);
		Cliente c = new Cliente();
		
		try {
			
			  c = clienteServicio.detalleCliente(id_cli);
		
				}catch (InvalidResultSetAccessException e)
		{
			model.addAttribute("error",e.getMessage());
		}
		catch (DataAccessException e)
		{
			model.addAttribute("error",e.getMessage());
		}
		
		model.addAttribute("c",id_cli);
		
		return "detallecliente";
	}

	@GetMapping("/historial/{id_cli}")
	public String historial(@PathVariable Integer id_cli, Model model) {
		log.info("Historial cliente "+ id_cli);
		Cliente c = new Cliente();
	

		 try {
				List<Map<String, Object>> alquileres = clienteServicio.historial(id_cli);
				model.addAttribute("historial", alquileres);
				model.addAttribute("clienteId", id_cli);
   		 } catch (DataAccessException e) {
        		model.addAttribute("error", e.getMessage());
    		}

		model.addAttribute("historial", alquileres);
		model.addAttribute("cliente",id_cli);
		
		return "historial";
	}
	//historial: rental > inventory > film > title 
}




