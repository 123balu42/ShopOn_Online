package com.salesmanager.shop.store.api.v1.messages;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.message.MessageRepository;
import com.salesmanager.core.business.services.user.UserService;
import com.salesmanager.core.model.message.Message;
import com.salesmanager.core.model.user.User;
import com.salesmanager.shop.model.messages.MessagePayload;
import com.salesmanager.shop.model.messages.Payload;
import com.salesmanager.shop.model.messages.UserPayload;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.salesmanager.shop.admin.model.authtoken.AuthToken;
import com.salesmanager.shop.admin.model.message.MessageInfo;

@RestController
@RequestMapping("api/v1/messages")
public class RestControllerMessages {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestControllerMessages.class);
	
	@Inject
	MessageRepository messageRepository;
	
	@Inject
	private UserService userService;
	
	@GetMapping(value = { "/chats/user/{id}" },produces = MediaType.APPLICATION_JSON)
	public List<UserPayload> getAllChats(@PathVariable Long id)
	{
		List<String[]> users=messageRepository.getAllActiveChattersFor(id);
		List<UserPayload>  usersPayload=new ArrayList<>();
		for(String[] oneuser :users)
		{
			usersPayload.add(new UserPayload(oneuser));
		}
		LOGGER.info(userService.getById(id).getAdminEmail()+"");
		return usersPayload;
	}
	//@RequestParam(value = "id1", defaultValue = "101") Long id1,@RequestParam(value = "id2", defaultValue = "102")Long id2
	//List<Message>
	//messageRepository.getAllMessagesBetween(id1, id2);
	
	@PostMapping(value = { "/chats/users/" },consumes = MediaType.APPLICATION_JSON,produces = MediaType.APPLICATION_JSON)
	public List<MessagePayload> getAllMessagesFor(@RequestBody Payload payload)
	{
		
		LOGGER.info(payload.getEmail()+"   "+payload.getId());
		Long id2=0L;
		try {
		List<User> users=userService.listUser();
		for(User a:users )
		{
			if(a.getAdminEmail().equals(payload.getEmail()))
			{
				id2=a.getId();
				LOGGER.info(id2+" kiska hai "+payload.getEmail());
			}
		}
		}
		catch(Exception e)
		{
			LOGGER.info("Some error "+e.getMessage());
		}
		LOGGER.info(id2+"   and  "+payload.getId());
		List<Message> messages=messageRepository.getAllMessagesBetween(id2,payload.getId());
		//List<MessagePayload>
		List<MessagePayload> mp=new ArrayList<>();
		for(Message a:messages )
		{
			LOGGER.info(a.getMessage());
			mp.add(new MessagePayload(a));
		}
		messageRepository.setMessageSeenFor(id2,payload.getId());
	//	new Messagepayload().setByMessage(messages.get(0));
		return mp;
	}
	
	
	// Rest API call to send message
	@PostMapping(value = { "/sendMessage/" },consumes = MediaType.APPLICATION_JSON,produces = MediaType.APPLICATION_JSON)
	public boolean sendMessage(@RequestBody MessageInfo messageInfo) throws ServiceException {
		System.out.println("In Send Message Rest Controller!!");
		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		AuthToken auth_token = com.salesmanager.shop.admin.controller.AdminController.getAuthToken();
		System.out.println("REST Auth Token: " + auth_token.getAuth_token());
		
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		postHeaders.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
		
		
		// define message object to be saved in both dbs
		
		//message
		String messageTxt = messageInfo.getMessageTxt();
	    System.out.println("Message is: " + messageTxt);
	    
	    //sender and reciever mails
	    String senderEmail = messageInfo.getSender();   
		String receiverEmail = messageInfo.getReceiver();
		System.out.println("Sender Email: " + senderEmail);
		System.out.println("Receiver Email: " + receiverEmail);
		 
		//sender and reciever users
		User fromUser=null;
		User toUser=null;
		try {
			List<User> users=userService.listUser();
			for(User a:users )
			{
				if(a.getAdminEmail().equals(senderEmail))
				{
					fromUser =a;
				}else if(a.getAdminEmail().equals(receiverEmail))
				{
					toUser=a;
				}
			}
			}
			catch(Exception e)
			{
				LOGGER.info("Some error "+e.getMessage());
			}
	//	User fromUser = userService.getByUserName(senderEmail);
	//	User toUser = userService.getByUserName(receiverEmail);
		if(fromUser==null || toUser==null)
			return false;
		
		LOGGER.info(fromUser.getAdminName()+"   "+toUser.getAdminName());
		
		//
		Message newDBMessage=new Message();
		messageRepository.save(newDBMessage);
        Long messageId=newDBMessage.getMsgId();
        LOGGER.info("New Id generated "+messageId);
        newDBMessage=new Message(messageId,fromUser, messageTxt, toUser, 0, 0);
        messageRepository.save(newDBMessage);
		
	    
	    String messageUri = "http://13.233.153.31:9000/gc/messageservice/users/";
		JSONObject newMessage = new JSONObject();
		newMessage.put("MsgId", messageId);
		newMessage.put("ToId", receiverEmail);
		newMessage.put("FromId", senderEmail);
		newMessage.put("Msg", messageTxt);
		newMessage.put("Ack", 0);
		newMessage.put("Type", 0);
		
		String sender_encoded_string = com.salesmanager.shop.admin.controller.AdminController.generateEncodedString(auth_token);
		System.out.println("Sender Encoded String: " + sender_encoded_string);
		newMessage.put("encoded_string", sender_encoded_string);
		System.out.println(newMessage.toString());
		HttpEntity<String> newMessageRequest = new HttpEntity<String>(newMessage.toString(), postHeaders);
		ResponseEntity<String> newMessageResponse = restTemplate.exchange(messageUri, HttpMethod.POST, newMessageRequest, String.class);
		System.out.println(newMessageResponse.getStatusCode());
		
		return true;
	}
	
	//Calls the retrieveLatestMessages() function in MessageRetriever class, to retrieve the latest message
	@GetMapping(value = { "/retreivelatest/" })
	public boolean getLatestMessages()
	{
		MessageRetriever receive = new MessageRetriever();
		return receive.retrieveLatestMessages(userService);
	}
	
}