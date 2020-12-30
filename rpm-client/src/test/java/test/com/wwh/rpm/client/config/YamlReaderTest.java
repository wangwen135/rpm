package test.com.wwh.rpm.client.config;

import java.io.File;

import com.wwh.rpm.client.config.pojo.ClientConfig;
import com.wwh.rpm.common.config.YamlConfigReader;
import com.wwh.rpm.common.exception.ConfigException;

public class YamlReaderTest {

    public static void main(String[] args) {
        try {
            ClientConfig cf = YamlConfigReader.readConfiguration("client.yaml", ClientConfig.class);
            System.out.println(cf.toPrettyString());

            File f = new File("/opt/yaml/client.yaml");
            cf = YamlConfigReader.readConfiguration(f, ClientConfig.class);
            System.out.println(cf.toPrettyString());

        } catch (ConfigException e) {
            System.err.println(e.getMessage());
        }
    }

}
