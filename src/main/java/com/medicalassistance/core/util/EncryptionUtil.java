package com.medicalassistance.core.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptionUtil {

  static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
  static PHPass phPass = new PHPass(8);
  public static String encryptPassword(String password) {

    String encryptedPassword =  bCryptPasswordEncoder.encode(password);
    return encryptedPassword;
  }

  public static boolean isValidPassword(String password, String encryptedPassword) {

    if (bCryptPasswordEncoder.matches(password, encryptedPassword))
    {
      return true;
    }
    else if(phPass.checkPassword(password, encryptedPassword))
    {
      return true;
    }
    return false;
  }
}
