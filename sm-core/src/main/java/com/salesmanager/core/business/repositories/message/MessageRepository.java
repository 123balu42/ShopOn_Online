package com.salesmanager.core.business.repositories.message;

import java.sql.ResultSet;
import java.util.List;

import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.salesmanager.core.model.message.Message;
//import com.salesmanager.core.model.message.MessageDetails;
import com.salesmanager.core.model.user.User;


@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	
		
	@Query("select m from Message m where id = ?1 ")
	Message getMessageById(Long msgId);
		
	//@Query(nativeQuery=true,name="Message.allMessages")  
	//@Query(value="select m.* from Messages m,message_user u where m.msgId=u.msgId and m.msgId in (select distinct msgId from message_user where user_id=?1) and user_id!=?1",nativeQuery=true)
	
	
	@Query(value="select m.* from Messages m,message_user u where m.msgId=u.msgId and m.msgId in (select distinct msgId from message_user where user_id=?1) and user_id!=?1",nativeQuery=true)
	List<Message> getAllMessagesFor(Long user1);
	
	
	@Query(value="select m.* from Messages m,message_user u where m.msgId=u.msgId and m.msgId in (  select distinct msgId from message_user where user_id=?2 and msgId in (select distinct msgId from message_user where user_id=?1 ) )and user_id!=?1",nativeQuery=true)
	List<Message> getAllMessagesBetween(Long user1,Long user2);
	//@Query(value="select m.* from Messages m,message_user u where m.msgId=u.msgId and m.msgId in (select distinct msgId from message_user where user_id=?1) and user_id!=?1",nativeQuery=true)
	
	@Query(value="Select distinct user_id from message_user where msgId in (select distinct msgId from message_user where user_id=?1)",nativeQuery=true)
	List<Long> getAllActiveChatsFor(Long id) ;
	
//	@Query(value="Select distinct ADMIN_NAME from message_user m,user u where m.user_id=u.user_id and msgId in (select distinct msgId from message_user where user_id=?1) and u.user_id!=?1",nativeQuery=true)
//	List<String> getAllActiveChattersFor(Long id) ;
	
	@Query(value="Select distinct ADMIN_NAME,ADMIN_EMAIL from message_user m,user u where m.user_id=u.user_id and msgId in (select distinct msgId from message_user where user_id=?1) and u.user_id!=?1",nativeQuery=true)
	List<String[]> getAllActiveChattersFor(Long id) ;
	
//	@Query(value="select SEQ_COUNT from sm_sequencer where SEQ_NAME='MESSAGE_SEQ_NEXT_VAL';  ",nativeQuery=true)
//	Long getNewMessageId();
	@Transactional
	@Modifying
	@Query(value="update Messages set ack=2 where msgId in(  select distinct msgId from message_user where user_id=?2 and msgId in (select distinct msgId from message_user where user_id=?1 ) ) ",nativeQuery=true)
	void setMessageSeenFor(Long user1,Long user2);
	
}
