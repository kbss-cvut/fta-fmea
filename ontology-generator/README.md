# Project Description

This project creates java source files based on the application ontology.

### Project structure
- ./ontology - ontology folder
- ./src/main/resource/mapping - input mapping file used by the jopa-maven-plugin. The file maps ontology IRIs to files 
in the ./ontology folder.


### Naming conventions
The URIs in the ontology should follow the following naming conventions:
- concepts contain only small letters, with words separated by `-` (even classes)
- object properties start with `has-` or `is-`. 
- datatype properties typically do not start with `:has-`