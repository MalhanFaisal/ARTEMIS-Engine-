public class Member {
    // Encapsulated private attributes (V3 Schema)
    private int memberId;
    private String username;
    private String password;
    private String fullName;
    private String companions;
    private String travelAgency;
    private String hotelAddress;
    private String hotelContact;

    // Constructor with ID (for reading from DB)
    public Member(int memberId, String username, String password, String fullName,
            String companions, String travelAgency, String hotelAddress, String hotelContact) {
        this.memberId = memberId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.companions = companions;
        this.travelAgency = travelAgency;
        this.hotelAddress = hotelAddress;
        this.hotelContact = hotelContact;
    }

    // Constructor without ID (for creating new member before saving to DB)
    public Member(String username, String password, String fullName,
            String companions, String travelAgency, String hotelAddress, String hotelContact) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.companions = companions;
        this.travelAgency = travelAgency;
        this.hotelAddress = hotelAddress;
        this.hotelContact = hotelContact;
    }

    // Legacy stubs so ThreatAnalyzer still compiles
    public String getMedicalHistory() {
        return "";
    }

    public String getBloodType() {
        return "";
    }

    // Getters and Setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int id) {
        this.memberId = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String n) {
        this.fullName = n;
    }

    public String getCompanions() {
        return companions;
    }

    public void setCompanions(String c) {
        this.companions = c;
    }

    public String getTravelAgency() {
        return travelAgency;
    }

    public void setTravelAgency(String t) {
        this.travelAgency = t;
    }

    public String getHotelAddress() {
        return hotelAddress;
    }

    public void setHotelAddress(String h) {
        this.hotelAddress = h;
    }

    public String getHotelContact() {
        return hotelContact;
    }

    public void setHotelContact(String h) {
        this.hotelContact = h;
    }
}
