package test.com.wwh.rpm.config.yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.wwh.rpm.config.client.ClientConfig;
import com.wwh.rpm.config.client.ForwardOverServer;
import com.wwh.rpm.config.client.ServerConf;
import com.wwh.rpm.config.server.ForwardOverClient;
import com.wwh.rpm.config.server.ServerConfig;

public class YamlWriterTest {

	public static void main(String[] args) throws IOException {
		serverConfig();
		clientConfig();
	}

	public static void serverConfig() throws IOException {
		ServerConfig sc = new ServerConfig();

		sc.setSid("xdkjadfqwwefasdfasdfel123");
		sc.setHost("0.0.0.0");
		sc.setPort(9999);
		sc.setEncryption(1);

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
	}

	public static void clientConfig() throws IOException {
		ClientConfig cc = new ClientConfig();

		cc.setCid("ssssddddff");
		ServerConf serverConf = new ServerConf();
		serverConf.setHost("192.168.1.1");
		serverConf.setPort(9900);
		serverConf.setSid("xxdfsdfsdf");

		cc.setServerConf(serverConf);

		List<ForwardOverServer> forwardOverServer = new ArrayList<>();
		ForwardOverServer fos1 = new ForwardOverServer();
		fos1.setForwardHost("127.0.0.1");
		fos1.setForwardPort(8899);
		fos1.setListenHost("127.0.0.1");
		fos1.setListenPort(8899);
		forwardOverServer.add(fos1);

		ForwardOverServer fos2 = new ForwardOverServer();
		fos2.setForwardHost("127.20.1");
		fos2.setForwardPort(8991);
		fos2.setListenHost("127.0.0.1");
		fos2.setListenPort(8991);
		forwardOverServer.add(fos2);
		cc.setForwardOverServer(forwardOverServer);

		// 设置输出格式
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
		dumperOptions.setPrettyFlow(true);

		Yaml yaml = new Yaml(dumperOptions);
		File file = new File("/opt/yaml/client.yaml");
		file.getParentFile().mkdirs();
		yaml.dump(cc, new FileWriter(file));
	}
}
