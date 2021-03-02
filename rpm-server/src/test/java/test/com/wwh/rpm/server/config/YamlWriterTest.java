package test.com.wwh.rpm.server.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.wwh.rpm.common.enums.EncryptTypeEnum;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;
import com.wwh.rpm.server.config.pojo.ServerConfig;

public class YamlWriterTest {

    public static void main(String[] args) throws IOException {
        ServerConfig sc = new ServerConfig();

        sc.setSid("xdkjadfqwwefasdfasdfel123");
        sc.setHost("0.0.0.0");
        sc.setPort(9999);
        sc.setEncryptType(EncryptTypeEnum.SIMPLE);
        sc.setCompressionLevel(9);
        sc.setEnableCompression(true);

        List<ForwardOverClient> forwardOverClient = new ArrayList<>();

        sc.setForwardOverClient(forwardOverClient);

        // 设置输出格式
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(true);

        Yaml yaml = new Yaml(dumperOptions);
        File file = new File("/opt/yaml/server.yaml");
        file.getParentFile().mkdirs();
        yaml.dump(sc, new FileWriter(file));

        System.out.println("文件生产在：" + file.getAbsolutePath());
    }

}
