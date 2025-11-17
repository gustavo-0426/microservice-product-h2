package com.co.softworld.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptHelper {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password1 = "123";
        String password2 = "456";
        
        System.out.println("Hash for '123': " + encoder.encode(password1));
        System.out.println("Hash for '456': " + encoder.encode(password2));
        
        String currentHash1 = "$2a$10$tCuxNeov6aZssW.tyRWBde.eS0pV2LEyQZ6YwHfYJ1I8tZdJEjCW6";
        String currentHash2 = "$2a$10$L9F2ggpePySjhnZ.H72vBe1g/0Nvaah7FWCEkNi4C7HOGrFVh1t2G";
        
        System.out.println("Verificando hash actual con 'Admin123!': " + encoder.matches(password1, currentHash1));
        System.out.println("Verificando hash actual con 'Test123!': " + encoder.matches(password2, currentHash2));
    }
}