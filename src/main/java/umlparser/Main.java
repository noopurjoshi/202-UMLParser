package umlparser;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

import umlparser.UmlRelationShipType;
import umlparser.UmlRelationship;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class Main {
	
	HashMap<String, ClassOrInterfaceDeclaration> classMap = new HashMap<String, ClassOrInterfaceDeclaration>();
    StringBuilder classDiagramStringInput = new StringBuilder();
    StringBuilder relationshipString = new StringBuilder();
    ClassOrInterfaceDeclaration currentClassOrInterfaceDeclaration = null;
    Map<String, UmlRelationship> relationshipMap = new HashMap<String, UmlRelationship>();
    
    public static void main(String args[]) throws Exception {
    	Main main = new Main();
    	main.main2(args);
    }

	public void main2(String[] args) throws Exception {
		// TODO Auto-generated method stub
		args = new String[2];
        int suffix = 7;
        args[0] = "code/uml-parser-test-" + suffix;
        args[1] = "code/uml-parser-test-"+suffix+"/output" + suffix + ".png";
        
        
        
        File files = new File(args[0]);
        ArrayList<CompilationUnit> listOfCompilationUnits = new ArrayList<CompilationUnit>();
        for (final File file : files.listFiles()) {
            if (file.getName().endsWith(".java") && file.isFile()) {
                FileInputStream fileInputStream = new FileInputStream(file);                
                CompilationUnit compilationUnit;
                try {
                	compilationUnit = JavaParser.parse(fileInputStream);
                    listOfCompilationUnits.add(compilationUnit);
                } finally {
                	fileInputStream.close();
                }
            }
        }
        
        for (CompilationUnit compilationUnit : listOfCompilationUnits) {
            List<TypeDeclaration> listOfTypeDeclarations = compilationUnit.getTypes();
            for (Node typeDeclaration : listOfTypeDeclarations) {
                ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
                classMap.put(classOrInterfaceDeclaration.getName(), classOrInterfaceDeclaration);
            }
        }
        
        classDiagramStringInput.append("@startuml\n");
        for (Map.Entry<String, ClassOrInterfaceDeclaration> classOrInterfaceDeclaration : classMap.entrySet()) {
        	if(!(classOrInterfaceDeclaration.getValue().isInterface())) {
        		currentClassOrInterfaceDeclaration = classOrInterfaceDeclaration.getValue();
        		classDiagramStringInput.append("class").append(" ").append(classOrInterfaceDeclaration.getValue().getName()).append("{").append("\n");
        		
        		for(Node childNode : classOrInterfaceDeclaration.getValue().getChildrenNodes()) {
        			if(childNode instanceof FieldDeclaration) {
						Type childNodeType = ((FieldDeclaration) childNode).getType();
						if(childNodeType instanceof ReferenceType) {
							Type childNodeSubType = ((ReferenceType) childNodeType).getType();
							if(childNodeSubType instanceof ClassOrInterfaceType) {
								if(((ClassOrInterfaceType) childNodeSubType).getTypeArgs() != null) {
									Type referenceName = ((ClassOrInterfaceType) childNodeSubType).getTypeArgs().get(0);
									if(referenceName instanceof ReferenceType) {
										if(classMap.containsKey(referenceName.toString())) {
											buildRelationshipMap(((ClassOrInterfaceType)childNodeSubType).getTypeArgs().get(0).toString(), "*");
										} else {
											buildStringForPimitiveTypeMemberVariables((FieldDeclaration)childNode, classDiagramStringInput);
										}
									}
								} else {
									if(((ReferenceType)childNodeType).getArrayCount() > 0) {
										if(classMap.containsKey(((ClassOrInterfaceType) childNodeSubType).toString())) {
											buildRelationshipMap(((ClassOrInterfaceType)childNodeSubType).getName(), "*");
										} else {
											buildStringForPimitiveTypeMemberVariables((FieldDeclaration)childNode, classDiagramStringInput);
										}
									} else {
										if(classMap.containsKey(((ClassOrInterfaceType) childNodeSubType).toString())) {
											buildRelationshipMap(((ClassOrInterfaceType)childNodeSubType).getName(), "1");
										} else {
											buildStringForPimitiveTypeMemberVariables((FieldDeclaration)childNode, classDiagramStringInput);
										}
									}
								}
							} else {
								buildStringForPimitiveTypeMemberVariables((FieldDeclaration)childNode, classDiagramStringInput);
							}
						} else {
							buildStringForPimitiveTypeMemberVariables((FieldDeclaration)childNode, classDiagramStringInput);
						}
        			} else if(childNode instanceof MethodDeclaration) {
        				MethodDeclaration md = (MethodDeclaration)childNode;
        				if(md.getModifiers() == ModifierSet.PUBLIC) {
        					classDiagramStringInput.append(getModifier(md.getModifiers())).append(md.getName()).append("(");
        					if(md.getParameters() != null) {
        						for (Parameter p : md.getParameters()) {
        							if(p.getType() instanceof ReferenceType) {
        								if()
        							}
        							classDiagramStringInput.append("");
        						}
        					}
        					classDiagramStringInput.append(") : ").append(md.getType()).append("\n");
        				}
        				
        			} else if(childNode instanceof ConstructorDeclaration) {
        				
        			}
        		}		
        	} else {
        		classDiagramStringInput.append("interface").append(" ").append(classOrInterfaceDeclaration.getValue().getName()).append("\n");
        	}
        	classDiagramStringInput.append("} \n");        	
        }
        buildRelationShipString();
        classDiagramStringInput.append(relationshipString.toString()).append("@enduml\n");
        System.out.println("output: \n"+ classDiagramStringInput.toString());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
		SourceStringReader reader = new SourceStringReader(classDiagramStringInput.toString());
		String plantUmlResponse = reader.generateImage(stream);
		byte[] plantUmlResponseByteArray = stream.toByteArray();
		InputStream imageInput = new ByteArrayInputStream(plantUmlResponseByteArray);
		BufferedImage img = ImageIO.read(imageInput);
		ImageIO.write(img, "png", new File(args[1]));
	}
	
	private void buildRelationShipString() {
        for (Map.Entry<String, UmlRelationship> entry : relationshipMap.entrySet()) {
            UmlRelationship umlRelationship = entry.getValue();
            relationshipString.append(umlRelationship.getCurrentClassOrInterfaceDeclaration().getName()).append(" ");
            if (umlRelationship.getRelationshipType() == UmlRelationShipType.AS && umlRelationship.getCurrentClassOrInterfaceDeclarationMultiplicity().length() > 0) {
            	relationshipString.append("\"")
                        .append(umlRelationship.getCurrentClassOrInterfaceDeclarationMultiplicity())
                        .append("\"");

            }
            relationshipString.append(" ").append(umlRelationship.getRelationshipType().getRelationshipType()).append(" ");
            if (umlRelationship.getRelationshipType() == UmlRelationShipType.AS && umlRelationship.getRelatedClassOrInterfaceDeclarationMultiplicity().length() > 0) {

            	relationshipString.append("\"")
                        .append(umlRelationship.getRelatedClassOrInterfaceDeclarationMultiplicity())
                        .append("\"");
            }
            relationshipString.append(" ").append(umlRelationship.getRelatedClassOrInterfaceDeclaration().getName())
                    .append("\n");
        }
    }
	
	private void buildStringForPimitiveTypeMemberVariables(FieldDeclaration primitiveType, StringBuilder classDiagramStringInput) {
		classDiagramStringInput.append(getModifier(primitiveType.getModifiers()));
		classDiagramStringInput.append(" ").append(primitiveType.getType().toString());
		classDiagramStringInput.append(" : ").append(primitiveType.getVariables().get(0));
		classDiagramStringInput.append("\n");
    }
	
	public String getModifier(int mod) {
        if ((mod & ModifierSet.PUBLIC) != 0)        return "+";
        if ((mod & ModifierSet.PROTECTED) != 0)     return "#";
        if ((mod & ModifierSet.PRIVATE) != 0)       return "-";
        return "~";
    }
	
	private void buildRelationshipMap(String className, String multiplicity) {
        ClassOrInterfaceDeclaration relatedClassOrInterfaceDeclaration = classMap.get(className);
        String relationKey = getRelationKey(currentClassOrInterfaceDeclaration.getName(), relatedClassOrInterfaceDeclaration.getName());
        if (relationshipMap.containsKey(relationKey)) {
            UmlRelationship umlRelationship = relationshipMap.get(relationKey);
            umlRelationship.setCurrentClassOrInterfaceDeclarationMultiplicity(multiplicity);
        } else {
            relationshipMap.put(relationKey, new UmlRelationship(currentClassOrInterfaceDeclaration,
                    "",
                    relatedClassOrInterfaceDeclaration,
                    multiplicity,
                    UmlRelationShipType.AS));
        }
    }

    private String getRelationKey(String name1, String name2) {
        if (name1.compareTo(name2) < 0) {
            return name1 + "_" + name2;
        }
        return name2 + "_" + name1;
    }

}
