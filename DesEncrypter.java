import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

class DesEncrypter 
{
  Cipher ecipher;
  Cipher dcipher;
  DesEncrypter(SecretKey key) throws Exception 
  {
    ecipher = Cipher.getInstance("DES");
    dcipher = Cipher.getInstance("DES");
    ecipher.init(Cipher.ENCRYPT_MODE, key);
    dcipher.init(Cipher.DECRYPT_MODE, key);
  }
  public String encrypt(String str) throws Exception {
    // Encode the string into bytes using utf-8
    byte[] utf8 = str.getBytes("UTF8");

    // Encrypt
    byte[] enc = ecipher.doFinal(utf8);

    // Encode bytes to base64 to get a string
    //return new java.util.Base64.Encoder.BASE64Encoder().encode(enc);
    // with 11 version jdk
    return Base64.getEncoder().encodeToString(enc);
  }
  public String decrypt(String str) throws UnsupportedEncodingException, IOException 
  {
    // Decode base64 to get bytes
    //byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
    byte[] dec = Base64.getDecoder().decode(str);

    byte[] utf8=null;
    try 
    {
        utf8 = dcipher.doFinal(dec);
    } catch (IllegalBlockSizeException ex) {
        Logger.getLogger(DesEncrypter.class.getName()).log(Level.SEVERE, null, ex);
    } catch (BadPaddingException ex) 
    {
        //System.out.println("Wrong KeySize");
        return "Wrong KeySize";
    }

    // Decode using utf-8
    return new String(utf8, "UTF8");
  }
}


