package com.alesiar48.alesiar48.videoclub.servicios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alesiar48.alesiar48.videoclub.modelo.Pelicula;

@Service
public class PeliculaServicio {
	  private static final Logger log = LoggerFactory.getLogger(PeliculaServicio.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
    
  
	public static List createList(ResultSet resultSet) throws SQLException {

		List<Pelicula> lista = new ArrayList<>();

		// Obtenemos la información de las columnas (opcional)
	    //ResultSetMetaData metadata = resultSet.getMetaData();
	    //int numberOfColumns = metadata.getColumnCount();
		
	    Integer i=0;
	    //log.info("createList");
	    while (resultSet.next()) {
	        Pelicula p = new Pelicula();
	        
	        // Asignamos los valores de las columnas a los atributos del objeto
	        // Usa los nombres de columna reales de tu tabla (o índices si prefieres)
	        p.setId(resultSet.getInt("film_id"));            // o el nombre que uses en tu tabla
	        p.setTitulo(resultSet.getString("title"));
	        p.setDescripcion(resultSet.getString("description"));
	        p.setDuracion(resultSet.getInt("length"));
	        p.setClasificacion(resultSet.getString("rating"));
	        //log.info(p.toString());
	        // Añadimos la película a la lista
	       lista.add(p);
	        
	    }
	    //log.info("Tamaño de la lista "+lista.size());
	    return lista;
		
	}	

	public List buscaPeliTitulo(String titulo) throws SQLException{

		Connection connection = null; // Creamos el objeto para la conexión

		   // Con el jdbcTemplate de spring se encapsulan las operaiones con JDBC

		   connection = jdbcTemplate.getDataSource().getConnection(); // Establecemos la conexión

		   PreparedStatement ps=connection.prepareStatement("select film_id, title, description, length, rating from film WHERE title LIKE ?"); // Creamos  consulta
		   ps.setString(1,titulo + "%");; // Pasamos el parámetro a la consulta 
		   ResultSet pelis=ps.executeQuery(); // Ejecutamos la consulta parametrizada
		   List<Pelicula> lista = new ArrayList<>();
		   lista = createList(pelis);
		   log.info("tamaño "+lista.size());
			 return lista;


		// Asignamos a la etiqueta de la plantilla “listaPelis.” un arrayList
	
	}

	public Pelicula detallepeliculaAux(Integer id_peli){

		Pelicula peli = new Pelicula();

		String sql="SELECT film_id, title, description, release_year, length, rating FROM film WHERE film_id = ?";
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
			  
		log.info("Pelicula recuperada "+peli.getTitulo());
		return peli;

	}

	

}
