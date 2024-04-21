package com.pro;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, PolicyFormatException {

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
}
