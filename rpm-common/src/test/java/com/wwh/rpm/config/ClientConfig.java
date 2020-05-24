package com.wwh.rpm.config;

import java.util.List;

public class ClientConfig {

	private String server;
	private int port;

	private String authId;

	private List<PortMapper> mappers;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public List<PortMapper> getMappers() {
		return mappers;
	}

	public void setMappers(List<PortMapper> mappers) {
		this.mappers = mappers;
	}

	@Override
	public String toString() {
		return "ClientConfig [server=" + server + ", port=" + port + ", authId=" + authId + ", mappers=" + mappers
				+ "]";
	}

}
