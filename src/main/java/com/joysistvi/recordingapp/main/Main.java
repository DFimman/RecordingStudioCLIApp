package com.joysistvi.recordingapp.main;

import java.util.ArrayList;
import java.sql.*;

public class Main {
    static String url = "jdbc:mysql://localhost:3306/recording_studio_db";
    static String username = "root";
    static String password = "";

    public static void main(String[] args) throws SQLException {
        try {
            Connection connection = connect();
            System.out.println("Connected Successfully! " + connection);
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }

        // --- Testing Albums CRUD ---
        viewAllAlbums();
//        createAlbum("Coco", 1990, 1);
//        updateAlbum(6, "Coco (Remastered)", 1995, 1);
//        deleteAlbum(6);

        System.out.println();

        // --- Testing Artists CRUD ---
        viewAllArtists();
        createArtist("IV of Spades");
        System.out.println();
        updateArtist(9, "IV Of Spades");
        System.out.println();
        deleteArtist(8);
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // CRUD Operation

    // Read Albums Table
    public static void viewAllAlbums(){
        String query = "SELECT\n" +
                "    albums.album_id,\n" +
                "    albums.name,\n" +
                "    YEAR(albums.year) AS year,\n" +
                "    artists.name AS artist_name\n" +
                "FROM albums\n" +
                "INNER JOIN artists\n" +
                "    ON albums.artist_id = artists.artist_id;"; // Create SQL Select Query

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)
        ) {
            String[] headers = {"ID", "Name", "Year", "Artist Name"};

            printTable(rs, headers);

        } catch (Exception e) {
            System.out.println("Read Albums: " + e.getMessage());
        }
    }


    public static void printTable(ResultSet rs, String[] headers) throws SQLException {
        int[] columnWidths = new int[headers.length];
        ArrayList<String[]> rows = new ArrayList<>();

        // Set initial widths based on header lengths
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
        }

        // Store rows and find the longest value in each column
        while (rs.next()) {
            String[] row = new String[headers.length];

            for (int i = 0; i < headers.length; i++) {
                String value = rs.getString(i + 1);
                row[i] = (value == null) ? "NULL" : value;

                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }

            rows.add(row);
        }

        // Build the horizontal border
        StringBuilder border = new StringBuilder("+");
        for (int width : columnWidths) {
            border.append("-".repeat(width + 2)).append("+");
        }

        // Print headers
        System.out.println(border);
        for (int i = 0; i < headers.length; i++) {
            System.out.printf("| %-" + columnWidths[i] + "s ", headers[i]);
        }
        System.out.println("|");
        System.out.println(border);

        // Print all records
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                System.out.printf("| %-" + columnWidths[i] + "s ", row[i]);
            }
            System.out.println("|");
        }

        System.out.println(border);
    }

    public static void createAlbum(String name, int year, int artistID){
        String query = "INSERT INTO albums (name, year, artist_id) " +
                "VALUES (?, ?, ?)"; // anti -sql

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, artistID);

            preparedStatement.executeUpdate();

            System.out.println("Album: " + name + " created success!");
            viewAllAlbums();
        } catch (Exception e){
            System.out.println("Update Album: " + e.getMessage());
        }
    }

    public static void updateAlbum(int id, String name, int year, int artistId){
        String query = "UPDATE albums SET name = ?, year = ?, artist_id = ? WHERE album_id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, artistId);
            preparedStatement.setInt(4, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Album ID " + id + " updated successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e){
            System.out.println("Update Album: " + e.getMessage());
        }
    }

    public static void deleteAlbum(int id){
        String query = "DELETE FROM albums WHERE album_id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Album ID " + id + " deleted successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e){
            System.out.println("Delete Album: " + e.getMessage());
        }
    }

    public static void viewAllArtists(){
        String query = "SELECT artist_id, name FROM artists;";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)
        ) {
            String[] headers = {"ID", "Artist Name"};

            printTable(rs, headers);

        } catch (Exception e) {
            System.out.println("Read Artists: " + e.getMessage());
        }
    }

    public static void createArtist(String name){
        String query = "INSERT INTO artists (name) " +
                "VALUES (?)"; // anti -sql

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();

            System.out.println("Artist: " + name + " created success!");
            viewAllArtists();
        } catch (Exception e){
            System.out.println("Update Album: " + e.getMessage());
        }
    }

    public static void updateArtist(int id, String name){
        String query = "UPDATE artists SET name = ? WHERE artist_id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Artist ID " + id + " updated successfully!");
                viewAllArtists();
            } else {
                System.out.println("No artist found with ID " + id + ".");
            }
        } catch (SQLException e){
            System.out.println("Update Artist: " + e.getMessage());
        }
    }

    public static void deleteArtist(int id){
        String query = "DELETE FROM artists WHERE artist_id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Artist ID " + id + " deleted successfully!");
                viewAllArtists();
            } else {
                System.out.println("No artist found with ID " + id + ".");
            }
        } catch (SQLException e){
            System.out.println("Delete Artist: " + e.getMessage());
        }
    }
}



//SELECT albums.id, albums.name, albums.year, artists.name
//FROM albums
//JOINS artists
//ON albums.artist_id = artists.artist_id
