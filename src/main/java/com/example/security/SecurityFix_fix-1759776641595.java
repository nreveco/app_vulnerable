
// Log4j Security Fix
// Update Log4j to version 2.17.1 or later
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.17.1</version>
</dependency>
// Set system property to disable JNDI lookups
System.setProperty("log4j2.formatMsgNoLookups", "true");
            