package com.seezoon.eagle.netty.simple;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
	private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

	//连接上的自定义属性 相当于session.setAttr();
	private static AttributeKey<String> attributeKeyIp = AttributeKey.valueOf("ip");
	/**
	 * 一般会用一个map 存用户和channel的对应关系
	 */
	public static List<ChannelHandlerContext> sessions = Collections.synchronizedList(new ArrayList<ChannelHandlerContext>());
	/**
	 * 通道激活时候
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		sessions.add(ctx);
		Attribute<String> attr = ctx.channel().attr(attributeKeyIp);
		attr.set(((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress());
	}
	/**
	 * 读取数据
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		if ("ping".equals(msg)) {//模拟是心跳
			ctx.write("pong");
			logger.info("server handler 收到客户端心跳 and write msg pong");
		} else {
			ctx.write(msg + new Date());
			logger.info("server handler read and write msg:{}", msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// 真正发送，writeAndFlush 直接发送
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		logger.error("server exceptionCaught error", cause);
	}
	/**
	 * channel 被正常关闭或者客户端主动close channel关闭。
	 */
     @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	 logger.info("server handler channelInactive");
    	 super.channelInactive(ctx);
    }
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
		//服务器一般对写不做处理
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
			//客户端有发心跳 如果触发的服务端读超时，则客户端和服务端是通信异常的，服务端读超时时间设置大于客户端
			case READER_IDLE:
				Iterator<ChannelHandlerContext> iterator = sessions.iterator();
				while(iterator.hasNext()) {
					ChannelHandlerContext channelHandlerContext = iterator.next();
					if (channelHandlerContext == ctx) {
						iterator.remove();
						logger.info("移除当前异常客户端，ip：{}",ctx.channel().attr(attributeKeyIp).get());
					} else {
						//其他异常连接应该用一个线程单独检测
					}
				}
				break;
			case WRITER_IDLE:
				break;
			case ALL_IDLE:
				break;
			default:
				break;
			}
		} else {
			//其他事件放行
			super.userEventTriggered(ctx, evt);
		}
	}
}
