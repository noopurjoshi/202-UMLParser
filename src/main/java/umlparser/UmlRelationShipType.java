package umlparser;

public enum UmlRelationShipType {
    EX("<|--"),
    IM("<|.."),
    AS("--"),
    DEP("..>"),
    LOLI("()--");

    private String relationshipType;
    UmlRelationShipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
    public String getRelationshipType() {
        return relationshipType;
    }
}
