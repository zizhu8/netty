package io.netty.example.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by yongyang on 17/6/17.
 */
public class Utils {

    public static void println(String tag, Object msg){
        ByteBuf byteBuf = (ByteBuf)msg;
        byte [] data = new byte[byteBuf.readableBytes()];
        byteBuf.duplicate().readBytes(data);
        System.out.println(tag+new String(data));
    }

    public static ByteBuf genMsgByteBuf(String msgStr) {
        ByteBuf msgByteBuf = Unpooled.buffer(msgStr.getBytes().length);
        msgByteBuf.writeBytes(msgStr.getBytes());
        return msgByteBuf;
    }

}
