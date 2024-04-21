# Remitly_Dawid_Nieradka

Technology:

- Java 21
- Maven

How to use:

1.  Download the project in the selected environment(recommended intellij idea)(The project requires Java 21 and Maven)
1.1 Run the main class to get the output for the example given in the command.
1.2 If you want to use other json files, provide their path on line 14(JsonNode rootNode = mapper.readTree(new File("src\\main\\resources\\package.json"));)
1.3 The tests I created are located in the path: src/test/java/IAMPolicyValidatorTest.
    
2.  Download the jar file "Dawid_Nieradka_java_fat_jar.jar" and include it as a library in your existing project.
2.1 Create an object IAMPolicyValidator (IAMPolicyValidator validator = new IAMPolicyValidator();
2.2  transferring data to a JsonNode object np. ObjectMapper mapper = new ObjectMapper();
                                                JsonNode rootNode = mapper.readTree(new File("src\\main\\resources\\package.json"));// provide the path to                                                                                                                                          the selected json file
2.3 Use method validatePolicyResource (boolean isValid = validator.validatePolicyResource(rootNode);)
2.4 example method usage:
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

    };
    
3.1 Use maven commands in terminal(The project requires Java 21 and Maven)
3.2 java -jar Dawid_Nieradka_java_fat_jar.jar // running the main class
3.3 mvn test // running tests





