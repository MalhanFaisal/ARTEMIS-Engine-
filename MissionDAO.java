import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MissionDAO {

    // 1. CREATE: Use Case 3 (Trigger Emergency SOS)
    public void addMission(Mission mission) {
        String sql = "INSERT INTO Mission(memberID, responderID, triggerTimestamp, status, classification, threatLevel) VALUES(?,?,?,?,?,?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mission.getMemberId());
            pstmt.setInt(2, mission.getResponderId()); // 0 = unassigned at SOS creation
            pstmt.setString(3, mission.getTriggerTimestamp());
            pstmt.setString(4, mission.getStatus());
            pstmt.setString(5, mission.getClassification());
            pstmt.setString(6, mission.getThreatLevel());

            pstmt.executeUpdate();
            System.out.println("Emergency SOS successfully logged in Database.");

        } catch (SQLException e) {
            System.out.println("Error logging mission: " + e.getMessage());
        }
    }

    // 2. UPDATE: Use Case 5 & 11 (Verify Status / Update Lifecycle State)
    public void updateMissionStatus(int missionId, String newStatus) {
        String sql = "UPDATE Mission SET status = ? WHERE missionID = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, missionId);

            pstmt.executeUpdate();
            System.out.println("Mission " + missionId + " status updated to: " + newStatus);

        } catch (SQLException e) {
            System.out.println("Error updating mission status: " + e.getMessage());
        }
    }

    // 3. READ: Pulls all active missions for the UI Dashboard
    public List<Mission> getActiveMissions() {
        String sql = "SELECT * FROM Mission WHERE status != 'Extracted'";
        List<Mission> activeMissions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                activeMissions.add(new Mission(
                        rs.getInt("missionID"),
                        rs.getInt("memberID"),
                        rs.getInt("responderID"), // NEW: read FK column
                        rs.getString("triggerTimestamp"),
                        rs.getString("status"),
                        rs.getString("classification"),
                        rs.getString("threatLevel")));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving active missions: " + e.getMessage());
        }
        return activeMissions;
    }

    // 3B. READ: Pulls active missions with Member names (LEFT JOIN)
    public List<App.MissionAlert> getActiveMissionAlerts() {
        String sql = "SELECT m.missionID, mem.fullName, m.threatLevel, m.status " +
                "FROM Mission m " +
                "LEFT JOIN Member mem ON m.memberID = mem.memberID " +
                "WHERE m.status != 'Extracted'";
        List<App.MissionAlert> alerts = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String memberName = rs.getString("fullName");
                if (memberName == null || memberName.trim().isEmpty()) {
                    memberName = "Unknown (ID: " + rs.getInt("missionID") + ")";
                }

                alerts.add(new App.MissionAlert(
                        rs.getInt("missionID"),
                        memberName,
                        rs.getString("threatLevel"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving active mission alerts: " + e.getMessage());
        }
        return alerts;
    }

    // 4. UPDATE: Assign (link) a Responder FK to a Mission row
    public void assignResponderToMission(int missionId, int responderId) {
        String sql = "UPDATE Mission SET responderID = ? WHERE missionID = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, responderId);
            pstmt.setInt(2, missionId);
            pstmt.executeUpdate();
            System.out.println("Responder " + responderId + " assigned to Mission " + missionId);

        } catch (SQLException e) {
            System.out.println("Error assigning responder to mission: " + e.getMessage());
        }
    }

    // 5. READ: All missions for a specific member (Digital Shadow — Incident
    // History)
    public List<Mission> getMissionsByMemberId(int memberId) {
        String sql = "SELECT * FROM Mission WHERE memberID = ? ORDER BY missionID DESC";
        List<Mission> missions = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    missions.add(new Mission(
                            rs.getInt("missionID"),
                            rs.getInt("memberID"),
                            rs.getInt("responderID"),
                            rs.getString("triggerTimestamp"),
                            rs.getString("status"),
                            rs.getString("classification"),
                            rs.getString("threatLevel")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching missions for member " + memberId + ": " + e.getMessage());
        }
        return missions;
    }
}
