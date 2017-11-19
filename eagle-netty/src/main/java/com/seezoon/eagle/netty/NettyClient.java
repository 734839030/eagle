package com.seezoon.eagle.netty;

import java.util.concurrent.TimeUnit;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * netty 客户端
 * 
 * @author hdf 2017年11月18日
 */
public class NettyClient {
	private static Logger logger = LoggerFactory.getLogger(NettyClient.class);

	private Channel channel;
	private String host;
	private int port;
	private ChannelHandler[] handlers;
	private Bootstrap b;
	EventLoopGroup group;
	private boolean needReconnet = true;

	public NettyClient(String host, int port, ChannelHandler[] handlers) {
		super();
		this.host = host;
		this.port = port;
		this.handlers = handlers;
	}

	public void start() throws InterruptedException {
		group = new NioEventLoopGroup();
		b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000).handler(new LoggingHandler(LogLevel.INFO))
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();
						// readerIdleTimeSeconds, 读超时. 即当在指定的时间间隔内没有从 Channel 读取到数据时, 会触发一个 READER_IDLE
						// 的 IdleStateEvent 事件.
						// writerIdleTimeSeconds, 写超时. 即当在指定的时间间隔内没有数据写入到 Channel 时, 会触发一个 WRITER_IDLE 的
						// IdleStateEvent 事件.
						// allIdleTimeSeconds, 读/写超时. 即当在指定的时间间隔内没有读或写操作时, 会触发一个 ALL_IDLE 的
						// IdleStateEvent 事件.
						p.addLast(new IdleStateHandler(10, 5, 5));
					    p.addLast(new ReconnectHandler(NettyClient.this));
						p.addLast(handlers);
					}
				});
		doConnect();
	}

	/**
	 * 连接
	 * 
	 * @throws InterruptedException
	 */
	private void doConnect() {
		if (channel != null && channel.isActive()) {
			return;
		}
		// 异步拿客户端 Start the client.
		ChannelFuture f = b.connect(host, port);
		f.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture futureListener) throws Exception {
				if (futureListener.isSuccess()) {
					channel = futureListener.channel();
					logger.info("Connect to server successfully!");
				} else {
					logger.info("Failed to connect to server, try connect after 3s");
					futureListener.channel().eventLoop().schedule(new Runnable() {
						@Override
						public void run() {
							doConnect();
						}
					}, 3, TimeUnit.SECONDS);
				}
			}
		});
		// 阻塞的 项目集成时不需要
		// channel.closeFuture().sync();
	}

	/**
	 * 发送消息
	 * 
	 * @param msg
	 */
	public void sendMsg(Object msg) {
		if (channel == null || !channel.isActive()) {
			throw new IllegalStateException("channel do not active");
		}
		channel.writeAndFlush(msg);
	}

	public void close() {
		needReconnet = false;
		if (channel != null) {
			//会触发channelInactive
			channel.close();
		}
		if (null != group) {
			group.shutdownGracefully();
		}
		logger.info("netty client channel closed");
	}

	/**
	 * 
	 * @author hdf 2017年11月19日
	 */
	class ReconnectHandler extends ChannelInboundHandlerAdapter {
		private NettyClient nettyClient;

		public ReconnectHandler(NettyClient client) {
			this.nettyClient = client;
		}

		/**
		 * channel被关闭时候会触发
		 */
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			if (nettyClient.needReconnet) {
				nettyClient.doConnect();
			}
		}
	}
}
