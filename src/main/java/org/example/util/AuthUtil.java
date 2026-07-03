package org.example.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.SQLException;

public class AuthUtil {

    public boolean authenticatePIN(String pin, String bcryptHashString) throws SQLException {
        BCrypt.Result result = BCrypt.verifyer().verify(pin.toCharArray(), bcryptHashString);
        return result.verified;
    }

    public String encryptPin(String pin){
        return BCrypt.withDefaults().hashToString(12, pin.toCharArray());
    }
}
