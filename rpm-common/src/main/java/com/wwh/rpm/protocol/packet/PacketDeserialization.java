package com.wwh.rpm.protocol.packet;

import static com.wwh.rpm.protocol.ProtocolConstants.TYPE_AUTH;
import static com.wwh.rpm.protocol.ProtocolConstants.TYPE_COMMAND;
import static com.wwh.rpm.protocol.ProtocolConstants.*;

import java.io.IOException;

import com.wwh.rpm.common.exception.RPMException;
import com.wwh.rpm.common.serialize.Serialization;
import com.wwh.rpm.common.serialize.SerializerFactory;
import com.wwh.rpm.protocol.packet.auth.AuthPacket;
import com.wwh.rpm.protocol.packet.auth.RegistPacket;
import com.wwh.rpm.protocol.packet.auth.TokenPacket;
import com.wwh.rpm.protocol.packet.command.ForwardCommandPacket;
import com.wwh.rpm.protocol.packet.command.ForwardResultPacket;
import com.wwh.rpm.protocol.packet.control.ShutdownPacket;
import com.wwh.rpm.protocol.packet.general.FailPacket;
import com.wwh.rpm.protocol.packet.general.ResultPacket;
import com.wwh.rpm.protocol.packet.general.SuccessPacket;
import com.wwh.rpm.protocol.packet.heartbeat.HearbeatPacket;

/**
 * 数据包对象反序列化
 * 
 * @author wangwh
 * @date 2020-12-29
 */
public class PacketDeserialization {

    public static AbstractPacket deserialization(byte type, byte[] bytes) throws IOException {
        Serialization serialization = SerializerFactory.getSerializer();
        switch (type) {
        case TYPE_HEARTBEAT:
            return serialization.deserialize(bytes, HearbeatPacket.class);
        case TYPE_RESULT:
            return serialization.deserialize(bytes, ResultPacket.class);
        case TYPE_SUCCESS:
            return serialization.deserialize(bytes, SuccessPacket.class);
        case TYPE_FAIL:
            return serialization.deserialize(bytes, FailPacket.class);
        case TYPE_REGIST:
            return serialization.deserialize(bytes, RegistPacket.class);
        case TYPE_AUTH:
            return serialization.deserialize(bytes, AuthPacket.class);
        case TYPE_TOKEN:
            return serialization.deserialize(bytes, TokenPacket.class);
        case TYPE_COMMAND:
            break;
        case TYPE_FORWARD_COMMAND:
            return serialization.deserialize(bytes, ForwardCommandPacket.class);
        case TYPE_FORWARD_COMMAND_RESULT:
            return serialization.deserialize(bytes, ForwardResultPacket.class);
        case TYPE_SHUTDOWN:
            return serialization.deserialize(bytes, ShutdownPacket.class);
        }

        throw new RPMException("未知的数据包类型：" + type);
    }
}
