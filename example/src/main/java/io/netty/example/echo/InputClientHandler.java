/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.echo;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class InputClientHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf firstMessage;

    AtomicInteger atomicInteger = new AtomicInteger(1);

    /**
     * Creates a client-side handler.
     */
    public InputClientHandler() {
        /*firstMessage = Unpooled.buffer(EchoClient.SIZE);
        for (int i = 0; i < firstMessage.capacity(); i ++) {
            firstMessage.writeByte((byte) i);
        }*/
        String hello = "Hello, World";
        firstMessage = genMsgByteBuf(hello);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Utils.println("I's Client : receive server msg : ", msg);
        int i = atomicInteger.getAndIncrement();
        String msgStr = "Hello, World"+i;

        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //ctx.writeAndFlush(genMsgByteBuf(msgStr));

    }

    private ByteBuf genMsgByteBuf(String msgStr) {
        ByteBuf msgByteBuf = Unpooled.buffer(msgStr.getBytes().length);
        msgByteBuf.writeBytes(msgStr.getBytes());
        return msgByteBuf;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       //ctx.flush();
       // ctx.writeAndFlush(msg);
        System.out.println("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
