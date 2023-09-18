package com.developersstack.edumanagement.util.security;

import org.mindrot.jbcrypt.BCrypt;

public class PassswordManager {
    public String encrypt(String rawPassword){
        return BCrypt.hashpw(rawPassword,BCrypt.gensalt(10));
    }

    public boolean checkPassword(String rawPassword,String hashPassword){
        return BCrypt.checkpw(rawPassword,hashPassword);
    }
}
