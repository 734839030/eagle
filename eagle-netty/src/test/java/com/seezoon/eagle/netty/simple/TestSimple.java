package com.seezoon.eagle.netty.simple;

import java.nio.charset.Charset;

import org.junit.Test;

import com.seezoon.eagle.netty.NettyClient;
import com.seezoon.eagle.netty.NettyServer;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class TestSimple {

	int port = 7000;
	StringEncoder encoder = new StringEncoder(CharsetUtil.UTF_8);
	StringDecoder decoder = new StringDecoder(Charset.forName("UTF-8"));
     
	@Test
	public void echoServer() throws Exception {
		NettyServer nettyServer = new NettyServer(port, new ChannelHandler[] {encoder,decoder,new ServerHandler()});
		//会阻塞
		nettyServer.startServer();
	}
	@Test
	public void echoClient() throws Exception {
		final NettyClient nettyClient  = new NettyClient("127.0.0.1", port, new ChannelHandler[] {encoder,decoder,new ClientHandler()});
		//不会阻塞
		nettyClient.start();
		System.out.println("任意键发送消息");
		System.in.read();
		for (int i = 0; i < 100; i++) {
			nettyClient.sendMsg("hello netty" + i);
			Thread.sleep(10);
		}
		nettyClient.sendMsg("hello netty" + 101);
		System.in.read();
		nettyClient.close();
		System.out.println("手动关掉netty channel");
		System.in.read();
	}
}
