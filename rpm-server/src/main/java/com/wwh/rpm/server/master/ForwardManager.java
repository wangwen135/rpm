package com.wwh.rpm.server.master;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wwh.rpm.common.Constants;
import com.wwh.rpm.common.connection.SimpleChannelWarp;
import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;
import com.wwh.rpm.server.config.pojo.ForwardOverClient;

import io.netty.channel.Channel;

/**
 * 转发管理器
 * 
 * @author wangwh
 * @date 2021-3-29
 */
public class ForwardManager {

    private static final Logger logger = LoggerFactory.getLogger(ForwardManager.class);

    private AtomicLong idGenerator = new AtomicLong(0);

    private MasterServer masterServer;

    private Map<Long, CallbackComposition> callBackMap = new ConcurrentHashMap<>();

    /**
     * 超时回调
     */
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    public ForwardManager(MasterServer masterServer) {
        this.masterServer = masterServer;
    }

    private Long nextId() {
        return idGenerator.incrementAndGet();
    }

    /**
     * 判断ID是否还存在，调用过一次之后会被移除
     * 
     * @param id
     * @return
     */
    public boolean idExist(long id) {
        return callBackMap.containsKey(id);
    }

    private void callbackError(Consumer<SimpleChannelWarp> callback, Exception e) {
        try {
            callback.accept(SimpleChannelWarp.ofErroe(e));
        } catch (Exception ex) {
            logger.error("执行回调异常", ex);
        }
    }

    /**
     * <pre>
     * 获取客户端上来的一个转发通道
     * 如果要控制回调函数在哪个线程中执行需要调用方自行编码
     * </pre>
     * 
     * @param forwardConfig
     * @param callback
     */
    public void acquireClientForwardChannel(ForwardOverClient forwardConfig, Consumer<SimpleChannelWarp> callback) {

        String clientId = forwardConfig.getClientId();
        // 先找客户端是否存在
        Channel clientMasterChannel = masterServer.getChannelByCid(clientId);
        if (clientMasterChannel == null) {
            callbackError(callback, new RPMException("客户端：" + clientId + " 未上线"));
            return;
        }
        if (!clientMasterChannel.isActive()) {
            callbackError(callback, new RPMException("客户端：" + clientId + " 主连接不是活动状态"));
            return;
        }
        // 像客户端发送转发指令包
        ForwardCommandPacket fcPacket = new ForwardCommandPacket();
        final Long id = nextId();
        fcPacket.setId(id);
        fcPacket.setHost(forwardConfig.getForwardHost());
        fcPacket.setPort(forwardConfig.getForwardPort());

        clientMasterChannel.writeAndFlush(fcPacket);

        CallbackComposition cc = new CallbackComposition();
        cc.setCallback(callback);
        callBackMap.put(id, cc);

        // 定时任务调度
        ScheduledFuture<?> scheduledFuture = scheduledExecutor.schedule(() -> {
            callBack(id, SimpleChannelWarp.ofErroe(new RPMException("获取连接超时")), true);
        }, Constants.ASYNC_ACQUIRE_CLIENT_FORWARD_CHANNEL_TIMEOUT, TimeUnit.MILLISECONDS);

        cc.setsFuture(scheduledFuture);
    }

    /**
     * 收到一个客户端的转发通道
     * 
     * @param id
     * @param forwardChannel
     */
    public void receiveClientChannel(long id, Channel forwardChannel) {
        callBack(id, SimpleChannelWarp.ofChannel(forwardChannel), false);
    }

    /**
     * 接收客户端转发通道异常
     * 
     * @param id
     * @param cause
     */
    public void receiveClientChannelError(long id, Throwable cause) {
        callBack(id, SimpleChannelWarp.ofErroe(cause), false);
    }

    private void callBack(Long id, SimpleChannelWarp scw, boolean scheduled) {
        CallbackComposition composition = callBackMap.remove(id);
        if (composition == null) {
            if (scheduled) {
                logger.warn("定时任务执行时回调函数为空");
                return;
            }
            throw new RPMException("Id对应的回调不存在，可能超时了");
        }
        // 取消定时任务
        if (!scheduled) {
            composition.getsFuture().cancel(false);
        }
        // 执行回调
        try {
            composition.getCallback().accept(scw);
        } catch (Exception e) {
            if (scheduled) {
                logger.error("定时任务执行回调异常", e);
            } else {
                throw new RPMException("执行回调异常", e);
            }
        }

    }

    public void shutdown() {
        scheduledExecutor.shutdownNow();
    }

    class CallbackComposition {
        private ScheduledFuture<?> sFuture;
        private Consumer<SimpleChannelWarp> callback;

        public ScheduledFuture<?> getsFuture() {
            return sFuture;
        }

        public void setsFuture(ScheduledFuture<?> sFuture) {
            this.sFuture = sFuture;
        }

        public Consumer<SimpleChannelWarp> getCallback() {
            return callback;
        }

        public void setCallback(Consumer<SimpleChannelWarp> callback) {
            this.callback = callback;
        }
    }
}
