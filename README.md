
java -jar wildfly_home\modules\system\layers\base\com\h2database\h2\main -webAllowOthers -tcpAllowOthers

### Hibernate latest
 * copy hibernate to wildfly server
```
mvn dependency:unpack
```