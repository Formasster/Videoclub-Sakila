package com.videoclub.sakila;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
		
	@GetMapping("/clientes")
	public String test(Model model) {
		
		//model.addAttribute("msg","Me has pasado como parámetro "+param);
		//tener en cuenta cliente, 
		
		return "listadoclientes";
	}

	@GetMapping("/contacto")
	public String mostrarContacto(Model model) {
		return "contacto";
	}
	
	
	
	public static List createList(ResultSet resultSet) throws SQLException {

		List<Pelicula> lista = new ArrayList<>();

		// Obtenemos la información de las columnas (opcional)
	    //ResultSetMetaData metadata = resultSet.getMetaData();
	    //int numberOfColumns = metadata.getColumnCount();
		
	    Integer i=0;
	    log.info("createList");
	    while (resultSet.next()) {
	        Pelicula p = new Pelicula();
	        
	        // Asignamos los valores de las columnas a los atributos del objeto
	        // Usa los nombres de columna reales de tu tabla (o índices si prefieres)
	        p.setId(resultSet.getInt("film_id"));            // o el nombre que uses en tu tabla
	        p.setTitulo(resultSet.getString("title"));
	        p.setDescripcion(resultSet.getString("description"));
	        p.setDuracion(resultSet.getInt("length"));
	        p.setClasificacion(resultSet.getString("rating"));
	        log.info(p.toString());
	        // Añadimos la película a la lista
	       lista.add(p);
	        
	    }
	    log.info("Tamaño de la lista "+lista.size());
	    return lista;
		
	}			
	
}




