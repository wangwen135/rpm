package test.com.wwh.rpm.config.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.scanner.ScannerException;

import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.config.client.ClientConfig;
import com.wwh.rpm.config.yaml.YamlReader;

public class YamlReaderTest {

    public static void main(String[] args) {
	try {
	    ClientConfig cf = YamlReader.readConfiguration("client.yaml", ClientConfig.class);
	    System.out.println(cf.toPrettyString());
	} catch (ConfigException e) {
	    System.err.println(e.getMessage());
	}
    }

    public static void main1(String[] args) throws FileNotFoundException, IOException {
	Yaml yaml = new Yaml();

	try (InputStream in = new FileInputStream(new File("/opt/yaml/client1.yaml"))) {
	    ClientConfig config = yaml.loadAs(in, ClientConfig.class);
	    System.out.println(config.toString());
	} catch (ScannerException e) {

	    // e.printStackTrace();
	} catch (Exception e) {

	    System.out.println(e.getMessage());

	    // e.printStackTrace();
	}

    }
}
