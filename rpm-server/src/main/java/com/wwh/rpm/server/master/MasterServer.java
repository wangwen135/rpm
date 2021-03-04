package com.wwh.rpm.server.master;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.common.utils.RpmMsgPrinter;
import com.wwh.rpm.server.ServerManager;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;
import com.wwh.rpm.server.config.pojo.ServerConfig;
import com.wwh.rpm.server.master.handler.MasterHandlerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 主服务
 * 
 * @author wwh
 */
public class MasterServer {

	private static final Logger logger = LoggerFactory.getLogger(MasterServer.class);

	private ServerManager serverManager;

	private ClientTokenManager clientTokenManager;

	private ForwardManager forwardManager;

	private AtomicBoolean isRunning = new AtomicBoolean(false);

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private Channel channel;
	// 客户端列表

	public MasterServer(ServerManager serverManager) {
		this.serverManager = serverManager;
		this.clientTokenManager = new ClientTokenManager();
		this.forwardManager = new ForwardManager(this);
		this.bossGroup = new NioEventLoopGroup(1);
		this.workerGroup = new NioEventLoopGroup();
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	public void shutdown() {
		logger.info("主服务关闭线程池...");
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();

		if (!isRunning()) {
			logger.warn("主服务没有启动");
			return;
		}
		if (channel != null && channel.isActive()) {
			channel.close();
			try {
				channel.closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() throws Exception {
		if (!isRunning.compareAndSet(false, true)) {
			logger.error("主服务正在运行！");
			return;
		}

		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
		b.childOption(ChannelOption.TCP_NODELAY, true);

		b.childHandler(new MasterHandlerInitializer(this));

		channel = b.bind(getConfig().getHost(), getConfig().getPort()).sync().channel();

		RpmMsgPrinter.printMsg("主服务启动在 {}:{}", getConfig().getHost(), getConfig().getPort());

		channel.closeFuture().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				logger.info("主服务关闭！");
				serverManager.close();
			}
		});

	}

	/**
	 * 获取配置
	 * 
	 * @return
	 */
	public ServerConfig getConfig() {
		return serverManager.getConfig();
	}

	/**
	 * 注册客户端
	 * 
	 * @param cid
	 * @param token
	 * @param channel
	 */
	public void registClient(String cid, String token, Channel channel) {
		RpmMsgPrinter.printMsg("客户端注册！ cid={}  address={}", cid, channel.remoteAddress());
		clientTokenManager.regist(cid, token, channel);
	}

	/**
	 * 注销客户端
	 * 
	 * @param cid
	 */
	public void unregistClient(String cid) {
		RpmMsgPrinter.printMsg("客户端注销！ cid={}", cid);
		clientTokenManager.unregist(cid);
	}

	/**
	 * 验证token
	 * 
	 * @param token
	 * @return token对应客户端的cid
	 */
	public String validateToken(String token) {
		String cid = clientTokenManager.getCidByToken(token);
		if (cid == null) {
			throw new RPMException("无效的token");
		} else {
			return cid;
		}
	}

	/**
	 * 根据cid获取对应客户端的主Channel
	 * 
	 * @param cid
	 * @return
	 */
	public Channel getChannelByCid(String cid) {
		return clientTokenManager.getChannelByCid(cid);
	}

	/**
	 * 获取转发管理器
	 * 
	 * @return
	 */
	public ForwardManager getForwardManager() {
		return forwardManager;
	}

	/**
	 * 获取一个客户端的转发通道，阻塞
	 * 
	 * @param forwardConfig
	 * @return 成功时返回通道，失败或超时抛出异常
	 */
	public Channel acquireClientForwardChannel(ForwardOverClient forwardConfig) {
		return forwardManager.acquireClientForwardChannel(forwardConfig);
	}

	public ServerManager getServerManager() {
		return serverManager;
	}

}
