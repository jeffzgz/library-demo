# library-demo

To start a Spring Boot project called `library-demo` using Maven and then integrate OpenAPI to generate skeleton code for your API definitions on your Macbook terminal, you can follow these steps:

### Step 1: Set up
Install Java and Maven using Homebrew:

```bash
brew install openjdk@11
brew install maven
```

You might need to add Java to your path or set `JAVA_HOME`. For Java installed via Homebrew, you typically add the following to your shell configuration file (like `.bash_profile` or `.zshrc`):

```bash
export JAVA_HOME=/usr/local/opt/openjdk@11
export PATH=$JAVA_HOME/bin:$PATH
```

Install the Spring Boot CLI with Homebrew if you haven't yet:

```bash
# Optionally tapping the spring-io tap in brew
# brew tap spring-io/tap
brew install spring-boot
```

### Step 2: Create a New Spring Boot Project
Use the Spring Initializr via command line by using Spring Boot’s CLI or by generating the project manually through Maven:

#### Using Spring Boot CLI

Generate your project:

```bash
# navigate into the project directory
cd library-demo
spring init --name=library-demo --dependencies=web --build=maven --extract .
```

### Step 3: Add Spring Boot Dependencies
Edit the `pom.xml` to add Spring Boot parent POM and dependencies such as Spring Boot Starter Web. Here’s an example snippet to include in your `pom.xml`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.0</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- Include Springdoc OpenAPI for OpenAPI support -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-ui</artifactId>
        <version>1.6.9</version>
    </dependency>
</dependencies>
```

### Step 4: Configure OpenAPI
After adding Springdoc OpenAPI, you can start defining your OpenAPI configurations. Add a configuration class in your Spring Boot application to set up OpenAPI details:

```java
package com.example.librarydemo;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("library-demo")
            .pathsToMatch("/**")
            .build();
    }
}
```

In your application.properties, you can specify the API info:
```
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### Step 5: Quick Test
Now you can add an example controller, for example:

```package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ExampleController {

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
}
```

and run the service through:

```
mvn clean spring-boot:run
```

you can check the Swagger UI at the following URL to see if your API documentation is now correctly displayed:

```
http://localhost:8080/swagger-ui.html
```

### Step 6: Generate APIs through OpenAPI
Add the following plugin to pom.xml:
```
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>6.0.1</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/api.yml</inputSpec>
                <generatorName>spring</generatorName>
                <output>${project.basedir}/generated</output>
                <apiPackage>com.example.demogenerated.api</apiPackage>
                <modelPackage>com.example.demogenerated.model</modelPackage>
                <generateApiTests>false</generateApiTests>
                <generateModelTests>false</generateModelTests>
                <generateApiDocumentation>false</generateApiDocumentation>
                <generateModelDocumentation>false</generateModelDocumentation>
                <configOptions>
                    <interfaceOnly>true</interfaceOnly>
                    <delegatePattern>true</delegatePattern>
                </configOptions>
                <supportingFilesToGenerate>ApiUtil.java</supportingFilesToGenerate>
            </configuration>
        </execution>
    </executions>
</plugin>

<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <version>3.2.0</version>
    <executions>
        <execution>
            <id>add-source</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>${project.basedir}/generated/src/main/java</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This adds a step to generate the APIs and models for our service from an `api.yml` file to a `generated` folder. This is executed at maven's `generate` step.

Also add the following plugin:
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-clean-plugin</artifactId>
    <version>3.1.0</version> <!-- Ensure you are using a recent version -->
    <configuration>
        <filesets>
            <fileset>
                <directory>generated</directory>
                <includes>
                    <include>**/*.java</include>
                </includes>
                <followSymlinks>false</followSymlinks>
            </fileset>
        </filesets>
    </configuration>
</plugin>
```

This enables maven's clean step to remove the generated java files generated by the above plugin.

### Step 7: Run app
Run this to generate the latest APIs and models and run the app.
```
mvn clean spring-boot:run
```

Again, swagger UI can be accessed at
```
http://localhost:8080/swagger-ui/index.html
```
