package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.constant.MessageType;
import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientTransactionHandler extends ChannelInboundHandlerAdapter
{

	private ChannelHandlerContext ctx;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		this.ctx = ctx;
		sendMsg("90000","1","http://com.ping.test/pang","get","{id:90999}");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		ctx.fireChannelRead(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
	{
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		ctx.fireExceptionCaught(cause);
	}

	public void sendMsg(String groupId,String groupMemeberId,String serviceLink,String type,String params){
		MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
		builder.setType(MessageType.TRANSACTION_REQ);
		JSONObject info = new JSONObject();
		info.put("groupId",groupId);
		info.put("groupMemeberId",groupMemeberId);
		info.put("serviceLink",serviceLink);
		info.put("params",params);
		info.put("type",type);
		builder.setInfo(info.toJSONString());
		MessageProto.Message message = builder.build();
		System.out.println("Send transaction message:\n" + message);
		ctx.writeAndFlush(message);
	}

}
