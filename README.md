Java UML Parser -

A parser which takes Java source code as input and provides a UML Class Diagram as the output.

Compile Instructions
Requirements:
* Java JDK version 1.8
The program expects following arguments:
1. Path:
* Full path of the folder which contains all the .java source files. The program picks only the .java files and ignores other files.
* Ex - "C:\path\to\folder_name"
2. Name of output file
* File name of the output png file. The file will be created at the same folder as Path given in second argument.
* Do not include extension along with the file name, the program will generate a PNG file.
* Ex – output
Example:- To generate class diagram
java -jar umlparser.jar class "C:\path\to\folder_name" class-diagram-output
The above command will create a diagram with the following path and file name:
"C:\path\to\folder_name\class-diagram-output.png"

Details of libraries and tools used
* Eclipse IDE: 
Used to write, compile, and test project code. The executable jar file can be conveniently tested using Eclipse IDE at all stages of creation, compilation and debug.

* Apache Maven: 
It is a build automation tool. There are two ways in which Maven can be used to build software. One aspect is that Maven can be used to describe how software is built and the second is that it also describes what the dependencies are. Maven has a central repository in which all its components and dependencies are published. These can be downloaded easily when needed.
* Javaparser:
Jar File: Javaparsercore2.1.0.jar
This code is used to parse Java Source Code. Like Maven Central, it contains project binaries. Maven Project has been created, and dependencies of this tool have been added to POM.xml file
* PlantUML:
http://plantuml.com/
Jar File : plantuml.jar(8031)
Java code parsed using javaparser is used as an input for plantuml to generate UML Class Diagram. Graphviz software is required to use this tool and generate diagrams. Graphviz must be installed in the default directory of your system. 

