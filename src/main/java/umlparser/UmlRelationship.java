package umlparser;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class UmlRelationship {
    private ClassOrInterfaceDeclaration currentClassOrInterfaceDeclaration;
    private String currentClassOrInterfaceDeclarationMultiplicity;
    private ClassOrInterfaceDeclaration relatedClassOrInterfaceDeclaration;
    private String relatedClassOrInterfaceDeclarationMultiplicity;
    private UmlRelationShipType relationshipType;

    public UmlRelationship(ClassOrInterfaceDeclaration currentClassOrInterfaceDeclaration, String currentClassOrInterfaceDeclarationMultiplicity,
    		ClassOrInterfaceDeclaration relatedClassOrInterfaceDeclaration, String relatedClassOrInterfaceDeclarationMultiplicity,
    		UmlRelationShipType relationshipType) {
        this.currentClassOrInterfaceDeclaration = currentClassOrInterfaceDeclaration;
        this.currentClassOrInterfaceDeclarationMultiplicity = currentClassOrInterfaceDeclarationMultiplicity;
        this.relatedClassOrInterfaceDeclaration = relatedClassOrInterfaceDeclaration;
        this.relatedClassOrInterfaceDeclarationMultiplicity = relatedClassOrInterfaceDeclarationMultiplicity;
        this.relationshipType = relationshipType;
    }

    public ClassOrInterfaceDeclaration getCurrentClassOrInterfaceDeclaration() {
        return currentClassOrInterfaceDeclaration;
    }

    public void setCurrentClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration currentClassOrInterfaceDeclaration) {
        this.currentClassOrInterfaceDeclaration = currentClassOrInterfaceDeclaration;
    }

    public String getCurrentClassOrInterfaceDeclarationMultiplicity() {
        return currentClassOrInterfaceDeclarationMultiplicity;
    }

    public void setCurrentClassOrInterfaceDeclarationMultiplicity(String currentClassOrInterfaceDeclarationMultiplicity) {
        this.currentClassOrInterfaceDeclarationMultiplicity = currentClassOrInterfaceDeclarationMultiplicity;
    }

    public ClassOrInterfaceDeclaration getRelatedClassOrInterfaceDeclaration() {
        return relatedClassOrInterfaceDeclaration;
    }

    public void setRelatedClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration relatedClassOrInterfaceDeclaration) {
        this.relatedClassOrInterfaceDeclaration = relatedClassOrInterfaceDeclaration;
    }

    public String getRelatedClassOrInterfaceDeclarationMultiplicity() {
        return relatedClassOrInterfaceDeclarationMultiplicity;
    }

    public void setRelatedClassOrInterfaceDeclarationMultiplicity(String relatedClassOrInterfaceDeclarationMultiplicity) {
        this.relatedClassOrInterfaceDeclarationMultiplicity = relatedClassOrInterfaceDeclarationMultiplicity;
    }

    public UmlRelationShipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(UmlRelationShipType relationshipType) {
        this.relationshipType = relationshipType;
    }
}
