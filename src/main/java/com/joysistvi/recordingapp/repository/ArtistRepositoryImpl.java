package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.Artist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepositoryImpl implements ArtistRepository {

    private final DbConnection dbConnection;

    public ArtistRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Artist> findAllActive() {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT * FROM artists WHERE is_archived = 0";

        try (Connection connection = dbConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                artists.add(new Artist(id, name));

            }
        } catch (SQLException e) {
            System.out.println("Read Artists Error: " + e.getMessage());
        }

        return artists;
    }

//    // Create Artist
//    public void createArtist(String name) {
//        // Guard Clause
//        if (name == null || name.trim().isEmpty()) {
//            System.out.println("\nValidation Error: Artist name is required.");
//            return;
//        }
//
//        String query = "INSERT INTO artists (name) VALUES (?)";
//
//        try (Connection connection = dbConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setString(1, name.trim());
//
//            preparedStatement.executeUpdate();
//
//            System.out.println("\nArtist: " + name.trim() + " created successfully!");
//            viewAllArtists();
//        } catch (SQLException e) {
//            System.out.println("\nCreate Artist Error: " + e.getMessage());
//        }
//    }
//
//    // Update Artist
//    public void updateArtist(int id, String name) {
//        // Guard Clauses
//        if (id <= 0) {
//            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
//            return;
//        }
//        if (name == null || name.trim().isEmpty()) {
//            System.out.println("\nValidation Error: Artist name is required.");
//            return;
//        }
//
//        String query = "UPDATE artists SET name = ? WHERE id = ?";
//
//        try (Connection connection = dbConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setString(1, name.trim());
//            preparedStatement.setInt(2, id);
//
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            if (rowsAffected > 0) {
//                System.out.println("\nArtist ID " + id + " updated successfully!");
//                viewAllArtists();
//            } else {
//                System.out.println("\nNo artist found with ID " + id + ".");
//            }
//        } catch (SQLException e) {
//            System.out.println("\nUpdate Artist Error: " + e.getMessage());
//        }
//    }
//
//    // Delete Artist
//    public void deleteArtist(int id) {
//        // Guard Clause
//        if (id <= 0) {
//            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
//            return;
//        }
//
//        String query = "DELETE FROM artists WHERE id = ?";
//
//        try (Connection connection = dbConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, id);
//
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            if (rowsAffected > 0) {
//                System.out.println("\nArtist ID " + id + " deleted successfully!");
//                viewAllArtists();
//            } else {
//                System.out.println("\nNo artist found with ID " + id + ".");
//            }
//        } catch (SQLException e) {
//            System.out.println("\nDelete Artist Error: " + e.getMessage());
//        }
//    }
//
//    // Archive Artist (Soft Delete)
//    public static void archiveArtist(int id) {
//        // Guard Clause
//        if (id <= 0) {
//            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
//            return;
//        }
//
//        String query = "UPDATE artists SET is_archived = 1 WHERE id = ?";
//
//        try (Connection connection = dbConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, id);
//
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            if (rowsAffected > 0) {
//                System.out.println("\nArtist ID " + id + " archived successfully!");
//                viewAllArtists();
//            } else {
//                System.out.println("\nNo artist found with ID " + id + ".");
//            }
//        } catch (SQLException e) {
//            System.out.println("\nArchive Artist Error: " + e.getMessage());
//        }
//    }
//
//    // Restore Artist
//    public static void restoreArtist(int id) {
//        // Guard Clause
//        if (id <= 0) {
//            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
//            return;
//        }
//
//        String query = "UPDATE artists SET is_archived = 0 WHERE id = ?";
//
//        try (Connection connection = dbConnection.connect();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, id);
//
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            if (rowsAffected > 0) {
//                System.out.println("\nArtist ID " + id + " restored successfully!");
//                viewAllArtists();
//            } else {
//                System.out.println("\nNo artist found with ID " + id + ".");
//            }
//        } catch (SQLException e) {
//            System.out.println("\nRestore Artist Error: " + e.getMessage());
//        }
//    }
//
//    // Search Artist
//    public static void searchArtist(String keyword) {
//        // Guard Clause
//        if (keyword == null || keyword.trim().isEmpty()) {
//            System.out.println("\nValidation Error: Search keyword is required.");
//            return;
//        }
//
//        String query = "SELECT id, name FROM artists " +
//                "WHERE is_archived = 0 AND name LIKE ? " +
//                "ORDER BY id ASC";
//
//        try (Connection conn = dbConnection.connect();
//             PreparedStatement prep = conn.prepareStatement(query)) {
//
//            String cleanKeyword = keyword.trim();
//            prep.setString(1, "%" + cleanKeyword + "%");
//
//            try (ResultSet res = prep.executeQuery()) {
//                System.out.println("\n----- Search Artists (Keyword: '" + cleanKeyword + "') -----");
//                System.out.printf("%-4s %-25s%n", "ID", "Artist Name");
//
//                boolean found = false;
//                while (res.next()) {
//                    found = true;
//                    System.out.printf("%-4d %-25s%n",
//                            res.getInt("id"),
//                            res.getString("name"));
//                }
//
//                if (!found) {
//                    System.out.println("No matching active artists found.");
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("\nSearch Artist Error: " + e.getMessage());
//        }
//    }

}
