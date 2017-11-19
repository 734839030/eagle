package com.seezoon.eagle.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * netty 服务器
 * 
 * @author hdf 2017年11月18日
 */
public class NettyServer {

	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private Channel channel;
	private int port;
	private ChannelHandler[] handlers;

	public NettyServer(int port, ChannelHandler... handlers) {
		super();
		this.port = port;
		this.handlers = handlers;
	}

	/**
	 * 启动服务器
	 * 
	 * @param port
	 *            端口
	 * @param handlers
	 *            处理器，先后顺序
	 * @throws InterruptedException
	 */
	public void startServer() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					// tpc udp等实现类
					.channel(NioServerSocketChannel.class)
					// option()是提供给NioServerSocketChannel用来接收进来的连接,也就是boss线程。childOption()是提供给由父管道ServerChannel接收到的连接
					// socket 通信时间
					.childOption(ChannelOption.SO_TIMEOUT, 1000)
					// 测试时候方便netty 输出整个日志
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							// readerIdleTimeSeconds, 读超时. 即当在指定的时间间隔内没有从 Channel 读取到数据时, 会触发一个 READER_IDLE
							// 的 IdleStateEvent 事件.
							// writerIdleTimeSeconds, 写超时. 即当在指定的时间间隔内没有数据写入到 Channel 时, 会触发一个 WRITER_IDLE 的
							// IdleStateEvent 事件.
							// allIdleTimeSeconds, 读/写超时. 即当在指定的时间间隔内没有读或写操作时, 会触发一个 ALL_IDLE 的
							// IdleStateEvent 事件.
							p.addLast(new IdleStateHandler(10, 10, 10));
							p.addLast(handlers);
						}
					});
			ChannelFuture f = b.bind(port).sync();
			logger.info("netty server channel started");
			// Wait until the server socket is closed.
			channel = f.channel();
			//// 阻塞的 项目集成时不需要
			channel.closeFuture().sync();
		} finally {
			// Shut down all event loops to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	/**
	 * 关闭
	 */
	public void close() {
		if (null != channel) {
			channel.close();
			logger.info("netty server channel closed");
		}
	}

}
