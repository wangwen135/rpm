package com.wwh.rpm.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlWriterTest {

	public static void main(String[] args) throws IOException {
		ClientConfig cc = new ClientConfig();
		cc.setServer("192.168.1.1");
		cc.setPort(1232);
		List<PortMapper> mappers = new ArrayList<>();
		PortMapper mapper = new PortMapper();
		mapper.setLocalBindAddr("0.0.0.0");
		mapper.setLocalPort(80);
		mapper.setRemoteServerAddr("127.0.0.1");
		mapper.setRemoteServerPort(8080);
		mapper.setType(1);
		mappers.add(mapper);
		cc.setMappers(mappers);
		

		// 设置输出格式
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
		dumperOptions.setPrettyFlow(true);

		Yaml yaml = new Yaml(dumperOptions);
		File file = new File("/opt/yaml/test.yaml");
		file.getParentFile().mkdirs();
		yaml.dump(cc, new FileWriter(file));
	}
}
