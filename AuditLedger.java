/**
 * GRASP CREATOR PATTERN — AuditLedger
 *
 * Justification: AuditLedger is created by the completion handler in App.java,
 * which already possesses the Mission data (missionId, responderId,
 * threatLevel)
 * needed to initialize it. This satisfies the GRASP Creator principle: assign
 * the responsibility of creating an object to the class that
 * contains/aggregates
 * the initializing data.
 */
public class AuditLedger {

    // ── Encapsulated Attributes ───────────────────────────────────────────────
    private final int missionId;
    private final int responderId;
    private final String threatLevel;
    private final String completionTimestamp;

    // ── Constructor ───────────────────────────────────────────────────────────
    public AuditLedger(int missionId, int responderId,
            String threatLevel, String completionTimestamp) {
        this.missionId = missionId;
        this.responderId = responderId;
        this.threatLevel = threatLevel;
        this.completionTimestamp = completionTimestamp;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int getMissionId() {
        return missionId;
    }

    public int getResponderId() {
        return responderId;
    }

    public String getThreatLevel() {
        return threatLevel;
    }

    public String getCompletionTimestamp() {
        return completionTimestamp;
    }

    // ── Core Behaviour ────────────────────────────────────────────────────────
    /**
     * Generates a formatted audit report for legal and operational records.
     * 
     * @return A multi-line professional report string.
     */
    public String generateReport() {
        String separator = "─────────────────────────────────────────";
        String threatTag;
        switch (threatLevel.toUpperCase()) {
            case "CRITICAL":
                threatTag = "⚠  CRITICAL  — Immediate After-Action Review Required";
                break;
            case "HIGH":
                threatTag = "🔴 HIGH      — Command Review Recommended";
                break;
            case "ELEVATED":
                threatTag = "🟠 ELEVATED  — Standard Debrief Required";
                break;
            default:
                threatTag = "🟢 " + threatLevel.toUpperCase()
                        + "      — Routine Closure";
                break;
        }

        return "ARTEMIS SECURE OPERATIONS — MISSION CLOSURE REPORT\n"
                + separator + "\n"
                + "  Classification   : CONFIDENTIAL\n"
                + "  Report Type      : Post-Mission Audit Ledger\n"
                + separator + "\n"
                + "  Mission ID       : #" + missionId + "\n"
                + "  Responder Unit   : Asset #" + responderId + "\n"
                + "  Threat Level     : " + threatTag + "\n"
                + "  Completion Time  : " + completionTimestamp + "\n"
                + separator + "\n"
                + "  Status           : ✔  MISSION SUCCESSFULLY COMPLETED\n"
                + "  Responder Status : Returned to Base — Available for Redeployment\n"
                + separator + "\n"
                + "  This record has been sealed and logged to the ARTEMIS\n"
                + "  Secure Audit Database. Unauthorized disclosure is prohibited\n"
                + "  under the ARTEMIS Global Extraction Mandate, Section 7(c).\n"
                + separator;
    }
}
