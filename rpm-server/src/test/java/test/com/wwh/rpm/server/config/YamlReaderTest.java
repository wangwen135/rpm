package test.com.wwh.rpm.server.config;

import java.io.File;

import com.wwh.rpm.common.config.YamlConfigReader;
import com.wwh.rpm.common.exception.ConfigException;
import com.wwh.rpm.server.config.pojo.ServerConfig;

public class YamlReaderTest {

    public static void main(String[] args) {
        try {
            ServerConfig cf = YamlConfigReader.readConfiguration("server.yaml", ServerConfig.class);
            System.out.println(cf.toPrettyString());

            File f = new File("/opt/yaml/server.yaml");
            cf = YamlConfigReader.readConfiguration(f, ServerConfig.class);
            System.out.println(cf.toPrettyString());

        } catch (ConfigException e) {
            System.err.println(e.getMessage());
        }
    }

}
