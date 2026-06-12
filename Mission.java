public class Mission {
    // Encapsulated private attributes
    private int missionId;
    private int memberId; // Foreign key reference to the Member who triggered it
    private int responderId; // Foreign key reference to the assigned Responder (0 = unassigned)
    private String triggerTimestamp;
    private String status; // e.g., "Pending", "Verified", "Extracted"
    private String classification; // e.g., "Medical", "Hostile Extraction"
    private String threatLevel; // e.g., "LOW", "ELEVATED", "CRITICAL"

    // Constructor with full DB fields (used when loading from the database)
    public Mission(int missionId, int memberId, int responderId, String triggerTimestamp, String status,
            String classification, String threatLevel) {
        this.missionId = missionId;
        this.memberId = memberId;
        this.responderId = responderId;
        this.triggerTimestamp = triggerTimestamp;
        this.status = status;
        this.classification = classification;
        this.threatLevel = threatLevel;
    }

    // Constructor without IDs (used when creating a brand new SOS alert —
    // responderId defaults to 0)
    public Mission(int memberId, String triggerTimestamp, String status, String classification, String threatLevel) {
        this.memberId = memberId;
        this.responderId = 0; // Unassigned until dispatched
        this.triggerTimestamp = triggerTimestamp;
        this.status = status;
        this.classification = classification;
        this.threatLevel = threatLevel;
    }

    // Getters and Setters
    public int getMissionId() {
        return missionId;
    }

    public void setMissionId(int missionId) {
        this.missionId = missionId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getResponderId() {
        return responderId;
    }

    public void setResponderId(int responderId) {
        this.responderId = responderId;
    }

    public String getTriggerTimestamp() {
        return triggerTimestamp;
    }

    public void setTriggerTimestamp(String triggerTimestamp) {
        this.triggerTimestamp = triggerTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(String threatLevel) {
        this.threatLevel = threatLevel;
    }
}
