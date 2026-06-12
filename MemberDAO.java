import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO {

    // CREATE: Register a new traveler (V3 schema)
    public void addMember(Member member) {
        String sql = "INSERT INTO Member(username, password, fullName, companions, travelAgency, hotelAddress, hotelContact) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getUsername());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getFullName());
            pstmt.setString(4, member.getCompanions());
            pstmt.setString(5, member.getTravelAgency());
            pstmt.setString(6, member.getHotelAddress());
            pstmt.setString(7, member.getHotelContact());

            pstmt.executeUpdate();
            System.out.println("Member successfully registered in Database.");

        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
        }
    }

    // READ by ID (for SOS trigger and digital shadow)
    public Member getMemberById(int id) {
        String sql = "SELECT * FROM Member WHERE memberID = ?";
        Member member = null;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                member = new Member(
                        rs.getInt("memberID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("fullName"),
                        rs.getString("companions"),
                        rs.getString("travelAgency"),
                        rs.getString("hotelAddress"),
                        rs.getString("hotelContact"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving member: " + e.getMessage());
        }
        return member;
    }

    // READ by username (for login authentication)
    public Member getMemberByUsername(String username) {
        String sql = "SELECT * FROM Member WHERE username = ?";
        Member member = null;

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                member = new Member(
                        rs.getInt("memberID"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("fullName"),
                        rs.getString("companions"),
                        rs.getString("travelAgency"),
                        rs.getString("hotelAddress"),
                        rs.getString("hotelContact"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving member by username: " + e.getMessage());
        }
        return member;
    }

    // UPDATE: Save travel itinerary for an existing member
    public boolean updateTravelItinerary(int memberId, String companions, String contact,
            String agency, String hotel) {
        String sql = "UPDATE Member SET companions = ?, hotelContact = ?, travelAgency = ?, hotelAddress = ? WHERE memberID = ?";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, companions);
            pstmt.setString(2, contact);
            pstmt.setString(3, agency);
            pstmt.setString(4, hotel);
            pstmt.setInt(5, memberId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("[MemberDAO] updateTravelItinerary — rows affected: " + rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("[MemberDAO] Error updating travel itinerary: " + e.getMessage());
            return false;
        }
    }
}
