package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.dbconnection.DBOperationType;
import com.coconason.dtf.client.core.dbconnection.LockAndCondition;
import com.coconason.dtf.client.core.dbconnection.SecondThreadsInfo;
import com.coconason.dtf.client.core.dbconnection.ThreadsInfo;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@ChannelHandler.Sharable
public class ClientTransactionHandler extends ChannelInboundHandlerAdapter
{

	@Autowired
	ThreadsInfo threadsInfo;

	@Autowired
	SecondThreadsInfo secondThreadsInfo;

	@Autowired
	ApplicationContext applicationContext;

	ChannelHandlerContext ctx;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{

		super.channelActive(ctx);
		this.ctx = ctx;
		System.out.println("新建链接-->"+this.ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{

		MessageProto.Message message = (MessageProto.Message) msg;
		ActionType action = message.getAction();
		JSONObject map=null;
		LockAndCondition lc=null;
		DBOperationType state=null;
		if(action != ActionType.HEARTBEAT_REQ&&action != ActionType.HEARTBEAT_RESP&&action != ActionType.LOGIN_REQ&&action != ActionType.LOGIN_RESP){
			map = JSONObject.parseObject(message.getInfo().toString());
			lc = threadsInfo.get(map.get("groupId").toString());
			state = lc.getState();
		}
		switch (action){
			case APPROVESUBMIT:
				//1.If notified to be commit
				if(state == DBOperationType.COMMIT){
					lc.signal();
				}
				//2.If notified to be rollback
				else if(state == DBOperationType.ROLLBACK){
					lc.signal();
				}
				break;
			case APPROVESUBMIT_STRONG:
				//1.If notified to be commit
				if(state == DBOperationType.COMMIT){
					lc.signal();
				}
				//2.If notified to be rollback
				else if(state == DBOperationType.ROLLBACK){
					lc.signal();
				}
				break;
			case WHOLE_SUCCESS_STRONG:
				LockAndCondition secondlc = secondThreadsInfo.get(map.get("groupId").toString());
				secondlc.setState(DBOperationType.WHOLESUCCESS);
				secondlc.signal();
				break;
			case WHOLE_FAIL_STRONG:
				LockAndCondition secondlc2 = secondThreadsInfo.get(map.get("groupId").toString());
				secondlc2.setState(DBOperationType.WHOLEFAIL);
				secondlc2.signal();
				break;
			case CANCEL:
				lc.setState(DBOperationType.ROLLBACK);
				lc.signal();
				break;
			default:
				break;
		}
		ctx.fireChannelRead(msg);
//		if(action==ActionType.APPROVESUBMIT){
//			JSONObject map = JSONObject.parseObject(message.getInfo().toString());
//			LockAndCondition lc = threadsInfo.get(map.get("groupId").toString());
//			DBOperationType state = lc.getState();
//			//1.If notified to be commit
//			if(state == DBOperationType.COMMIT){
//				lc.signal();
//			}
//			//2.If notified to be rollback
//			else if(state == DBOperationType.ROLLBACK){
//				lc.signal();
//			}
//		}else if(action==ActionType.CANCEL){
//			JSONObject map = JSONObject.parseObject(message.getInfo().toString());
//			LockAndCondition lc = threadsInfo.get(map.get("groupId").toString());
//			DBOperationType state = lc.getState();
//			lc.setState(DBOperationType.ROLLBACK);
//			lc.signal();
//		}
//		ctx.fireChannelRead(msg);
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

	public void sendMsg(TransactionServiceInfo serviceInfo) {
		ActionType actionType = serviceInfo.getAction();
		switch (actionType){
			case ADD:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberId").toString(),(Method) serviceInfo.getInfo().get("method"),(Object[]) serviceInfo.getInfo().get("args"));
				break;
			case APPLYFORSUBMIT:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberSet").toString());
				break;
			case ADD_STRONG:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberId").toString(),(Method) serviceInfo.getInfo().get("method"),(Object[]) serviceInfo.getInfo().get("args"));
				break;
			case APPLYFORSUBMIT_STRONG:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberSet").toString());
				break;
			case SUB_SUCCESS_STRONG:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberSet").toString(),serviceInfo.getInfo().get("memberId").toString());
				break;
			case SUB_FAIL_STRONG:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberSet").toString());
				break;
			default:
				break;
		}
	}

	public void sendMsg(String id,ActionType action,String groupId, String groupMemberId, Method method,Object[] args){
		MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
		JSONObject info = new JSONObject();
		info.put("groupId",groupId);
		info.put("groupMemberId",groupMemberId);
		info.put("method",method);
		info.put("args",args);
		builder.setInfo(info.toJSONString());
		builder.setId(id);
		builder.setAction(action);
		MessageProto.Message message = builder.build();
		System.out.println("Send transaction message:\n" + message);
		ctx.writeAndFlush(message);
	}

	public void sendMsg(String id,ActionType action,String groupId, String groupMemberSet){
		MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
		JSONObject info = new JSONObject();
		info.put("groupId",groupId);
		info.put("groupMemberSet",groupMemberSet);
		builder.setInfo(info.toJSONString());
		builder.setId(id);
		builder.setAction(action);
		MessageProto.Message message = builder.build();
		System.out.println("Send transaction message:\n" + message);
		ctx.writeAndFlush(message);
	}

	public void sendMsg(String id,ActionType action,String groupId, String groupMemberSet,String memberId){
		MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
		JSONObject info = new JSONObject();
		info.put("groupId",groupId);
		info.put("groupMemberSet",groupMemberSet);
		info.put("memberId",memberId);
		builder.setInfo(info.toJSONString());
		builder.setId(id);
		builder.setAction(action);
		MessageProto.Message message = builder.build();
		System.out.println("Send transaction message:\n" + message);
		ctx.writeAndFlush(message);
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
}
