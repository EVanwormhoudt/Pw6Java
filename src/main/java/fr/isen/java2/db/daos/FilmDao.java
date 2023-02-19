package fr.isen.java2.db.daos;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;

public class FilmDao {

	public List<Film> listFilms() {
		ArrayList<Film> films = new ArrayList<>();
		try(Connection connection = getDataSource().getConnection()) {
			try(Statement statement = connection.createStatement()) {
				try(ResultSet resultSet = statement.executeQuery(
						"SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre")) {
					while(resultSet.next()) {
						int id = resultSet.getInt("idfilm");
						String title = resultSet.getString("title");
						java.sql.Date legacyDate = resultSet.getDate("release_date");
						LocalDate releaseDate = legacyDate.toLocalDate();
						int duration = resultSet.getInt("duration");
						String director = resultSet.getString("director");
						String summary = resultSet.getString("summary");
						Genre genre = new Genre(resultSet.getInt("idgenre"),
								resultSet.getString("name"));
						Film film = new Film(id, title, releaseDate, genre, duration, director, summary);
						films.add(film);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	public List<Film> listFilmsByGenre(String genreName) {
		ArrayList<Film> films = new ArrayList<>();
		try(Connection connection = getDataSource().getConnection()) {
			try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre WHERE genre.name = ?")){
				statement.setString(1, genreName);
				try(ResultSet resultSet = statement.executeQuery()) {
					while(resultSet.next()) {
						int id = resultSet.getInt("idfilm");
						String title = resultSet.getString("title");
						java.sql.Date legacyDate = resultSet.getDate("release_date");
						LocalDate releaseDate = legacyDate.toLocalDate();
						int duration = resultSet.getInt("duration");
						String director = resultSet.getString("director");
						String summary = resultSet.getString("summary");
						Genre genre = new Genre(resultSet.getInt("idgenre"),
								resultSet.getString("name"));
						Film film = new Film(id, title, releaseDate, genre, duration, director, summary);
						films.add(film);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	public Film addFilm(Film film) {
		try(Connection connection = getDataSource().getConnection()) {
			try(PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO film(title, release_date, genre_id, duration, director, summary) VALUES(?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, film.getTitle());
				statement.setDate(2, Date.valueOf(film.getReleaseDate()));
				statement.setInt(3, film.getGenre().getId());
				statement.setInt(4, film.getDuration());
				statement.setString(5, film.getDirector());
				statement.setString(6, film.getSummary());
				statement.executeUpdate();
				try(ResultSet resultSet = statement.getGeneratedKeys()) {
					if(resultSet.next()) {
						film.setId(resultSet.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return film;
	}
}
