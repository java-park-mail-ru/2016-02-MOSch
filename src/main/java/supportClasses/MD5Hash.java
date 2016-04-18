package supportclasses;
import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Olerdrive on 18.04.16.
 */
public final class MD5Hash {
    private static AtomicLong counter = new AtomicLong(0);

    private MD5Hash(){
        counter = new AtomicLong(0);
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


    @NotNull
    public static String getHashString(String input){
        return generateMD5(input);
    }



}
