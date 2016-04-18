package supportclasses;
import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Olerdrive on 18.04.16.
 */
public final class MD5Hash {
    private static String MD5String;
    private static AtomicLong counter;

    private MD5Hash(String inputString){
        counter = new AtomicLong(0);
        MD5String = generateMD5(inputString);

    }

    @SuppressWarnings("MagicNumber")
    @NotNull
    private static String generateMD5(String input){
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            input += String.valueOf(counter.getAndIncrement());
            final byte[] messageDigest = md.digest(input.getBytes());
            // Convert to hex string
            final StringBuilder sb = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                if ((0xff & aMessageDigest) < 0x10) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(0xff & aMessageDigest));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMD5(String hash){
        MD5String = hash;
    }

    @NotNull
    public static String getHashString(String input){
        String hash = generateMD5(input);
        setMD5(hash);
        return MD5String;
    }



}
