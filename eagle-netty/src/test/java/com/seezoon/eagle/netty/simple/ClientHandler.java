package com.seezoon.eagle.netty.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seezoon.eagle.netty.NettyClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 当客户端重连时候需要@Sharable 注解，不然会认为不能重复添加
 * @author hdf
 * 2017年11月19日
 */
@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String>{
	private static  Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		logger.info("client handler read msg:{}",msg);
	}
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    		ctx.flush();
    }
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
		logger.error("client error",cause);
	}
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
			//read_idle=10 s 未读则需要重连了
			case READER_IDLE:
				//先关闭当前
				ctx.close();
				break;
			case WRITER_IDLE:
				break;
			//客户端有无读写all_idle= 5s,发送心跳，服务端读超时时间设置大于客户端
			case ALL_IDLE:
				ctx.writeAndFlush("ping");
				logger.info("客户端发送心跳");
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
