### Java EE7 Stack using Groovy
* Groovy 2.4.7
* Apache Deltaspike 1.7
* JSF 2.2
* Primefaces 6.0
* hibernate 5.2.2
* Spring-data-jpa 4.2.8
* prettyfaces 3.4.1
* Wildfly 10.1

### Features
* Compiles with groovy lang
* Primefaces UI
* modified primefaces datagrid component to use request based pagination and sorting
* automatic scaffolding using spring data pagination and crud
* request based views
* type checked routes
* pretty URLs

### Hibernate use latest version on wildfly
 * copy hibernate to wildfly server
```
mvn dependency:unpack
```