package com.salesmanager.shop.admin.controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;


import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.message.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.user.UserService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.message.Message;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.user.User;
import com.salesmanager.shop.admin.model.authtoken.AuthToken;
import com.salesmanager.shop.admin.model.message.RecievedMessage;
import com.salesmanager.shop.admin.model.message.UserName;
import com.salesmanager.shop.constants.Constants;

@CrossOrigin(origins="*")
@Controller
public class AdminController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
	
    @Inject
    CountryService countryService;
    
    @Inject
    UserService userService;
    
    @Inject
	MessageRepository messageRepository;
    
    private static AuthToken auth_token;
	private static UserName userName;
	static  {
		auth_token = new AuthToken("");
		userName = new UserName();
		System.out.println("static block executed !!");
		LOGGER.info("static block executed !!");
	}
	
	public static AuthToken getAuthToken() {
		return auth_token;
	}
	
	public static UserName getUserName() {
		return userName;
	}
    
	@PreAuthorize("hasRole('AUTH')")
    @RequestMapping(value={"/admin/home.html","/admin/"}, method=RequestMethod.GET)
    public String displayDashboard(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Language language = (Language)request.getAttribute("LANGUAGE");
        
        //display menu
        Map<String,String> activeMenus = new HashMap<String,String>();
        activeMenus.put("home", "home");
        
        model.addAttribute("activeMenus",activeMenus);
        
        
        //get store information
        MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
        
        Map<String,Country> countries = countryService.getCountriesMap(language);
        
        Country storeCountry = store.getCountry();
        Country country = countries.get(storeCountry.getIsoCode());
        
        String sCurrentUser = request.getRemoteUser();
        User currentUser = userService.getByUserName(sCurrentUser);
        
     // Logging in to cpp api with the admin details
 		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
 		RestTemplate restTemplate = new RestTemplate(requestFactory);
 		ObjectMapper objectMapper = new ObjectMapper();
 		String loginUri = "http://13.233.153.31:9000/gc/userservice/login/";
 		HttpHeaders postHeaders = new HttpHeaders();
 		postHeaders.setContentType(MediaType.APPLICATION_JSON);
 		postHeaders.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
 		JSONObject newUser = new JSONObject();
 		newUser.put("email", currentUser.getAdminEmail());
 		newUser.put("password", "IamBatman");
 		LOGGER.info(newUser.toString());
 		HttpEntity<String> newUserRequest = new HttpEntity<String>(newUser.toString(), postHeaders);
 		ResponseEntity<String> newUserResponse = null;
		try {
			newUserResponse = restTemplate.exchange(loginUri, HttpMethod.POST, newUserRequest, String.class);
		} catch (Exception e1) {
			// User does not exist in the cpp database
			LOGGER.info(currentUser.getAdminEmail() + "does not exist in cpp database. Trying to create one");
			String createUserUri = "http://13.233.153.31:9000/gc/userservice/users/";
			JSONObject newEntry = new JSONObject();
			newEntry.put("name", currentUser.getFirstName());
			newEntry.put("email", currentUser.getAdminEmail());
			newEntry.put("password", "IamBatman");
			newEntry.put("mobileNumber", "9876543210");
	 		LOGGER.info(newEntry.toString());
	 		HttpEntity<String> newCreateUserRequest = new HttpEntity<String>(newEntry.toString(), postHeaders);
	 		try {
				ResponseEntity<String> newCreateUserResponse = restTemplate.exchange(createUserUri, HttpMethod.POST, newCreateUserRequest, String.class);
				LOGGER.info(newCreateUserResponse.getStatusCode() + "! User: " + currentUser.getAdminEmail() + " created Successfully!");
			} catch (Exception e) {
				LOGGER.info("New User Could not be created");;
			}	
		}
		
 		if (newUserResponse != null) {
			try {
				auth_token = objectMapper.readValue(newUserResponse.getBody(), AuthToken.class);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} 
		}
		LOGGER.info(currentUser.getAdminEmail() + " auth_token: " + auth_token.getAuth_token());
        
		//Storing the current user email, to be used by the 
 		userName.setAdminEmail(currentUser.getAdminEmail());
 		
 		//After login retrieve all messages received for current user received while away
 		//Generate Encoded string
 		String encode = generateEncodedString(auth_token);
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
        	  String uri = "http://13.233.153.31:9000/gc/messageservice/users/"+currentUser.getAdminEmail();
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
            
            //Getting the list of users
            List<User> users=userService.listUser();
            for(RecievedMessage m : msg) {
            	if(m.getAck()==(-1)) {
            		long messageId = m.getMsgId();
                	String messageTxt = m.getMsg();
                	System.out.println("\n"+messageTxt+"\n");
                	User fromUser = null;
                	User toUser = currentUser;
                	int ack = m.getAck();
                	int type = m.getType();
                	for(User u : users) {
                		if(u.getAdminEmail().equalsIgnoreCase(m.getFromId())) {
                			fromUser = u;
                			break;
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
          }
		
        model.addAttribute("store", store);
        model.addAttribute("country", country);
        model.addAttribute("user", currentUser);
        //get last 10 orders
        //OrderCriteria orderCriteria = new OrderCriteria();
        //orderCriteria.setMaxCount(10);
        //orderCriteria.setOrderBy(CriteriaOrderBy.DESC);
        
        return ControllerConstants.Tiles.adminDashboard;
    }
   
    private static String getIsoDate() {
        Instant instant = Instant.now();
        String isoTimeinMillis = instant.toString();
        String isoTimeinNanos = isoTimeinMillis.substring(0, isoTimeinMillis.length()-1) + "500";
        return isoTimeinNanos;
    }
   
    public static String generateEncodedString(AuthToken token) {
        String str = token.getAuth_token() + getIsoDate();
        return Base64.getEncoder().encodeToString(str.getBytes());
    }
    
    @RequestMapping( value=Constants.ADMIN_URI , method=RequestMethod.GET)
    public String displayStoreLanding(HttpServletRequest request, HttpServletResponse response) {

 

        return "redirect:" + Constants.ADMIN_URI + Constants.SLASH;
    }

}