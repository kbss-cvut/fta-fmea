# Project Description

This project generates the `Vocabulary.java` source file based on the ontology specified in the mapping file. 
For more details see configuration in [pom.xml](pom.xml)  

## Project structure
- ./ontology - folder containing the ontology files
- ./src/main/resource/mapping - input mapping file used by the jopa-maven-plugin. The file maps ontology IRIs to files 
in the ./ontology folder.


## Naming conventions
### RDF resources
- resources should be lower-case with words separated by "-"
- IRI fragment should be separated by "/" instead of "#"
- ontology IRI should start with http://onto.fel.cvut.cz/ontologies/
- resource IRI cannot end with "/" or "#" at the end of the IRI (applies also to ontology IRI)

### OWL resources
- object property should be named with "has-" and "is-" prefix
- datatype property should be named without "has-" and "is-" prefix
