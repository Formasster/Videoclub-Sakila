package com.alesiar48.alesiar48.videoclub.servicios;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alesiar48.alesiar48.videoclub.modelo.Pelicula;

@Service
public class PeliculaServicio {
    
    //Obtener el número de películas

    public Integer numPeliculas(List<Pelicula>pelis){
        return pelis.size();
    }

    	
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
	
}
