package com.wwh.rpm.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

public class YamlReaderTest {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Yaml yaml = new Yaml();
		try (InputStream in = new FileInputStream(new File("/opt/yaml/test.yaml"))) {
			ClientConfig config = yaml.loadAs(in, ClientConfig.class);
			System.out.println(config.toString());
		}
	}
}
