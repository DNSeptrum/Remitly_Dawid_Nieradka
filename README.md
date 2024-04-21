# Remitly_Dawid_Nieradka

## Technology:
- Java 21
- Maven

## How to use:

### Setup and Running the Project:
1. Download the project in the selected environment (recommended IntelliJ IDEA). The project requires Java 21 and Maven.
2. To run the main class and see the output for the example given in the command:
   - Run the main class directly through your IDE or use the command line interface.
3. If you want to use other JSON files, provide their path on line 14:
   ```java
   JsonNode rootNode = mapper.readTree(new File("src\\main\\resources\\package.json"));
4. The tests I created are located in the path: src/test/java/IAMPolicyValidatorTest.

### Using the Jar File:
1. Download the jar file Dawid_Nieradka_java_fat_jar.jar and include it as a library in your existing project.
2. Create an instance of IAMPolicyValidator:
    ```java
   IAMPolicyValidator validator = new IAMPolicyValidator();
3. Transfer data to a JsonNode object:
    ```java
   ObjectMapper mapper = new ObjectMapper();
   JsonNode rootNode = mapper.readTree(new File("src\\main\\resources\\package.json")); // provide the path to the selected json file
4. Use the validatePolicyResource method:
   ```java
   boolean isValid = validator.validatePolicyResource(rootNode);
6. Example method usage:
   ```java
    public void example() throws IOException, PolicyFormatException {
        IAMPolicyValidator validator = new IAMPolicyValidator();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File("src\\main\\resources\\package.json"));

        boolean isValid = validator.validatePolicyResource(rootNode);
        if (isValid) {
            System.out.println("The 'Resource' field does not contain a single asterisk.");
        } else {
            System.out.println("The 'Resource' field does contain a single asterisk.");
        }
    }

### Use Maven commands in the terminal (the project requires Java 21 and Maven):
    
1. To run the main class:
   java -jar Dawid_Nieradka_java_fat_jar.jar
2. To run tests:
   mvn test





