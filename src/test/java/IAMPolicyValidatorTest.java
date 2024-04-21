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
    void testValidatePolicyResource_WithStarInResource() throws Exception {
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

        // You can add more tests to cover other cases
    }



