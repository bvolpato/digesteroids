digesteroids
========

[![Apache License](http://img.shields.io/badge/license-ASL-blue.svg)](https://github.com/brunocvcunha/digesteroids/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/brunocvcunha/digesteroids.svg)](https://travis-ci.org/brunocvcunha/digesteroids)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.brunocvcunha.digesteroids/digesteroids/badge.svg)](https://maven-badges.herokuapp.com/maven-central/digesteroids/digesteroids)
[![Coverage Status](https://coveralls.io/repos/github/brunocvcunha/digesteroids/badge.svg?branch=master)](https://coveralls.io/github/brunocvcunha/digesteroids?branch=master)

:pill: Digester on Steroids.

Map data to your POJOs in a beautiful way. Supports mapping from multiple sources:
  1. HTML (scraping), using XPath, ID or CSS selector.
  2. JSON Path
  3. Maps
  4. Property files

Download
--------

Download [the latest JAR][1] or grab via Maven:
```xml
<dependency>
  <groupId>org.brunocvcunha.digesteroids</groupId>
  <artifactId>digesteroids</artifactId>
  <version>0.1</version>
</dependency>
```
or Gradle:
```groovy
compile 'org.brunocvcunha.digesteroids:digesteroids:0.1'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

Digesteroids requires at minimum Java 8.





Examples
--------


### Convert a HTML into a POJO, based on the IDs, CSS Selector or XPath

example.html
```html
<html>
	<head>
	   <title>Digesteroids Test</title>
	</head>
	<body>
        <p id="fname">Bruno Candido Volpato da Cunha</p>
        <p id="age">24</p>
        <p class="address1">65535 University Ave</p>
        <p><span>Palo Alto</span></p>
	</body>
</html>
```

PersonPOJO.java
```java
@DigesterEntity
public class PersonPOJO {
  
  @DigesterMapping(value = "fullName")
  @DigesterMapping(source = "htmlExample", refType = ReferenceTypeEnum.HTML_ID, value = "fname")
  private String name;
  
  @DigesterMapping(value = "age")
  @DigesterMapping(source = "htmlExample", refType = ReferenceTypeEnum.HTML_ID, value = "age")
  private Integer age;
  
  @DigesterMapping(value = "personAddress")
  @DigesterMapping(source = "htmlExample", refType = ReferenceTypeEnum.PASS_THROUGH, value = "")
  private AddressPOJO address;
  
}

@DigesterEntity
public class AddressPOJO {
  @DigesterMapping(value = "address")
  @DigesterMapping(source = "htmlExample", refType = ReferenceTypeEnum.HTML_CSS, value = "p.address1")
  private String address1;
  
  @DigesterMapping(value = "addressCity")
  @DigesterMapping(source = "htmlExample", refType = ReferenceTypeEnum.HTML_XPATH, value = "p > span")
  private String city;
  
}
```


Usage
```java
Digesteroids digister = new Digesteroids();

PersonPOJO person = digister.convertHTMLToType("htmlExample", getClass().getResourceAsStream("/example.html"), PersonPOJO.class);

assertEquals("Bruno Candido Volpato da Cunha", person.getName());
assertEquals("65535 University Ave", person.getAddress().getAddress1());
assertEquals("Palo Alto", person.getAddress().getCity());
```




### Convert a Map or Json into a POJO, converting the names:

POJOs:
```java
@DigesterEntity
public class PersonPOJO {
  
  @DigesterMapping(value = "fullName")
  private String name;
  
  @DigesterMapping(value = "age")
  private Integer age;
  
  @DigesterMapping(value = "personAddress")
  private AddressPOJO address;
  
}

@DigesterEntity
public class AddressPOJO {
  @DigesterMapping(value = "address")
  private String address1;
  
  @DigesterMapping(value = "addressCity")
  private String city;
  
}
```


Usage (Map to POJO):
```java
Digesteroids digister = new Digesteroids();

Map<String, Object> personMap = new LinkedHashMap<>();
personMap.put("fullName", "Bruno");
personMap.put("age", "24");

Map<String, Object> addressMap = new LinkedHashMap<>();
addressMap.put("address", "65535 University Ave");
addressMap.put("addressCity", "Palo Alto");

personMap.put("personAddress", addressMap);

PersonPOJO person = digister.convertObjectToType(personMap, PersonPOJO.class);

assertEquals("Bruno", person.getName());
assertEquals("65535 University Ave", person.getAddress().getAddress1());
assertEquals("Palo Alto", person.getAddress().getCity());
```



 [1]: https://search.maven.org/remote_content?g=org.brunocvcunha.digesteroids&a=digesteroids&v=LATEST
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
