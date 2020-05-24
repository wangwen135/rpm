package com.wwh.rpm.config;

public class PortMapper {

	private int type;

	private String localBindAddr;

	private int localPort;

	private String remoteServerAddr;

	private int remoteServerPort;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getLocalBindAddr() {
		return localBindAddr;
	}

	public void setLocalBindAddr(String localBindAddr) {
		this.localBindAddr = localBindAddr;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public String getRemoteServerAddr() {
		return remoteServerAddr;
	}

	public void setRemoteServerAddr(String remoteServerAddr) {
		this.remoteServerAddr = remoteServerAddr;
	}

	public int getRemoteServerPort() {
		return remoteServerPort;
	}

	public void setRemoteServerPort(int remoteServerPort) {
		this.remoteServerPort = remoteServerPort;
	}

	@Override
	public String toString() {
		return "PortMapper [type=" + type + ", localBindAddr=" + localBindAddr + ", localPort=" + localPort
				+ ", remoteServerAddr=" + remoteServerAddr + ", remoteServerPort=" + remoteServerPort + "]";
	}

}
