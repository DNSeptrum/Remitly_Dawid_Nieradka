import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pro.IAMPolicyValidator;
import com.pro.PolicyFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IAMPolicyValidatorTest {

        private IAMPolicyValidator validator;
        private ObjectMapper mapper;

        @BeforeEach
        void setUp() {
            validator = new IAMPolicyValidator();
            mapper = new ObjectMapper();
        }


    @Test
    void testValidatePolicyResource_WithValidInput() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": \"iam:ListUsers\", \"Resource\": \"arn:aws:iam::123456789012:user/\"}]}}";
        JsonNode rootNode = mapper.readTree(json);
        assertTrue(validator.validatePolicyResource(rootNode));
    }

    @Test
    void testValidatePolicyResource_WithAsteriskAndSpaceInput() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": \"iam:ListUsers\", \"Resource\": \" * \"}]}}";
        JsonNode rootNode = mapper.readTree(json);
        assertTrue(validator.validatePolicyResource(rootNode));
    }

    @Test
    void testValidatePolicyResource_WithAsteriskInResource() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": \"iam:ListUsers\", \"Resource\": \"*\"}]}}";
        JsonNode rootNode = mapper.readTree(json);
        assertFalse(validator.validatePolicyResource(rootNode), "Resource with '*' should return false");
    }
    @Test
    void testValidatePolicyResource_WithEmptyInResource() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": \"iam:ListUsers\", \"Resource\": \"\"}]}}";
        JsonNode rootNode = mapper.readTree(json);
        assertTrue(validator.validatePolicyResource(rootNode), "Resource with '*' should return false");
    }

    @Test
    void testValidatePolicyResource_WithInvalidPolicyFormat() throws Exception {
        String json = "{\"PolicyName\": 123, \"PolicyDocument\": {}}";
        JsonNode rootNode = mapper.readTree(json);
        assertThrows(PolicyFormatException.class, () -> validator.validatePolicyResource(rootNode), "Should throw PolicyFormatException due to invalid PolicyName type");
    }

    @Test
    void testValidatePolicyResource_WithAdditionalKeys() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"ExtraKey\": \"value\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": \"iam:ListUsers\", \"Resource\": \"arn:aws:iam::123456789012:user/\"}]}}";
        JsonNode rootNode = mapper.readTree(json);
        assertThrows(PolicyFormatException.class, () -> validator.validatePolicyResource(rootNode), "Should throw PolicyFormatException due to additional keys");
    }

    @Test
    void testValidatePolicyResource_WithEmptyPolicyDocument() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {}}";
        JsonNode rootNode = mapper.readTree(json);
        assertThrows(PolicyFormatException.class, () -> validator.validatePolicyResource(rootNode), "Should throw PolicyFormatException due to empty PolicyDocument");
    }

    @Test
    void testValidatePolicyResource_InvalidPolicyNameType() throws Exception {
        String json = "{\"PolicyName\": 123, \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": []}}";
        JsonNode rootNode = mapper.readTree(json);
        assertThrows(PolicyFormatException.class, () -> validator.validatePolicyResource(rootNode),
                "PolicyName should be a string, expecting PolicyFormatException");
    }

    @Test
    void testValidatePolicyResource_ResourceTypeIsNotString() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": \"iam:ListUsers\", \"Resource\": [\"arn:aws:iam::123456789012:user/\"]}]}}";
        JsonNode rootNode = mapper.readTree(json);
        assertTrue(validator.validatePolicyResource(rootNode), "Resources passed as an array, not a string");
    }

    @Test
    void testValidatePolicyResource_MissingStatement() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {\"Version\": \"2012-10-17\"}}";
        JsonNode rootNode = mapper.readTree(json);
        assertThrows(PolicyFormatException.class, () -> validator.validatePolicyResource(rootNode),
                "Missing Statement in PolicyDocument, expecting PolicyFormatException");
    }

    @Test
    void testValidatePolicyResource_MultipleStatementsWithMixedResources() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": \"iam:ListUsers\", \"Resource\": \"arn:aws:iam::123456789012:user/\"}, {\"Effect\": \"Deny\", \"Action\": \"iam:DeleteUser\", \"Resource\": \"*\"}]}}";
        JsonNode rootNode = mapper.readTree(json);
        assertFalse(validator.validatePolicyResource(rootNode), "One of the Resources is '*', expecting return false");
    }

    @Test
    void testValidatePolicyResource_ExtremelyLongResourceString() throws Exception {
        String longResource = "arn:aws:iam::123456789012:user/ * " + "x".repeat(10000);
        String json = String.format("{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": \"iam:ListUsers\", \"Resource\": \"%s\"}]}}", longResource);
        JsonNode rootNode = mapper.readTree(json);
        assertTrue(validator.validatePolicyResource(rootNode), "Extremely long resource string, expecting return true if it's valid");
    }

    @Test
    void testValidatePolicyResource_NestedAndComplexStatementStructures() throws Exception {
        String json = "{\"PolicyName\": \"examplePolicy\", \"PolicyDocument\": {\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": \"iam:ListUsers\", \"Resource\": \"arn:aws:iam::123456789012:user/\"}, {\"Effect\": \"Allow\", \"Action\": \"iam:CreateUser\", \"Resource\": [\"arn:aws:iam::123456789012:user/Alice\", \"arn:aws:iam::123456789012:user/Bob\"]}]}}";
        JsonNode rootNode = mapper.readTree(json);
        assertTrue(validator.validatePolicyResource(rootNode), "Complex nested structures in Statements, expecting return true if handled correctly");
    }

    }



