package com.joysistvi.recordingapp.main;

import org.mindrot.jbcrypt.BCrypt;

import java.time.Year;
import java.util.ArrayList;
import java.sql.*;

public class Main {
    static String url = "jdbc:mysql://localhost:3306/recording_studio_db";
    static String username = "root";
    static String password = "";

    public static void main(String[] args) {

        // --- Testing Albums CRUD ---
        //viewAllAlbums();
//        createAlbum("Coco", 1990, 1);
//        updateAlbum(6, "Coco (Remastered)", 1995, 1);
//        deleteAlbum(6);

        // --- Testing Artists CRUD ---
//        viewAllArtists();
//        createArtist("IV of Spades");
//        System.out.println();
//        updateArtist(9, "IV Of Spades");
//        System.out.println();
//        deleteArtist(8);

        //viewAllArtists();
        //archiveArtist(1);
        //restoreArtist(1);
        //searchArtist("Re");

//        archiveAlbum(1);
//        restoreAlbum(1);
//        searchAlbum("Langit");
//
//        createUser("newUser", "user123", "newuser@gmail.com");
//        loginUser("newUser", "user123"); // should show login success
//        loginUser("newUse", "user123"); // should show login error

        // Guard rail checks
        createArtist(null);
        createArtist("   ");
        searchAlbum(null);
        searchAlbum("  ");

        createAlbum("", 2000, 1);
        createAlbum("Test", -1, 1);
        createAlbum("Test", 2000, -5);

        loginUser("", "password123");
        loginUser("test", "");
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
                "    ON albums.artist_id = artists.artist_id\n" +
                "WHERE albums.is_archived  = 0;"; // Create SQL Select Query

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

    public static void createAlbum(String name, int year, int artistId){
        String query = "INSERT INTO albums (name, year, artist_id) " +
                "VALUES (?, ?, ?)"; // anti -sql

        // Guard Clause
        if (name == null || name.trim().isEmpty()){
            System.out.println("\nValidation Error: Album name is required");
            return;
        }

        if (year < 1850 || year > Year.now().getValue()){
            System.out.println("\nValidation Error: Year should be a appropriate value");
            return;
        }

        if (artistId <= 0){
            System.out.println("\nValidation Error: Artist ID should be positive");
            return;
        }

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, artistId);

            preparedStatement.executeUpdate();

            System.out.println("Album: " + name + " created success!");
            viewAllAlbums();
        } catch (Exception e){
            System.out.println("Create Album ERROR: " + e.getMessage());
        }
    }

    public static void updateAlbum(int id, String name, int year, int artistId){
        String query = "UPDATE albums SET name = ?, year = ?, artist_id = ? WHERE album_id = ?";

        if (id <= 0){
            System.out.println("Validation Error: Album ID should be positive");
            return;
        }

        if (name == null || name.trim().isEmpty()){
            System.out.println("\nValidation Error: Album name is required");
            return;
        }

        if (year < 1850 || year > Year.now().getValue()){
            System.out.println("\nValidation Error: Year should be a appropriate value");
            return;
        }

        if (artistId <= 0){
            System.out.println("\n Validation Error: Artist ID should be positive");
            return;
        }

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
            System.out.println("Update Album ERROR: " + e.getMessage());
        }
    }

    public static void deleteAlbum(int id){
        String query = "DELETE FROM albums WHERE album_id = ?";

        if (id <= 0){
            System.out.println("\nValidation Error: Album ID should be positive");
            return;
        }

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
            System.out.println("Delete Album ERROR: " + e.getMessage());
        }
    }

    public static void viewAllArtists(){
        String query = "SELECT artist_id, name FROM artists WHERE is_archived = 0;";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)
        ) {
            String[] headers = {"ID", "Artist Name"};

            printTable(rs, headers);

        } catch (Exception e) {
            System.out.println("Read Artists ERROR: " + e.getMessage());
        }
    }

    public static void createArtist(String name){
        String query = "INSERT INTO artists (name) " +
                "VALUES (?)"; // anti -sql

        if (name == null || name.trim().isEmpty()){
            System.out.println("\nValidation Error: Artist Name should not be empty");
            return;
        }

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();

            System.out.println("Artist: " + name + " created success!");
            viewAllArtists();
        } catch (Exception e){
            System.out.println("Update Album ERROR: " + e.getMessage());
        }
    }

    public static void updateArtist(int id, String name){
        String query = "UPDATE artists SET name = ? WHERE artist_id = ?";

        if (id <= 0){
            System.out.println("\nValidation Error: Artist ID should be positive");
            return;
        }

        if (name == null || name.trim().isEmpty()){
            System.out.println("\nValidation Error: Artist Name should not be empty");
            return;
        }

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
            System.out.println("Update Artist ERROR: " + e.getMessage());
        }
    }

    public static void deleteArtist(int id){
        String query = "DELETE FROM artists WHERE artist_id = ?";

        if (id <= 0){
            System.out.println("\nValidation Error: Artist ID should be positive");
            return;
        }

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
            System.out.println("Delete Artist ERROR: " + e.getMessage());
        }
    }

    // method for soft deleting (archive) the Artists Table
    public static void archiveArtist(int id){
        String query = "UPDATE artists SET is_archived = 1 WHERE artist_id = ?";

        if (id <= 0){
            System.out.println("\nValidation Error: Artist ID should be positive");
            return;
        }

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Artist ID " + id + " archived successfully!");
                viewAllArtists();
            } else {
                System.out.println("No artist found with ID " + id + ".");
            }
        } catch (SQLException e){
            System.out.println("Archive Artist ERROR: " + e.getMessage());
        }
    }

    public static void restoreArtist(int id){
        String query = "UPDATE artists SET is_archived = 0 WHERE artist_id = ?";

        if (id <= 0){
            System.out.println("\nValidation Error: Artist ID should be positive");
            return;
        }

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Artist ID " + id + " restored successfully!");
                viewAllArtists();
            } else {
                System.out.println("No artist found with ID " + id + ".");
            }
        } catch (SQLException e){
            System.out.println("Restore Artist ERROR: " + e.getMessage());
        }
    }

    // method for searching specific artist

    public static void searchArtist(String keyword) {
        String query = "SELECT artist_id, name FROM artists " +
                "WHERE is_archived = 0 AND name LIKE ? " +
                "ORDER BY artist_id ASC";

        if (keyword == null || keyword.trim().isEmpty()){
            System.out.println("\nValidation Error: Keyword should not be empty");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement prep = conn.prepareStatement(query)) {

            // Clean/trim keyword
            String cleanKeyword = (keyword == null) ? "" : keyword.trim();
            prep.setString(1, "%" + cleanKeyword + "%");

            try (ResultSet res = prep.executeQuery()) {
                System.out.println("\n----- Search Artists (Keyword: '" + cleanKeyword + "') -----");

                // check if artist is present without moving cursor
                if (!res.isBeforeFirst()) {
                    System.out.println("No matching active artists found.");
                } else {
                    String[] headers = {"ID", "Artist Name"};
                    printTable(res, headers);
                }
            }
        } catch (SQLException e) {
            System.out.println("\nSearch Artist Error: " + e.getMessage());
        }
    }

    // method for soft deleting (archive) the Albums Table
    public static void archiveAlbum(int id){
        String query = "UPDATE albums SET is_archived = 1 WHERE album_id = ?";

        if (id <= 0){
            System.out.println("\nValidation Error: Album ID should be positive");
            return;
        }

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Album ID " + id + " archived successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e){
            System.out.println("Archive Album ERROR: " + e.getMessage());
        }
    }

    public static void restoreAlbum(int id){
        String query = "UPDATE albums SET is_archived = 0 WHERE album_id = ?";

        if (id <= 0){
            System.out.println("\nValidation Error: Album ID should be positive");
            return;
        }

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Album ID " + id + " restored successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e){
            System.out.println("Restore Album ERROR: " + e.getMessage());
        }
    }

    // method for searching specific artist

    public static void searchAlbum(String keyword) {
        String query = "SELECT album_id, name FROM albums " +
                "WHERE is_archived = 0 AND name LIKE ? " +
                "ORDER BY album_id ASC";

        if (keyword == null || keyword.trim().isEmpty()){
            System.out.println("\nValidation Error: Keyword should not be empty");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement prep = conn.prepareStatement(query)) {

            // Clean/trim keyword
            String cleanKeyword = (keyword == null) ? "" : keyword.trim();
            prep.setString(1, "%" + cleanKeyword + "%");

            try (ResultSet res = prep.executeQuery()) {
                System.out.println("\n----- Search Albums (Keyword: '" + cleanKeyword + "') -----");

                // check if artist is present without moving cursor
                if (!res.isBeforeFirst()) {
                    System.out.println("No matching active albums found.");
                } else {
                    String[] headers = {"ID", "Album Name"};
                    printTable(res, headers);
                }
            }
        } catch (SQLException e) {
            System.out.println("\nSearch Album Error: " + e.getMessage());
        }
    }

    /*
     * WHAT IS VALIDATION?
     * Validation is the process of checking whether incoming data is safe, complete,
     * and valid BEFORE processing it or saving it to the database (e.g., non-empty
     * username, valid email format, minimum password length, positive ID).
     *
     * Why do it early?
     * - Prevents bad/incomplete data from entering the database.
     * - Avoids unnecessary database queries when input is already invalid.
     * - Gives clear feedback on what went wrong.
     *
     * What is a Guard Clause?
     * A guard clause is an early-exit check (`if (invalid) { ... return; }`) placed
     * at the top of a method so the code "fails fast" without deep if-else nesting.
     */

    public static void createUser (String uName, String rawPassword, String email){
        // Guard Clause
        if (uName == null || uName.trim().isEmpty()){
            System.out.println("\nValidation Error: Username is required");
            return;
        }

        if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")){
            System.out.println("\nValidation Error: Valid Email is required");
            return;
        }

        if (rawPassword == null || rawPassword.length() < 6) {
            System.out.println("\nValidation Error: Valid Password is required");
            return;
        }

        String hashedPassword = BCrypt.hashpw(rawPassword.trim(), BCrypt.gensalt());
        String query = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'USER');";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){
            preparedStatement.setString(1, uName);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, email);

            preparedStatement.executeUpdate();

            System.out.println("User: " + uName + " created success!");
            viewAllUsers();
        } catch (Exception e){
            System.out.println("Create User ERROR: " + e.getMessage());
        }
    }

    public static void loginUser(String uname, String candidatePassword) {
        // GUARD CLAUSE 1: Early exit if username is blank/null.
        if (uname == null || uname.trim().isEmpty()) {
            System.out.println("\nValidation Error: Username is required to log in.");
            return;
        }

        // GUARD CLAUSE 2: Early exit if password is missing/empty.
        if (candidatePassword == null || candidatePassword.isEmpty()) {
            System.out.println("\nValidation Error: Password is required to log in.");
            return;
        }

        // Look up the user by username first so we can retrieve the stored BCrypt hash.
        // Notice we do NOT select or read 'id' here.
        String query = "SELECT username, password, role FROM users WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement prep = conn.prepareStatement(query)) {

            prep.setString(1, uname.trim());

            try (ResultSet res = prep.executeQuery()) {
                if (res.next()) {
                    String storedHash = res.getString("password");

                    // BCrypt.checkpw(plainText, storedHash) safely compares the entered password
                    // against the stored hash (extracting the salt automatically).
                    // Note: If a seeded DB row has a plain-text password instead of a BCrypt hash,
                    // BCrypt.checkpw() will return false / throw an error unless hashed first.
                    if (BCrypt.checkpw(candidatePassword, storedHash)) {
                        String username = res.getString("username");
                        String role = res.getString("role");
                        System.out.println("\nLogin successful! Welcome, " + username + " (Role: " + role + ")");
                    } else {
                        // Generic message: avoids telling attackers whether the username exists or not.
                        System.out.println("\nLogin failed: Invalid username or password.");
                    }
                } else {
                    // Same generic message when username is not found.
                    System.out.println("\nLogin failed: Invalid username or password.");
                }
            }
        } catch (SQLException e) {
            System.out.println("\nLogin Error: " + e.getMessage());
        }
    }

    public static void viewAllUsers(){
        String query = "SELECT user_id, username, email FROM users WHERE is_archived = 0;";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)
        ) {
            String[] headers = {"ID", "User", "Email"};

            printTable(rs, headers);

        } catch (Exception e) {
            System.out.println("Read Users ERROR: " + e.getMessage());
        }
    }
// ============================================================================
// PART 5: BRIDGE TO 3-TIER ARCHITECTURE (NO CONTROLLER LAYER)
// ============================================================================
// Once all features work in Main, split the code into focused layers/packages:
//
// - config       : Database connection management
//                  Examples: DbConnection
//
// - model        : Plain data carriers (POJOs / entities)
//                  Examples: Artist, Album, User
//
// - repository   : SQL queries & ResultSet mapping only
//                  Examples: ArtistRepository, ArtistRepositoryImpl,
//                            UserRepository, UserRepositoryImpl
//
// - service      : Validation guard clauses, business rules & password hashing
//                  Examples: ArtistService, ArtistServiceImpl,
//                            UserService, UserServiceImpl
//
// - presentation : Console menus, Scanner prompts & formatted output
//                  Examples: ArtistView, UserView
//
// - Entry Point  : Dependency wiring & startup flow (main())
//                  Example : App
// ============================================================================
}
