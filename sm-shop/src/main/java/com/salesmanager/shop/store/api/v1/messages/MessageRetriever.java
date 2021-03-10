package com.salesmanager.shop.store.api.v1.messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.message.MessageRepository;
import com.salesmanager.core.business.services.user.UserService;
import com.salesmanager.core.model.message.Message;
import com.salesmanager.core.model.user.User;
import com.salesmanager.shop.admin.model.authtoken.AuthToken;
import com.salesmanager.shop.admin.model.message.RecievedMessage;
import com.salesmanager.shop.admin.model.message.UserName;

//To retrieve the latest message when the user is online!
//In case this throws error, please modify accordingly
public class MessageRetriever {


	//private UserService userService;
	
	    
	 @Inject
	 	MessageRepository messageRepository;
	 
	 private static final Logger LOGGER = LoggerFactory.getLogger(MessageRetriever.class);
	    
	public boolean retrieveLatestMessages(UserService userService)
	{
		
		AuthToken auth_token = com.salesmanager.shop.admin.controller.AdminController.getAuthToken();
 		//Retrieve the latest messages
 		//Generate Encoded string
		String encode = com.salesmanager.shop.admin.controller.AdminController.generateEncodedString(auth_token);
        System.out.println(encode);
        
        //Create the JSON payload to be sent
        JSONObject encodeString = new JSONObject();
        encodeString.put("encoded_string", encode);
        System.out.println(encodeString.toString());
        
        //Retrive the message using apache HttpClient
        HttpEntityEnclosingRequestBase httpEntity = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
              return HttpMethod.GET.name();
            }
          };
          httpEntity.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.getType());
          httpEntity.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.getType());
          
          BufferedReader bufferedReader = null;
          //Trying to retrieve the message
          try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
        	  UserName email = com.salesmanager.shop.admin.controller.AdminController.getUserName();
        	  String uri = "http://13.233.153.31:9000/gc/messageservice/users/"+email.getAdminEmail();
        	  System.out.println(uri);
            URI authenticationUri = new URI(uri);
            httpEntity.setURI(authenticationUri);
            httpEntity.setEntity(new StringEntity(encodeString.toString(), ContentType.APPLICATION_JSON));
            HttpResponse response2 = client.execute(httpEntity);
            
            //We'll be receiving the JSON list as string here
            bufferedReader = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
            String responseBody = bufferedReader.readLine();
            bufferedReader.close();
            System.out.println(responseBody);
            
            //Converting the received string into JSON list
            List<RecievedMessage> msg = new ArrayList<RecievedMessage>();
            ObjectMapper mapper = new ObjectMapper();
            msg = mapper.readValue(responseBody,  new TypeReference<List<RecievedMessage>>(){});
           // LOGGER.info(""+msg.size());
            if(msg.size()==0)return false;
            //Getting the list of users
            List<User> users=userService.listUser();
          //  LOGGER.info(""+users.size());
            for(RecievedMessage m : msg) {
            	if(m.getAck()==(-1)) {
            		long messageId = m.getMsgId();
                	String messageTxt = m.getMsg();
                	System.out.println("\n"+messageTxt+"\n");
                	User fromUser = null;
                	User toUser = null;
                	int ack = m.getAck();
                	int type = m.getType();
                	for(User u : users) {
                		if(u.getAdminEmail().equalsIgnoreCase(m.getFromId())) {
                			fromUser = u;
                		}
                		else if(u.getAdminEmail().equalsIgnoreCase(m.getToId())){
                			toUser = u;
                		}
                	}
                	//Storing the message into the database
                	Message newDBMessage=new Message(messageId,fromUser, messageTxt, toUser, ack, type);
                    messageRepository.save(newDBMessage);
            	}
            }
          } catch (URISyntaxException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          } catch (ServiceException e) {
			// TODO Auto-generated catch block
        	  
			e.printStackTrace();
		}
          return true;
	}

}
