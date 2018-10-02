package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.dbconnection.ClientLockAndConditionInterface;
import com.coconason.dtf.client.core.dbconnection.DbOperationType;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.google.common.cache.Cache;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@ChannelHandler.Sharable
class ClientTransactionHandler extends ClientTransactionHandlerAdapter
{

	@Autowired
	@Qualifier("threadLockCacheProxy")
	private Cache<String,ClientLockAndConditionInterface> threadLockCacheProxy;

	@Autowired
	@Qualifier("threadLockCacheProxy")
	private Cache<String,ClientLockAndConditionInterface>  secondThreadLockCacheProxy;

	@Autowired
	@Qualifier("threadLockCacheProxy")
	private Cache<String,ClientLockAndConditionInterface>  thirdThreadLockCacheProxy;

	@Autowired
	@Qualifier("threadLockCacheProxy")
	private Cache<String,ClientLockAndConditionInterface>  asyncFinalCommitThreadLockCacheProxy;

	@Autowired
	@Qualifier("threadLockCacheProxy")
	private Cache<String,ClientLockAndConditionInterface>  syncFinalCommitThreadLockCacheProxy;

	private ChannelHandlerContext ctx;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		super.channelActive(ctx);
		this.ctx = ctx;
		System.out.println("create connection-->"+this.ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		System.out.println(msg);
		MessageProto.Message message = (MessageProto.Message) msg;
		ActionType action = message.getAction();
		JSONObject map=null;
		ClientLockAndConditionInterface lc=null;
		DbOperationType state=null;

		if(action == ActionType.APPROVESUBMIT||action == ActionType.APPROVESUBMIT_STRONG||action == ActionType.CANCEL){
			map = JSONObject.parseObject(message.getInfo().toString());
			lc = threadLockCacheProxy.getIfPresent(map.get("groupId").toString());
			state = lc.getState();
		}
		switch (action){
			case APPROVESUBMIT:
				if(state == DbOperationType.COMMIT||state == DbOperationType.ROLLBACK){
					lc.signal();
				}
				break;
			case APPROVESUBMIT_STRONG:
				if(state == DbOperationType.COMMIT||state == DbOperationType.ROLLBACK){
					lc.signal();
				}
				break;
			case WHOLE_SUCCESS_STRONG:
				map = JSONObject.parseObject(message.getInfo().toString());
				ClientLockAndConditionInterface secondlc = secondThreadLockCacheProxy.getIfPresent(map.get("groupId").toString());
				secondlc.setState(DbOperationType.WHOLE_SUCCESS);
				secondlc.signal();
				break;
			case WHOLE_FAIL_STRONG:
				map = JSONObject.parseObject(message.getInfo().toString());
				ClientLockAndConditionInterface secondlc2 = secondThreadLockCacheProxy.getIfPresent(map.get("groupId").toString());
				secondlc2.setState(DbOperationType.WHOLE_FAIL);
				secondlc2.signal();
				break;
			case CANCEL:
				lc.setState(DbOperationType.ROLLBACK);
				lc.signal();
				break;
			case ADD_SUCCESS_ASYNC:
				ClientLockAndConditionInterface thirdlc = thirdThreadLockCacheProxy.getIfPresent(JSONObject.parseObject(message.getInfo().toString()).get("groupId").toString());
				thirdlc.setState(DbOperationType.ASYNC_SUCCESS);
				thirdlc.signal();
				break;
			case ADD_FAIL_ASYNC:
				ClientLockAndConditionInterface thirdlc2 = thirdThreadLockCacheProxy.getIfPresent(JSONObject.parseObject(message.getInfo().toString()).get("groupId").toString());
				thirdlc2.setState(DbOperationType.ASYNC_FAIL);
				thirdlc2.signal();
				break;
			case COMMIT_SUCCESS_ASYNC:
				ClientLockAndConditionInterface asyncFinallc = asyncFinalCommitThreadLockCacheProxy.getIfPresent(JSONObject.parseObject(message.getInfo().toString()).get("groupId").toString());
				asyncFinallc.setState(DbOperationType.COMMIT_SUCCESS_ASYNC);
				asyncFinallc.signal();
				break;
			default:
				break;
		}
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
	@Override
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
			case ADD_ASYNC:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberId").toString(),serviceInfo.getInfo().get("url").toString(),serviceInfo.getInfo().get("obj"));
				break;
			case ASYNC_COMMIT:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberSet").toString());
				break;
			case CANCEL:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberSet").toString());
				break;
			case WHOLE_SUCCESS_STRONG_ACK:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString());
				break;
			case WHOLE_FAIL_STRONG_ACK:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString());
				break;
			case SUB_SUCCESS:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberSet").toString(),serviceInfo.getInfo().get("memberId").toString());
				break;
			case SUB_FAIL:
				sendMsg(serviceInfo.getId(),serviceInfo.getAction(),serviceInfo.getInfo().get("groupId").toString(),serviceInfo.getInfo().get("groupMemberSet").toString());
				break;
			default:
				break;
		}
	}

	private void sendMsg(String id,ActionType action,String groupId){
		MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
		JSONObject info = new JSONObject();
		info.put("groupId",groupId);
		builder.setInfo(info.toJSONString());
		builder.setId(id);
		builder.setAction(action);
		MessageProto.Message message = builder.build();
		System.out.println("Send transaction message:\n" + message);
		ctx.writeAndFlush(message);
	}

	private void sendMsg(String id,ActionType action,String groupId, String groupMemberId, String url,Object obj){
		MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
		JSONObject info = new JSONObject();
		info.put("groupId",groupId);
		info.put("groupMemberId",groupMemberId);
		info.put("url",url);
		info.put("obj",obj);
		builder.setInfo(info.toJSONString());
		builder.setId(id);
		builder.setAction(action);
		MessageProto.Message message = builder.build();
		System.out.println("Send transaction message:\n" + message);
		ctx.writeAndFlush(message);
	}

	private void sendMsg(String id,ActionType action,String groupId, String groupMemberId, Method method,Object[] args){
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

	private void sendMsg(String id,ActionType action,String groupId, String groupMemberSet){
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

	private void sendMsg(String id,ActionType action,String groupId, String groupMemberSet,String memberId){
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
}
