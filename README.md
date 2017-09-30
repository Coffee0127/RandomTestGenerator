# RandomTestGenerator

[![Build Status](https://travis-ci.org/Coffee0127/RandomTestGenerator.svg?branch=master)](https://travis-ci.org/Coffee0127/RandomTestGenerator)

## Installation
### The Front End
* `npm install` to install Node packages (or `yarn`)

### The Back End
* You'll need Maven 3+ and Java 8
    + The plugin `com.github.eirslett:frontend-maven-plugin:1.3` requires Maven version 3.1.0+

## Run
### The Fron End
* `npm start`

### The Back End
* `mvn spring-boot:run` to start spring boot

## Packaging
* **install Node packages** first!!
* change the pom.xml `frontend-maven-plugin installDirectory` to the NodeJS **parent** directory
* set up `base href` via maven property `app.context.name`
```xml
<plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <version>1.3</version>
    <executions>
        <execution>
            <id>ng build</id>
            <goals>
                <goal>npm</goal>
            </goals>
            <phase>generate-sources</phase>
            <configuration>
                <!-- plugin would execute C:/iCoding/node/nodejs -->
                <installDirectory>C:/iCoding</installDirectory>
                <workingDirectory>front/</workingDirectory>
                <arguments>run ng build -- --prod --base-href /${app.context.name}/</arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```
