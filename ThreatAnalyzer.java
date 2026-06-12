public class ThreatAnalyzer {

    /**
     * Analyzes an emergency description and member profile to calculate a threat
     * level.
     *
     * @param emergencyDescription Free-text description of the emergency situation.
     * @param member               The Member object whose profile is being
     *                             analyzed.
     * @return "CRITICAL", "ELEVATED", or "LOW" based on computed threat score.
     */
    public static String calculateThreatLevel(String emergencyDescription, Member member) {
        int threatScore = 0;

        // --- Analyze Emergency Description ---
        String desc = emergencyDescription != null ? emergencyDescription.toLowerCase() : "";

        // Critical (Lethal/Immediate) keywords: +50
        String[] criticalKeywords = { "gun", "shot", "bleeding", "heart", "attack", "dying",
                "hostage", "ambush", "hemorrhage", "unconscious",
                "stroke", "terrorist", "bomb", "kidnapped",
                "active shooter", "trauma", "unresponsive", "bleeding out", "cardiac arrest",
                "explosion", "abducted", "held captive", "riot", "overdose" };
        for (String kw : criticalKeywords) {
            if (desc.contains(kw)) {
                threatScore += 50;
                break; // One hit is enough to make it critical
            }
        }

        // Elevated (Urgent/Non-Lethal) keywords: +25
        String[] elevatedKeywords = { "broken", "pain", "trapped", "scared", "threat",
                "lost", "fever", "fracture", "stranded", "theft",
                "suspicious", "robbed",
                "injury", "panic", "stalked", "collision", "crash",
                "missing", "disoriented", "assaulted", "mugged" };
        for (String kw : elevatedKeywords) {
            if (desc.contains(kw)) {
                threatScore += 25;
                break;
            }
        }

        // Low-severity keywords: +10
        if (desc.contains("sick") || desc.contains("vehicle") || desc.contains("weather")) {
            threatScore += 10;
        }

        // --- Return Classification ---
        if (threatScore >= 50) {
            return "CRITICAL";
        } else if (threatScore >= 25) {
            return "ELEVATED";
        } else {
            return "LOW";
        }
    }
}
