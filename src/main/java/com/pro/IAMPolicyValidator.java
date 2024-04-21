package com.pro;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.regex.Pattern;

public class IAMPolicyValidator {

    public boolean validatePolicyResource(JsonNode rootNode) throws PolicyFormatException {

        //checking the correctness of input data
        boolean PolicyFormat = isIAMRolePolicyFormat(rootNode);
        if (!PolicyFormat) {
            throw new PolicyFormatException("Policy format is incorrect");
        }
        boolean DocumentFormat = isValidPolicyDocument(rootNode);
        if (!DocumentFormat) {
            throw new PolicyFormatException("Document format is incorrect");
        }


        JsonNode policyDocument = rootNode.path("PolicyDocument");
        JsonNode statements = policyDocument.path("Statement");

        for (JsonNode statement : statements) {
            JsonNode resource = statement.path("Resource");
            if (resource.isTextual() && resource.textValue().equals("*")) {
                return false; // Single star found
            }
        }
        return true; // All resources are correct
    }

    protected static boolean isIAMRolePolicyFormat(JsonNode rootNode) {

        // Checking that the PolicyName and PolicyDocument keys exist and are unique
        if (rootNode.has("PolicyName") && rootNode.has("PolicyDocument")) {
            JsonNode policyName = rootNode.get("PolicyName");
            JsonNode policyDocument = rootNode.get("PolicyDocument");

            // Checking for keys other than PolicyName and PolicyDocument
            Iterator<String> fieldNames = rootNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                if (!fieldName.equals("PolicyName") && !fieldName.equals("PolicyDocument")) {
                    return false;
                }
            }

            // Validation PolicyName
            if (policyName.isTextual()) {
                Pattern pattern = Pattern.compile("[\\w+=,.@-]{1,128}");
                if (!pattern.matcher(policyName.textValue()).matches()) {
                    return false;
                }
            } else {
                return false; // PolicyName is not String
            }

            // Checking whether the Policy Document is not empty
            if (!policyDocument.isObject() || policyDocument.isEmpty()) {
                return false;
            }

            return true;
        }
        return false;
    }

    protected static boolean isValidPolicyDocument(JsonNode rootNode) {

        JsonNode policyDocumentNode = rootNode.get("PolicyDocument");

        if (policyDocumentNode == null || !policyDocumentNode.isObject()) {
            return false;
        }

        JsonNode versionNode = policyDocumentNode.get("Version");
        JsonNode statementNode = policyDocumentNode.get("Statement");

        if (versionNode == null || !versionNode.isTextual() ||
                statementNode == null || !statementNode.isArray()) {
            return false;
        }

        Iterator<JsonNode> statements = statementNode.elements();
        while (statements.hasNext()) {
            JsonNode statement = statements.next();
            if (!isValidStatement(statement)) {
                return false;
            }
        }
        return true;
    }

    protected static boolean isValidStatement(JsonNode statement) {
        if (statement == null || !statement.isObject()) {
            return false;
        }

        JsonNode effectNode = statement.get("Effect");
        JsonNode actionNode = statement.get("Action");
        JsonNode resourceNode = statement.get("Resource");

        return effectNode != null && effectNode.isTextual() &&
                actionNode != null && (actionNode.isTextual() || actionNode.isArray()) &&
                resourceNode != null && (resourceNode.isTextual() || resourceNode.isArray());
    }
}
