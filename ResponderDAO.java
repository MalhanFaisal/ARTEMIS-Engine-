import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResponderDAO {

    // 1. READ: Use Case 7 (Query Proximity Assets - Find Available Teams)
    public List<Responder> getAvailableResponders() {
        String sql = "SELECT * FROM Responder WHERE deploymentStatus = 'Available'";
        List<Responder> responders = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                responders.add(new Responder(
                        rs.getInt("responderID"),
                        rs.getString("unitName"),
                        rs.getString("specialtyType"),
                        rs.getString("deploymentStatus")));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving responders: " + e.getMessage());
        }
        return responders;
    }

    // 2. UPDATE: Use Case 8 (Authorize Extraction Mission)
    public void deployResponder(int responderId) {
        String sql = "UPDATE Responder SET deploymentStatus = 'Deployed' WHERE responderID = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, responderId);
            pstmt.executeUpdate();
            System.out.println("Responder " + responderId + " has been marked as Deployed.");

        } catch (SQLException e) {
            System.out.println("Error deploying responder: " + e.getMessage());
        }
    }

    // 3. UPDATE: Use Case Phase 5 (Return Responder to Base after Mission Complete)
    public void returnResponderToBase(int responderId) {
        String sql = "UPDATE Responder SET deploymentStatus = 'Available' WHERE responderID = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, responderId);
            pstmt.executeUpdate();
            System.out.println("Responder " + responderId + " has been returned to base.");

        } catch (SQLException e) {
            System.out.println("Error returning responder to base: " + e.getMessage());
        }
    }

    // 4. READ: Use Case Phase 5 (Get deployed responders for radar selection)
    public List<Responder> getDeployedResponders() {
        String sql = "SELECT * FROM Responder WHERE deploymentStatus = 'Deployed'";
        List<Responder> responders = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                responders.add(new Responder(
                        rs.getInt("responderID"),
                        rs.getString("unitName"),
                        rs.getString("specialtyType"),
                        rs.getString("deploymentStatus")));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving deployed responders: " + e.getMessage());
        }
        return responders;
    }
}
