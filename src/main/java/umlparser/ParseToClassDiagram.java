package umlparser;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

import net.sourceforge.plantuml.SourceStringReader;

public class ParseToClassDiagram {

	HashMap<String, ClassOrInterfaceDeclaration> classMap = new HashMap<String, ClassOrInterfaceDeclaration>();
    StringBuilder classDiagramStringInput = new StringBuilder();
    StringBuilder relationshipString = new StringBuilder();
    ClassOrInterfaceDeclaration currentClassOrInterfaceDeclaration = null;
    Map<String, UmlRelationship> associationRelationshipMap = new HashMap<String, UmlRelationship>();
    Map<String, UmlRelationship> dependencyRelationshipMap = new HashMap<String, UmlRelationship>();
    Map<String, UmlRelationship> relationshipMap = new HashMap<String, UmlRelationship>();
    List<String> listOfVariableNames = new ArrayList<String>();
    ArrayList<CompilationUnit> listOfCompilationUnits = new ArrayList<CompilationUnit>();
    
    public void umlParser(String[] args) throws Exception {
        
        File files = new File(args[0]);
        getCompilationUnits(files);
        
        classDiagramStringInput.append("@startuml\n");
        for (Map.Entry<String, ClassOrInterfaceDeclaration> classOrInterfaceDeclaration : classMap.entrySet()) {
        	currentClassOrInterfaceDeclaration = classOrInterfaceDeclaration.getValue();
        	if(!(classOrInterfaceDeclaration.getValue().isInterface())) {
        		classDiagramStringInput.append("class").append(" ").append(classOrInterfaceDeclaration.getValue().getName()).append("{").append("\n");
        		
        		for(Node childNode : classOrInterfaceDeclaration.getValue().getChildrenNodes()) {
        			if(childNode instanceof FieldDeclaration) {
        				if(((FieldDeclaration) childNode).getModifiers() == ModifierSet.PUBLIC ||
        						((FieldDeclaration) childNode).getModifiers() == ModifierSet.PRIVATE) {
        					buildFieldDeclaration(childNode);
        				} else {
        					Type childNodeType = ((FieldDeclaration) childNode).getType();
        					if(childNodeType instanceof ReferenceType) {
        						Type childNodeSubType = ((ReferenceType) childNodeType).getType();
        						if(childNodeSubType instanceof ClassOrInterfaceType) {
        							if(((ClassOrInterfaceType) childNodeSubType).getTypeArgs() != null) {
        								Type referenceName = ((ClassOrInterfaceType) childNodeSubType).getTypeArgs().get(0);
        								if(referenceName instanceof ReferenceType) {
        									if(classMap.containsKey(referenceName.toString())) {
        										buildRelationshipMap(((ClassOrInterfaceType)childNodeSubType).getTypeArgs().get(0).toString(), "*");
        									}
        								}
        							} else {
        								if(((ReferenceType)childNodeType).getArrayCount() > 0) {
        									if(classMap.containsKey(((ClassOrInterfaceType) childNodeSubType).toString())) {
        										buildRelationshipMap(((ClassOrInterfaceType)childNodeSubType).getName(), "*");
        									}
        								} else {
        									if(classMap.containsKey(((ClassOrInterfaceType) childNodeSubType).toString())) {
        										buildRelationshipMap(((ClassOrInterfaceType)childNodeSubType).getName(), "1");
        									}
        								}
        							}
        						}
        					}
        				}
        			} else if(childNode instanceof MethodDeclaration) {
        				if(((MethodDeclaration) childNode).getModifiers() == ModifierSet.PUBLIC ||
        						isPublicStatic(((MethodDeclaration) childNode).getModifiers()) || isPublicAbstract(((MethodDeclaration) childNode).getModifiers())) {
        					buildMethodDeclaration(childNode);
        				}
        			} else if(childNode instanceof ConstructorDeclaration) {
        				if(((ConstructorDeclaration) childNode).getModifiers() == ModifierSet.PUBLIC) {
        					buildConstructorDeclaration(childNode);
        				}
        			}
        		}		
        	} else {
        		classDiagramStringInput.append("interface").append(" ").append(classOrInterfaceDeclaration.getValue().getName()).append("{").append("\n");
        		for(Node childNode : classOrInterfaceDeclaration.getValue().getChildrenNodes()) {
        			if(childNode instanceof FieldDeclaration) {
        				if(((FieldDeclaration) childNode).getModifiers() == ModifierSet.PUBLIC ||
        						((FieldDeclaration) childNode).getModifiers() == ModifierSet.PRIVATE) {
        					buildFieldDeclaration(childNode);
        				} else {
        					Type childNodeType = ((FieldDeclaration) childNode).getType();
        					if(childNodeType instanceof ReferenceType) {
        						Type childNodeSubType = ((ReferenceType) childNodeType).getType();
        						if(childNodeSubType instanceof ClassOrInterfaceType) {
        							if(((ClassOrInterfaceType) childNodeSubType).getTypeArgs() != null) {
        								Type referenceName = ((ClassOrInterfaceType) childNodeSubType).getTypeArgs().get(0);
        								if(referenceName instanceof ReferenceType) {
        									if(classMap.containsKey(referenceName.toString())) {
        										buildRelationshipMap(((ClassOrInterfaceType)childNodeSubType).getTypeArgs().get(0).toString(), "*");
        									}
        								}
        							} else {
        								if(((ReferenceType)childNodeType).getArrayCount() > 0) {
        									if(classMap.containsKey(((ClassOrInterfaceType) childNodeSubType).toString())) {
        										buildRelationshipMap(((ClassOrInterfaceType)childNodeSubType).getName(), "*");
        									}
        								} else {
        									if(classMap.containsKey(((ClassOrInterfaceType) childNodeSubType).toString())) {
        										buildRelationshipMap(((ClassOrInterfaceType)childNodeSubType).getName(), "1");
        									}
        								}
        							}
        						}
        					}
        				}
        			} else if(childNode instanceof MethodDeclaration) {
        				if(((MethodDeclaration) childNode).getModifiers() == ModifierSet.PUBLIC ||
        						isPublicStatic(((MethodDeclaration) childNode).getModifiers()) || isPublicAbstract(((MethodDeclaration) childNode).getModifiers())) {
        					buildMethodDeclaration(childNode);
        				}
        			}
        		}
        	}
        	classDiagramStringInput.append("} \n");
        	
        	List<ClassOrInterfaceType> listOfExtendedClasses = currentClassOrInterfaceDeclaration.getExtends();
        	if(listOfExtendedClasses != null) {
        		for (ClassOrInterfaceType c : listOfExtendedClasses) {
            		if(classMap.containsKey(c.getName())) {
            			String relationKey = c.getName() + "_" + currentClassOrInterfaceDeclaration.getName();
            			relationshipMap.put(relationKey, new UmlRelationship(classMap.get(c.getName()),
                                "",
                                currentClassOrInterfaceDeclaration,
                                "",
                                UmlRelationShipType.EX));
            		}
            	}
        	}
        	
        	List<ClassOrInterfaceType> listOfImplementedClasses = currentClassOrInterfaceDeclaration.getImplements();
        	if(listOfImplementedClasses != null) {
        		for (ClassOrInterfaceType cd : listOfImplementedClasses) {
            		if(classMap.containsKey(cd.getName())) {
            			String relationKey = cd.getName() + "_" + currentClassOrInterfaceDeclaration.getName();
            			relationshipMap.put(relationKey, new UmlRelationship(classMap.get(cd.getName()),
                                "",
                                currentClassOrInterfaceDeclaration,
                                "",
                                UmlRelationShipType.IM));
            		}
            	}
        	}
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
		ImageIO.write(img, "png", new File(args[0]+"/"+args[1]+".png"));
	}
	
	private void getCompilationUnits(File files) throws Exception {
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
	}
	
	private void buildRelationShipString() {
        for (Map.Entry<String, UmlRelationship> entry : associationRelationshipMap.entrySet()) {
        	printAllRelationshipMaps(entry);
        }
        for (Map.Entry<String, UmlRelationship> entry : dependencyRelationshipMap.entrySet()) {
        	printAllRelationshipMaps(entry);
        }
        for (Map.Entry<String, UmlRelationship> entry : relationshipMap.entrySet()) {
        	printAllRelationshipMaps(entry);
        }
    }
	
	private void printAllRelationshipMaps(Entry<String, UmlRelationship> entry) {

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
	
	private void buildStringForPimitiveTypeMemberVariables(FieldDeclaration primitiveType, StringBuilder classDiagramStringInput) {
		classDiagramStringInput.append(getModifier(primitiveType.getModifiers()));
		classDiagramStringInput.append(" ").append(primitiveType.getType().toString());
		classDiagramStringInput.append(" : ").append(primitiveType.getVariables().get(0));
		classDiagramStringInput.append("\n");
		listOfVariableNames.add(primitiveType.getVariables().get(0).toString());
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
        if (associationRelationshipMap.containsKey(relationKey)) {
            UmlRelationship umlRelationship = associationRelationshipMap.get(relationKey);
            umlRelationship.setCurrentClassOrInterfaceDeclarationMultiplicity(multiplicity);
        } else {
        	associationRelationshipMap.put(relationKey, new UmlRelationship(currentClassOrInterfaceDeclaration,
                    "",
                    relatedClassOrInterfaceDeclaration,
                    multiplicity,
                    UmlRelationShipType.AS));
        }
    }
	
	private void buildRelationshipMap(String className) {
        ClassOrInterfaceDeclaration relatedClassOrInterfaceDeclaration = classMap.get(className);
        String relationKey = getRelationKey(currentClassOrInterfaceDeclaration.getName(), relatedClassOrInterfaceDeclaration.getName());
        if(currentClassOrInterfaceDeclaration.isInterface() && relatedClassOrInterfaceDeclaration.isInterface()) {
        	return;
        }
        if(!dependencyRelationshipMap.containsKey(relationKey) && relatedClassOrInterfaceDeclaration.isInterface()){
        	dependencyRelationshipMap.put(relationKey, new UmlRelationship(currentClassOrInterfaceDeclaration,
                    "",
                    relatedClassOrInterfaceDeclaration,
                    "",
                    UmlRelationShipType.DEP));
        }
    }

    private String getRelationKey(String name1, String name2) {
        if (name1.compareTo(name2) < 0) {
            return name1 + "_" + name2;
        }
        return name2 + "_" + name1;
    }
    
    private void buildFieldDeclaration (Node childNode) {

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
	
    }
    
    private void buildMethodDeclaration (Node childNode) {

		MethodDeclaration md = (MethodDeclaration)childNode;
		if(md.getModifiers() == ModifierSet.PUBLIC || isPublicStatic(md.getModifiers()) || isPublicAbstract(md.getModifiers())) {
			int count = 0;
			for (String s : listOfVariableNames) {
				if(md.getName().equalsIgnoreCase(("get".concat(s))) || md.getName().equalsIgnoreCase(("set".concat(s)))) {
					return;
				}
			}
			classDiagramStringInput.append(getModifier(md.getModifiers())).append(md.getName()).append("(");
			if(md.getParameters() != null) {
				for (Parameter p : md.getParameters()) {
					Type childNodeType = p.getType();
					classDiagramStringInput.append(p.getId()).append(":").append(p.getType().toString());
					if(childNodeType instanceof ReferenceType) {
						Type childNodeSubType = ((ReferenceType)childNodeType).getType();
	                    if (childNodeSubType instanceof ClassOrInterfaceType) {
	                        if(((ClassOrInterfaceType) childNodeSubType).getTypeArgs() != null) {
	                        	List<Type> type = ((ClassOrInterfaceType) childNodeSubType).getTypeArgs();
	                        	for (Type t : type) {
	                        		if(this.classMap.containsKey(t.toString())) {
	                        			buildRelationshipMap(t.toString());
	                        		}
	                        	}
	                        } else {
	                        	if(this.classMap.containsKey(childNodeSubType.toString())) {
                        			buildRelationshipMap(childNodeSubType.toString());
                        		}
	                        }
	                    }
					}
					classDiagramStringInput.append("");
					count++;
					if (count > 0) {
						classDiagramStringInput.append(", ");
	                }
				}
			}
			classDiagramStringInput.append(") : ").append(md.getType()).append("\n");
			if(md.getBody() != null) {
				BlockStmt b = md.getBody();
				if(b.getStmts() != null) {
					for (Statement s : b.getStmts()) {
						if(s instanceof ExpressionStmt && ((ExpressionStmt)s).getExpression() != null) {
							if(((ExpressionStmt)s).getExpression() instanceof VariableDeclarationExpr) {
								Type t = ((VariableDeclarationExpr)((ExpressionStmt)s).getExpression()).getType();
								if(t instanceof ReferenceType) {
									if(((ReferenceType) t).getType() instanceof ClassOrInterfaceType) {
										if(((ClassOrInterfaceType)(((ReferenceType)t).getType())).getTypeArgs() != null) {
											if(classMap.containsKey(((ClassOrInterfaceType)(((ReferenceType)t).getType())).getTypeArgs().get(0).toString())) {
												String className = ((ClassOrInterfaceType)(((ReferenceType)t).getType())).getTypeArgs().get(0).toString();
												ClassOrInterfaceDeclaration relatedClassOrInterfaceDeclaration = classMap.get(className);
										        String relationKey = getRelationKey(currentClassOrInterfaceDeclaration.getName(), relatedClassOrInterfaceDeclaration.getName());
										        if(currentClassOrInterfaceDeclaration.isInterface() && relatedClassOrInterfaceDeclaration.isInterface()) {
										        	return;
										        }
										        if(!dependencyRelationshipMap.containsKey(relationKey) && relatedClassOrInterfaceDeclaration.isInterface()) {
										        	dependencyRelationshipMap.put(relationKey, new UmlRelationship(currentClassOrInterfaceDeclaration,
										                    "",
										                    relatedClassOrInterfaceDeclaration,
										                    "",
										                    UmlRelationShipType.DEP));
										        }
											}
										} else {
											if(classMap.containsKey(((ClassOrInterfaceType)(((ReferenceType)t).getType())).getName())) {
												String className = ((ClassOrInterfaceType)(((ReferenceType)t).getType())).getName();
												ClassOrInterfaceDeclaration relatedClassOrInterfaceDeclaration = classMap.get(className);
										        String relationKey = getRelationKey(currentClassOrInterfaceDeclaration.getName(), relatedClassOrInterfaceDeclaration.getName());
										        if(currentClassOrInterfaceDeclaration.isInterface() && relatedClassOrInterfaceDeclaration.isInterface()) {
										        	return;
										        }
										        if(!dependencyRelationshipMap.containsKey(relationKey) && relatedClassOrInterfaceDeclaration.isInterface()) {
										        	dependencyRelationshipMap.put(relationKey, new UmlRelationship(currentClassOrInterfaceDeclaration,
										                    "",
										                    relatedClassOrInterfaceDeclaration,
										                    "",
										                    UmlRelationShipType.DEP));
										        }
											}
										}
									}
								} 
							}
						}
					}
				}
			} else {
				classDiagramStringInput.append("\n");
			}
		}	
    }
    
    private boolean isPublicStatic(int mod) {
        return (mod == 9);
    }
    
    private boolean isPublicAbstract(int mod) {
        return (mod == 1025);
    }
    
    public void buildConstructorDeclaration(Node childNode) {
		ConstructorDeclaration md = (ConstructorDeclaration)childNode;
		if(md.getModifiers() == ModifierSet.PUBLIC) {
			int count = 0;
			classDiagramStringInput.append(getModifier(md.getModifiers())).append(md.getName()).append("(");
			if(md.getParameters() != null) {
				for (Parameter p : md.getParameters()) {
					Type childNodeType = p.getType();
					classDiagramStringInput.append(p.getId()).append(":").append(p.getType().toString());
					if(childNodeType instanceof ReferenceType) {
						Type childNodeSubType = ((ReferenceType)childNodeType).getType();
	                    if (childNodeSubType instanceof ClassOrInterfaceType) {
	                        if(((ClassOrInterfaceType) childNodeSubType).getTypeArgs() != null) {
	                        	List<Type> type = ((ClassOrInterfaceType) childNodeSubType).getTypeArgs();
	                        	for (Type t : type) {
	                        		if(this.classMap.containsKey(t.toString())) {
	                        			buildRelationshipMap(t.toString());
	                        		}
	                        	}
	                        } else {
	                        	if(this.classMap.containsKey(childNodeSubType.toString())) {
                        			buildRelationshipMap(childNodeSubType.toString());
                        		}
	                        }
	                    }
					}
					classDiagramStringInput.append("");
					count++;
					if (count > 0) {
						classDiagramStringInput.append(", ");
	                }
				}
			}
			classDiagramStringInput.append(")").append("\n");
		}
    }
}
