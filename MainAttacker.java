import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class MainAttacker {
    public static boolean Finished = false;
    public static int CipherCount = 0;
    public static Set<String> wordSet = new HashSet<String>();

    public static Set<String> getDictionary() throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                wordSet.add(line.toLowerCase());
            }
        }
        return wordSet;
    }

    public static void main(String[] argv) throws Exception {
        Set<String> Dictionary = getDictionary();

        //Encryption For Testing
        System.out.println("Encryption For Testing");
        KeyGeneration KGen = new KeyGeneration();
        byte[] ByteSeq = KGen.ByteSeq;

        SecretKeyFactory sf = SecretKeyFactory.getInstance("DES");

        byte[] B1 = new byte[]{3, 7, 6, 0, 0, 31, 0x70, (byte) 0x80};
        SecretKey K1 = sf.generateSecret(new DESKeySpec(B1));

        System.out.println("####### Secret Key 1: " + Base64.getEncoder().encodeToString(K1.getEncoded()));
        DesEncrypter encrypter = new DesEncrypter(K1);
        String encrypted = encrypter.encrypt("secret");
        PrintWriter CodeBreakWriter = null;
        try {
            CodeBreakWriter = new PrintWriter(new FileWriter(("CipherFile.txt"), true));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        CodeBreakWriter.println(encrypted);
        //	CodeBreakWriter.println(");
        CodeBreakWriter.close();
        System.out.println("DES encrypted string: " + encrypted);
        String decrypted = encrypter.decrypt(encrypted);
        System.out.println("DES decrypted string: " + decrypted);
        System.out.println("Encryption Test Finish.");
        /////////////////////////////////////////

        int NumbOfThreads = 128;

        AttackingThread[] attackers = new AttackingThread[NumbOfThreads];
        try (BufferedReader br = new BufferedReader(new FileReader("CipherFile.txt"))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                System.out.println("checking line " + line);
                for (int a = 0; a < NumbOfThreads; a++) {
                    attackers[a] = new AttackingThread(a, ByteSeq, line, sf);
                    attackers[a].start();
                }
                for (int i = 0; i < NumbOfThreads; i++) {
                    attackers[i].join();
                }
            }
            CipherCount++;
        }


        return;
    }
}
