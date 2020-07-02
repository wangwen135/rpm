package com.wwh.rpm.config.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.scanner.ScannerException;

import com.wwh.rpm.common.exception.ConfigException;

public class YamlReader {

    public static <T> T readConfiguration(File file, Class<T> c) {
	if (file == null || !file.exists()) {
	    throw new ConfigException("配置文件不存在");
	}
	Yaml yaml = new Yaml();
	try (InputStream in = new FileInputStream(file)) {
	    return yaml.loadAs(in, c);
	} catch (ScannerException e) {
	    throw new ConfigException("配置文件【" + file + "】格式错误\n" + e.getMessage(), e);
	} catch (Exception e) {
	    throw new ConfigException("配置文件【" + file + "】解析错误\n" + e.getMessage(), e);
	}
    }

    public static <T> T readConfiguration(String file, Class<T> c) {
	if (StringUtils.isBlank(file)) {
	    throw new ConfigException("配置文件不存在");
	}
	Yaml yaml = new Yaml();
	try (InputStream in = YamlReader.class.getClassLoader().getResourceAsStream(file)) {
	    if (in == null) {
		throw new ConfigException("配置文件【" + file + "】不存在");
	    }
	    return yaml.loadAs(in, c);
	} catch (ConfigException e) {
	    throw e;
	} catch (ScannerException e) {
	    throw new ConfigException("配置文件【" + file + "】格式错误\n" + e.getMessage(), e);
	} catch (Exception e) {
	    throw new ConfigException("配置文件【" + file + "】解析错误\n" + e.getMessage(), e);
	}
    }
}
