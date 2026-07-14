package org.example.util;

import org.mindrot.jbcrypt.BCrypt;

public class Encrypt {

    public static String hashPin(String pin) {
        return BCrypt.hashpw(pin, BCrypt.gensalt(12));
    }

    public static boolean verifyPin(String pin, String hashedPin) {
        return BCrypt.checkpw(pin, hashedPin);
    }
}

