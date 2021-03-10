package com.salesmanager.core.model.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.salesmanager.core.model.user.User;


//@SqlResultSetMapping(
//	    name = "allMessages",
//	    classes = @ConstructorResult(
//	            targetClass = MessageDetails.class,
//	            columns = {
//	                    @ColumnResult(name = "msgId",type=Long.class),
//	                    @ColumnResult(name = "Msg",type=String.class),
//	                    @ColumnResult(name = "ack",type=Integer.class),
//	                    @ColumnResult(name = "type",type=Integer.class),
//	                    @ColumnResult(name = "USER_ID",type=Long.class),
//	                    @ColumnResult(name = "participantType",type=Integer.class)
//	            }
//	    )
//	)
//@NamedNativeQuery(name="Message.allMessages",resultClass=MessageDetails.class,resultSetMapping ="allMessages",
//query="select m.msgId as msgId,msg,ack,m.type as type,u.USER_ID,u.type as participantType from Messages m,message_user u where m.msgId=u.msgId and m.msgId in (select distinct msgId from message_user where user_id=?1) and user_id!=?1")
@Entity
@Table(name="messages")
public class Message {
	
	@Id
	@Column(name="msgId", unique=true, nullable=false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "MESSAGE_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long msgId;
	
	@ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "message_User", 
             joinColumns = { @JoinColumn(name = "msgId") }, 
             inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
	@OrderColumn(name="type")
	//private HashMap<ParticipantType,com.salesmanager.core.model.user.User> users=new HashMap<>();
	List<com.salesmanager.core.model.user.User> users=new ArrayList<>();
		
	@Column(length=500)
	private String Msg;
	
	private int ack;
	
	private int type;
	
	
	
	
	
	public Message()
	{
		
	}
	public Message(Long msgId, com.salesmanager.core.model.user.User fromUser, String msg, com.salesmanager.core.model.user.User toUser,int ack, int type) {
		super();
		this.msgId = msgId;
	//	users.put(ParticipantType.SENDER,fromUser);
	//	users.put(ParticipantType.RECIEVER,toUser);
		users.add(fromUser);
		users.add(toUser);
		Msg = msg;
		this.ack = ack;
		this.type = type;
	}

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	
	
	public void setSender(com.salesmanager.core.model.user.User user)
	{
		users.set(0, user);
		
	}
	public void setReciever(com.salesmanager.core.model.user.User user)
	{
		users.set(1,user);
	}
	
	
	public com.salesmanager.core.model.user.User getSender()
	{
		return users.get(0);
	}
	
	public com.salesmanager.core.model.user.User getReciever()
	{
		return users.get(1);
	}
		
	public List<com.salesmanager.core.model.user.User> getUsers() {
		return users;
	}
	public String getMessage() {
		return Msg;
	}

	public void setMessage(String msg) {
		Msg = msg;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}
