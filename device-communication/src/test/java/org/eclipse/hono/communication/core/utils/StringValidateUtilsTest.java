package org.eclipse.hono.communication.core.utils;

import java.util.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringValidateUtilsTest {

    @Test
    public void isBase64_valid_true(){
        String string = "This is a test.";
        String base64 = Base64.getEncoder().encodeToString(string.getBytes());
        Assertions.assertTrue(StringValidateUtils.isBase64(base64));
    }

    @Test
    public void isBase64_invalid_false(){
        String string = "This is a test.";
        Assertions.assertFalse(StringValidateUtils.isBase64(string));
    }

}