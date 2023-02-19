package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class GenreDao {

	public List<Genre> listGenres() {
		ArrayList<Genre> genres = new ArrayList<>();

		try(Connection connection = getDataSource().getConnection()) {
			try(Statement statement = connection.createStatement()) {
				try(ResultSet resultSet = statement.executeQuery("SELECT * FROM genre")) {
					while(resultSet.next()) {
						Genre genre = new Genre(resultSet.getInt("idgenre"),
								resultSet.getString("name"));
						genres.add(genre);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return genres;
	}

	public Genre getGenre(String name) {
		try(Connection connection = getDataSource().getConnection()) {
			try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")) {
				statement.setString(1, name);
				try(ResultSet resultSet = statement.executeQuery()) {
					if(resultSet.next()) {
						return new Genre(resultSet.getInt("idgenre"),
								resultSet.getString("name"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Genre addGenre(String name) {
		try(Connection connection = getDataSource().getConnection()) {
			try(PreparedStatement statement = connection.prepareStatement("INSERT INTO genre(name) VALUES(?)")) {
				statement.setString(1, name);
				statement.executeUpdate();
				ResultSet generatedKeys = statement.getGeneratedKeys();
				if(generatedKeys.next()) {
					return new Genre(generatedKeys.getInt(1), name);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
