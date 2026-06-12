import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

        public static void initializeDatabase() {
                String createMemberTable = "CREATE TABLE IF NOT EXISTS Member (" +
                                "memberID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "username TEXT UNIQUE," +
                                "password TEXT," +
                                "fullName TEXT," +
                                "companions TEXT," +
                                "travelAgency TEXT," +
                                "hotelAddress TEXT," +
                                "hotelContact TEXT" +
                                ");";

                String createMissionTable = "CREATE TABLE IF NOT EXISTS Mission (" +
                                "missionID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "memberID INTEGER," +
                                "responderID INTEGER," +
                                "triggerTimestamp TEXT," +
                                "status TEXT," +
                                "classification TEXT," +
                                "threatLevel TEXT," +
                                "FOREIGN KEY (memberID) REFERENCES Member(memberID)," +
                                "FOREIGN KEY (responderID) REFERENCES Responder(responderID)" +
                                ");";

                String createResponderTable = "CREATE TABLE IF NOT EXISTS Responder (" +
                                "responderID INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "unitName TEXT NOT NULL," +
                                "specialtyType TEXT," +
                                "deploymentStatus TEXT DEFAULT 'Available'" +
                                ");";

                try (Connection conn = DatabaseManager.getInstance().getConnection();
                                Statement stmt = conn.createStatement()) {

                        stmt.execute(createMemberTable);
                        stmt.execute(createMissionTable);
                        stmt.execute(createResponderTable);

                        System.out.println("Database tables initialized successfully.");

                        // ── Seed Responders (if empty) ────────────────────────────
                        var rsR = stmt.executeQuery("SELECT COUNT(*) FROM Responder");
                        if (rsR.next() && rsR.getInt(1) == 0) {
                                stmt.execute("INSERT INTO Responder (unitName, specialtyType, deploymentStatus) VALUES ('Alpha Team', 'Medical Extraction', 'Available')");
                                stmt.execute("INSERT INTO Responder (unitName, specialtyType, deploymentStatus) VALUES ('Bravo Team', 'Tactical Security', 'Available')");
                                stmt.execute("INSERT INTO Responder (unitName, specialtyType, deploymentStatus) VALUES ('Charlie Team', 'Hostile Extraction', 'Available')");
                                stmt.execute("INSERT INTO Responder (unitName, specialtyType, deploymentStatus) VALUES ('Delta Team', 'Search & Rescue', 'Available')");
                                System.out.println("Seeded 4 responder units.");
                        }

                        // ── Force-seed 3 Dummy Members ───────────────────────────
                        stmt.execute("INSERT OR IGNORE INTO Member (memberID, username, password, fullName, companions, travelAgency, hotelAddress, hotelContact) "
                                        + "VALUES "
                                        + "(1, 'demo', 'demo', 'Demo Traveler', 'Solo', 'GlobalAid Travel', '14 Embassy Row, Kabul', '+1-555-0199'), "
                                        + "(2, 'jsmith', 'pass123', 'Jane Smith', 'Family', 'SafeVoyage', 'The Continental', '+1-555-0200'), "
                                        + "(3, 'valpha', 'pass123', 'VIP Alpha', 'Delegation', 'GovTravel', 'US Embassy', '+1-555-0300')");
                        System.out.println("Force-seeded members 1, 2, 3 (INSERT OR IGNORE).");

                        // ── Force-seed 6 global SOS missions using INSERT OR IGNORE ──
                        stmt.execute(
                                        "INSERT OR IGNORE INTO Mission (missionID, memberID, triggerTimestamp, status, classification, threatLevel) VALUES "
                                                        + "(101, 1, '2026-05-02 07:14:33', 'Awaiting Verification', 'Hostile Extraction', 'CRITICAL')");
                        stmt.execute(
                                        "INSERT OR IGNORE INTO Mission (missionID, memberID, triggerTimestamp, status, classification, threatLevel) VALUES "
                                                        + "(102, 2, '2026-05-02 08:42:11', 'Awaiting Verification', 'Medical', 'CRITICAL')");
                        stmt.execute(
                                        "INSERT OR IGNORE INTO Mission (missionID, memberID, triggerTimestamp, status, classification, threatLevel) VALUES "
                                                        + "(103, 3, '2026-05-02 09:03:55', 'Dispatched', 'Hostile Extraction', 'CRITICAL')");
                        stmt.execute(
                                        "INSERT OR IGNORE INTO Mission (missionID, memberID, triggerTimestamp, status, classification, threatLevel) VALUES "
                                                        + "(104, 1, '2026-05-02 10:18:00', 'Awaiting Verification', 'Environmental', 'ELEVATED')");
                        stmt.execute(
                                        "INSERT OR IGNORE INTO Mission (missionID, memberID, triggerTimestamp, status, classification, threatLevel) VALUES "
                                                        + "(105, 2, '2026-05-02 11:07:29', 'Dispatched', 'Medical', 'ELEVATED')");
                        stmt.execute(
                                        "INSERT OR IGNORE INTO Mission (missionID, memberID, triggerTimestamp, status, classification, threatLevel) VALUES "
                                                        + "(106, 999, '2026-05-02 12:30:48', 'Awaiting Verification', 'Environmental', 'LOW')" // 999
                                                                                                                                               // simulates
                                                                                                                                               // missing
                                                                                                                                               // member
                        );

                        System.out.println("Force-seeded missions 101-106 (INSERT OR IGNORE).");

                } catch (SQLException e) {
                        System.err.println("Error initializing database: " + e.getMessage());
                }
        }
}
