# FTA and FMEA tool backend

## Ontology Vocabulary

Vocabulary is generated by maven plugin located in `ontology-generator` subproject.
When ontology definition is changed, the sources need to be regenerated. 
For more details see [README.md](ontology-generator/README.md)

## Persistence

The data are mapped from Java objects to RDF entities via ontological mapping library JOPA and stored in a local GraphDB database.

The database URL needs to be configured in `application.yml`. The repository first needs to be created.  
