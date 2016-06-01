package supportclasses;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Olerdrive on 31.05.16.
 */
public class PropertiesReader {
    private Properties serverProperties;

    public PropertiesReader(@SuppressWarnings("SameParameterValue") String serverPropertiesPath) throws IOException{
        final InputStream is = getClass().getClassLoader().getResourceAsStream(serverPropertiesPath);
        if (is != null) {
            serverProperties = new Properties();
            serverProperties.load(is);
        }
        else {
            throw new FileNotFoundException("File " + serverPropertiesPath + " not found");
        }

    }

    public String getHost(){
        return serverProperties.getProperty("host");
    }

    public int getPort(){
        return Integer.parseInt(serverProperties.getProperty("port"));
    }
}
