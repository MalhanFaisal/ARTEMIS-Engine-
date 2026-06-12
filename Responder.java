public class Responder {
    // Encapsulated private attributes
    private int responderId;
    private String unitName;
    private String specialtyType; // e.g., "Paramedic", "Tactical", "Aviation"
    private String deploymentStatus; // e.g., "Available", "Deployed", "Standby"

    // Constructor with ID
    public Responder(int responderId, String unitName, String specialtyType, String deploymentStatus) {
        this.responderId = responderId;
        this.unitName = unitName;
        this.specialtyType = specialtyType;
        this.deploymentStatus = deploymentStatus;
    }

    // Constructor without ID
    public Responder(String unitName, String specialtyType, String deploymentStatus) {
        this.unitName = unitName;
        this.specialtyType = specialtyType;
        this.deploymentStatus = deploymentStatus;
    }

    // Getters and Setters
    public int getResponderId() {
        return responderId;
    }

    public void setResponderId(int responderId) {
        this.responderId = responderId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getSpecialtyType() {
        return specialtyType;
    }

    public void setSpecialtyType(String specialtyType) {
        this.specialtyType = specialtyType;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }
}
