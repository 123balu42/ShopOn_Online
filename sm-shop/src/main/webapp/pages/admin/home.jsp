<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ page session="false" %>

<style>
/* Button used to open the chat form - fixed at the bottom of the page */
.open-button {
  background-color: #555;
  color: white;
  padding: 16px 20px;
  border: none;
  cursor: pointer;
  opacity: 0.8;
  position: fixed;
  bottom: 23px;
  right: 28px;
  width: 250px;
}
/* The popup chat - hidden by default */
.chat-popup {
  background: white;
  display: none;
  position: fixed;
  bottom: 0;
  right: 15px;
  border: 3px solid white;
  border-radius: 10px 10px 0px 0px;
  width: 20%;
    z-index: 9;
    min-width: 200px;
    min-height: 400px;
  box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
}
.heading{
 background-color: white;
 border-radius: 10px 10px 0px 0px;
 z-index: -1;
}
/* Add styles to the form container */
.form-container {
  /* width: 250px; */
  padding: 10px;
  background-color: white;
  max-height: 350px;
  min-height: 350px;
  overflow: auto;
}
/* Set a style for the submit/send button */
.form-container .butn {
  background-color: #4CAF50;
  color: white;
  padding: 4px 5px;
  border: none;
  cursor: pointer;
  /* width: 20%; */
  margin-bottom:10px;
  opacity: 0.8;
}
/* Add a red background color to the cancel button */
.form-container .cancel {
  background-color: red;
}
/* Add some hover effects to buttons */
.form-container .butn:hover, .open-button:hover {
  opacity: 1;
}
.form-container .input {
  width: 70%;
}
.form-container .table {
  width: 100%;
}
.form-container::-webkit-scrollbar {
  display: none;
}
.close {
  color: white;
  float: right;
  font-size: 50px;
  font-weight: bold;
  padding: 10px 20px;
  z-index:10;
}
.close:hover,
.close:focus {
  color: white;
  text-decoration: none;
  cursor: pointer;
  font-weight: 700;
}
.back {
  background-color:white;
  color: #c1c1c1;
  float: right;
  font-size: 50px;
  font-weight: bold;
  padding: 10px 20px;
  z-index:10;
  border: none;
}
#newUserLabel
{
display:none;
text-align:center
}
#newUsernames
{
display:none;
}
.back:hover,
.back:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
  font-weight: 700;
}
.input
{
	height:auto;
}
#allChats
{
height: 250px;
overflow: scroll;
}
#allChats::-webkit-scrollbar {
  display: none;
}
.msgDivL
{
direction:ltr;
padding-top:2px;
padding-bottom:2px;
}
.msgDivR
{
padding-top:2px;
padding-bottom:2px;
direction:rtl;
}
.messageR
{
padding: 10px;
border-color: rgb(0,84,194);
background: rgb(0,123,255);
border-radius: 10px;
color: white;
max-width: 70%;
    overflow-wrap: anywhere;
display:inline-block;
}
.messageL
{
background: rgb(108,117,125);
}
/* width: 70%; */ 


.textareaClass
{
resize:none;
margin-right:10px;
wdth:-webkit-fill-available;
}
.textareaClass::-webkit-scrollbar {
  display: none;
}

.newChatAlert
{
text-align: center;
width: auto;
height: 270px;
line-height: 240px;
}
.newMessagesAlert
{
text-align: center;
width: auto;
}
.msgTypeArea
{
display: flex;
position: absolute;
bottom: 0;
left: 2px;
width: 95%;
margin: 0;
padding: 0 2.5%;
}
.msgSendBtn
{
height: max-content; 							 
min-width: 22%;
}

</style>


<div class="tabbable">

					<jsp:include page="/common/adminTabs.jsp" />
					<div id="messages" class="alert alert-info" style="display:none">
					</div>

					<div class="box">
						<span class="box-title">
						<p><s:message code="label.store.information" text="Store information" /></p>
						</span>
						
						<p>
						<address>
							<strong><c:out value="${store.storename}"/></strong><br/>
							<c:if test="${not empty store.storeaddress}">
								<c:out value="${store.storeaddress}"/><br/>
							</c:if>
							<c:if test="${not empty store.storecity}">
								<c:out value="${store.storecity}"/>,
							</c:if>
							<c:choose>
							<c:when test="${not empty store.zone}">
								<c:out value="${store.zone.code}"/>,
							</c:when>
							<c:otherwise>
								<c:if test="${not empty store.storestateprovince}">
									<c:out value="${store.storestateprovince}"/>,
								</c:if>
							</c:otherwise>
							</c:choose>
							<c:if test="${not empty store.storepostalcode}">
								<c:out value="${store.storepostalcode}"/>
							</c:if>
							<br/><c:out value="${country.name}"/>
							<c:if test="${not empty store.storephone}">
								<br/><c:out value="${store.storephone}"/>
							</c:if>
						</address>

						
						</p>
						<p>
							<i class="icon-user"></i> 
							<sec:authentication property="principal.username" /><br/>
							<i class="icon-calendar"></i> <s:message code="label.profile.lastaccess" text="Last access"/>: <fmt:formatDate type="both" dateStyle="long" value="${user.lastAccess}" />
						</p>
						
						
					</div>
					
				<sec:authorize access="hasAnyRole('ADMIN', 'SUPERADMIN', 'ADMIN_ORDER')">
					
					<br/>
					<h3><s:message code="label.order.recent" text="Recent orders"/></h3>
					<br/><br/>
					
				 <!-- Listing grid include -->
				 <c:set value="/admin/orders/paging.html?_endRow=10" var="pagingUrl" scope="request"/>
				 <c:set value="/admin/orders/remove.html" var="removeUrl" scope="request"/>
				 <c:set value="/admin/orders/editOrder.html" var="editUrl" scope="request"/>
				 <c:set value="/admin/orders/list.html" var="afterRemoveUrl" scope="request"/>
				 <c:set var="entityId" value="orderId" scope="request"/>
				 <c:set var="componentTitleKey" value="label.order.title" scope="request"/>
				 <c:set var="gridHeader" value="/pages/admin/orders/orders-gridHeader.jsp" scope="request"/>
				 <c:set var="canRemoveEntry" value="false" scope="request"/>

            	 <jsp:include page="/pages/admin/components/list.jsp"></jsp:include> 
				 <!-- End listing grid include -->
				 
				 </sec:authorize>
				 
				 
							<!-- Chat box Application Side -->


 <button class="open-button" onclick="openForm()">Chat Box</button>
				 <div class="chat-popup" id="myForm">
				    <div class='heading'>
				    	<img src="<c:url value="/resources/img/shopizer_small.jpg" />"/>
				    	<span class="close">&times;</span>
				    </div> 
				 	<div  class="form-container">
				 	    <input class="input"  autocomplete="off" id="email"
				 	     type="text" name="Name" placeholder="Enter name to search...." oninput="gocheckemail(this.value)">
				 	     
				 	    
				 	    
				 	    <button class="butn" onclick="search()">Search</button>
				 	    <p class= "table" id="demo"></p> 
				 	    <p class= "table" id="newUserLabel">Chat other Admins in the database</p> 
				 	    <p class= "table"  id="newUsernames"></p>
        			</div> 
    			 </div>
    			 <div class="chat-popup" id="myChat">
    			 	<img src="<c:url value="/resources/img/shopizer_small.jpg" />"/>
    			 	<button class="back" onclick="closeChat()">&larr;</button>
				 	<div id="chatArea" action="#" class="form-container"> 
				 		<h3 id="name"></h3>
				 		<p>Chat History</p>
				 		<div id="allChats">
    						<!--  Empty div for messages-->
    						</div>
    						
    						<!-- bottom Div for the new message -->
				 		<div  class="msgTypeArea">
				 		<textarea class="input textareaClass" id="messageTxt" oninput="updateChat"
				 		name="messageTxt" style="resize:none;margin-right:10px;wdth:-webkit-fill-available;"
				 		 placeholder="Enter text to send...."></textarea>
				 		<p id="hiddenfield"><input type="hidden" id="sender" name="sender"
				 		 value="<c:out value="${user.adminEmail}"/>"></p>
	    				<button class="butn msgSendBtn"  onclick="sendRestMessage()">Send</button>
	    				
	    				</div>
        			</div> 
    			 </div>  
   
   				

</div>
<script>

var alladmin;
var contactedadmin;
var currentUrl = location.protocol + "//" + location.hostname + ":" + location.port;
console.log(currentUrl);
getAllUsers();
window.setInterval(updateChat,2000);
//document.getElementById('email').addEventListener('oninput',
 /* document.getElementById('email').oninput=function() {
	console.log("checking ");
    var optionFound = false;
    //  console.log(JSON.parse(contactedadmin))
    // Determine whether an option exists with the current value of the input.
    for (var j = 0; j < contactedadmin.length; j++) {
        console.log(contactedadmin[j].indexOf(this.value)+"     "+contactedadmin[j]+"     "+this.value)
        if ( contactedadmin[j].toLowerCase().indexOf(this.value.toLowerCase())!=-1) {
            console.log("mil gaya");
            optionFound = true;
            break;
        }
    } 
    if(optionFound==true)
        {console.log("hai user");
    	document.getElementById("newUserLabel").style.display = "none";
    	document.getElementById("newUsernames").style.display = "none";
    	
        }
    else
        {console.log("nahi hai user");
    	document.getElementById("newUserLabel").style.display = "block";
    	document.getElementById("newUsernames").style.display = "block";
        }
        
    
 } */
 
// code for interactive form on input change in the search bar  document.getElementById('email').oninput=
function gocheckemail(emailvalue){
		/* console.log("checking ");
		console.log(window.alladmin)
		console.log(contactedadmin) */
		txt='';
	    var optionFound = false;
	    //  console.log(JSON.parse(contactedadmin))
	    // Determine whether an option exists with the current value of the input.
	    txt+='<table style="width:100%">'	
	    for ( j = 0; j < contactedadmin.length; j++) {
	    	/* console.log(j)
	    	console.log(typeof(contactedadmin[j].name))
	    	console.log(contactedadmin[j]) */
	    	
	        console.log(window.contactedadmin[j].name.indexOf(emailvalue)+"     "+window.contactedadmin[j].name+"     "+emailvalue)
	        if ( window.contactedadmin[j].name.toLowerCase().indexOf(emailvalue.toLowerCase())!=-1) {
	            //console.log("mil gaya");
	            optionFound = true;
	            txt += '<tr><td onclick="openChat('+ "'" + window.contactedadmin[j].email + "'" +')">' + window.contactedadmin[j].name  + '</td></tr>';
	            
	        }
	    } 
	    txt += "</table>"  
	    //	txt.replace(this.value.toLowerCase(),"<b>"+this.value.toLowerCase()+"</b>") 
	    if(optionFound==true)
	        {//console.log("hai user");
	    	document.getElementById("newUserLabel").style.display = "none";
	    	document.getElementById("newUsernames").style.display = "none";
	    	document.getElementById("demo").innerHTML = txt; 
	        }
	    else
	        {//console.log("nahi hai user");
	        newtxt='';
	    	document.getElementById("newUserLabel").style.display = "block";
	    	document.getElementById("newUsernames").style.display = "block";
	    	newtxt+='<table style="width:100%">'
	    	for( j=0;j<window.alladmin.length;j++)
		    	{
		    	
	    		console.log(window.alladmin[j].name.toLowerCase().indexOf(emailvalue.toLowerCase())+"     "+window.alladmin[j].name+"     "+emailvalue)
		        if ( window.alladmin[j].name.toLowerCase().indexOf(emailvalue.toLowerCase())!=-1) {
		            //console.log("mil gaya");
		            newtxt += '<tr><td onclick="openChat('+ "'" + window.alladmin[j].email + "'" +')">' + window.alladmin[j].name + '<br>'+window.alladmin[j].email+'</tr>'
		          
		        }
		    	
		    	}
	    	newtxt+='</table>'  
		    //	newtxt.replace(this.value.toLowerCase(),"<b>"+this.value.toLowerCase()+"</b>")
	    		document.getElementById("newUsernames").innerHTML = newtxt; 
	    	document.getElementById("demo").innerHTML="";
	        }
	        
	    
	 }
//);
 
//Populate the corresponding javascript object.
function openForm() {
	
	document.getElementById("myForm").style.display = "block";
	
}
var span = document.getElementsByClassName("close")[0];
span.onclick = function() {
	  document.getElementById("myForm").style.display = "none";
}
//button click for search button
function search(){
	console.log(document.getElementById("email").value);
	if(document.getElementById("email").value!=''){
		var request = new XMLHttpRequest()
		//Open a new connection, using the GET request on the URL endpoint
		request.open('GET', 'https://cors-anywhere.herokuapp.com/http://13.233.153.31:9000/gc/userservice/users/'+document.getElementById("email").value, true);
		request.setRequestHeader("Access-Control-Allow-Origin","*");
		request.onload = function () {
			if(request.status==200){
				document.getElementById("myForm").style.display = "none";
				document.getElementById("myChat").style.display = "block";
				var data = JSON.parse(this.response);
				document.getElementById("name").innerHTML = data.email;
				setHiddenField(data.email);
			}
			else{
				alert(document.getElementById("email").value+" is not an admin");
			}
		}
		//Send request
		request.send()
	}
	else{
		alert("Enter email to search admin");
	}
}	
// button click to open chat
function openChat(chatMail){
	
	document.getElementById("myForm").style.display = "none";
	document.getElementById("myChat").style.display = "block";
	document.getElementById("name").innerHTML = chatMail;
	setHiddenField(chatMail);
	getMessagesForChat(chatMail);
}

// Send message through RestController
function sendRestMessage(){
    var request = new XMLHttpRequest()
    var messageTxt = $("#messageTxt");
    var receiver = $("#receiver");
    var sender = $("#sender");
    var messageReceived;
  	var payload={
	    messageTxt: messageTxt.val(),
	    sender: sender.val(),
	    receiver: receiver.val()
    };
    console.log("In Send rest message");
    request.open('POST', currentUrl + '/api/v1/messages/sendMessage/', true);
    request.onload = function () {
        if(request.status == 200){
            var data = JSON.parse(this.response);
            console.log(data);
            openChat(receiver.val());
            document.getElementById("messageTxt").value="";
        }
        else{
            alert("Message could not be sent");
        }
    }
    request.setRequestHeader("Content-Type","application/json");
    request.send(JSON.stringify(payload));
}

// get all users on the cpp server
function getAllUsers()
{
	var i,txt='',mail='';
  	var request = new XMLHttpRequest()
	//Open a new connection, using the GET request on the URL endpoint
	request.open('GET', 'https://cors-anywhere.herokuapp.com/http://13.233.153.31:9000/gc/userservice/users/', true);
	request.setRequestHeader("Access-Control-Allow-Origin","*");
	
	request.onload = function () {
		if(request.status==200){
			window.alladmin=JSON.parse(this.response);
			showAllChats()
			 var admins = JSON.parse(this.response);
			var count = Object.keys(admins).length;
			txt+='<table style="width:100%">'
			for (i = 0; i < count; i++) {
				txt += '<tr><td onclick="openChat('+ "'" + window.alladmin[i].email + "'" +')">' + window.alladmin[i].name + '<br>'+window.alladmin[i].email+'</tr>'
			}
			txt+='</table>'  
			document.getElementById("newUsernames").innerHTML = txt; 
		}
	}
	//Send request
	request.send()
}
//to get all users, the current user chats to 
function showAllChats()
{
	console.log(window.alladmin[0].email)
	var id=<c:out value=' ${user.id }'></c:out>;
	console.log(id);
	var request = new XMLHttpRequest()
	//var id="50";
	//Open a new connection, using the GET request on the URL endpoint
	var txt='';
	request.open('GET', currentUrl + '/api/v1/messages/chats/user/'+id, true);
	//request.setRequestHeader("Access-Control-Allow-Origin","*");
	request.onload = function () {
		if(request.status==200){
			a=JSON.parse(request.responseText);
			contactedadmin=JSON.parse(request.responseText);
			console.log(a)
			console.log(a.length+" contactedadmin");
			txt+='<table style="width:100%">'
				/*  txt += '<tr><td  onclick="openChat('+ "'" + admins[0].email + "'" +')">' + a[0]  + '</td></tr>';
				 txt += '<tr><td  onclick="openChat('+ "'" + admins[1].email + "'" +')">' + a[1]  + '</td></tr>';
				 txt += '<tr><td  onclick="openChat('+ "'" + admins[2].email + "'" +')">' + a[2]  + '</td></tr>'; */
				for( i=0;i<a.length;i++ ) {
				   txt += '<tr><td onclick="openChat('+ "'" + a[i].email + "'" +')">' + a[i].name  + '</td></tr>';
				}
				txt += "</table>"    
				document.getElementById("demo").innerHTML = txt; 
		}
		else{
			alert(" BT");
		}
	}
	//Send request
	request.send()
}
//to get messages between two users 
	function getMessagesForChat(email)
	{
		var id=<c:out value=' ${user.id }'></c:out>;
		var request = new XMLHttpRequest()
		//var id="50";
		//Open a new connection, using the GET request on the URL endpoint
		var payload={"id":id,"email":email};
		console.log(payload);
		
		var txt='';
		
		request.open('POST', currentUrl + '/api/v1/messages/chats/users/', true);
		//request.setRequestHeader("Access-Control-Allow-Origin","*");
		request.onload = function () {
			if(request.status==200){
				a=JSON.parse(request.responseText);
				
				console.log(a.length);
				txt+='<div style="width:100%">'
					var newMessageAlertShown=0;
					/*  txt += '<tr><td  onclick="openChat('+ "'" + admins[0].email + "'" +')">' + a[0]  + '</td></tr>';
					 txt += '<tr><td  onclick="openChat('+ "'" + admins[1].email + "'" +')">' + a[1]  + '</td></tr>';
					 txt += '<tr><td  onclick="openChat('+ "'" + admins[2].email + "'" +')">' + a[2]  + '</td></tr>'; */
					for( i=0;i<a.length;i++ ) {
						
						//if(i%2==0)
						console.log(a[i].senderId+"   "+id)
						if(a[i].ack==-1&&newMessageAlertShown==0)
							{
							txt+="<p class='newMessageAlert'>New Messages</p>"
								newMessageAlertShown=1
							}
						
						
						if(a[i].senderId==id)
					   txt += '<div class="msgDivR"><div class="messageR">' + a[i].msg  + '</div></div>';
					   else
						   txt += '<div class="msgDivL"><div class="messageR messageL">' + a[i].msg  + '</div></div>';
 					}
						if(a.length==0)
							{
							txt+="<p class='newChatAlert'>Send a message to start chatting</p>"
							}
						
					txt += "</div>"    
					document.getElementById("allChats").innerHTML = txt; 
			}			
			else{
				alert(" BT");
			}	
		}
		//Send request
		request.setRequestHeader("Content-Type","application/json");
		request.send(JSON.stringify(payload))

	
		}
	
function closeChat(){
	document.getElementById("myForm").style.display = "block";
	document.getElementById("myChat").style.display = "none";
	
}

// Part of sending receiver email to the restController
function setHiddenField(emailId){
  console.log("in hidden field setter");
  var para, hiddenInput;
    para = document.getElementById("hiddenfield");
    console.log(para);
    if(document.getElementById("receiver") == null){
      hiddenInput = document.createElement('input');
      hiddenInput.type = 'hidden';
      hiddenInput.id = 'receiver';
      hiddenInput.name = 'receiver';
      hiddenInput.value = emailId;
      console.log(hiddenInput.value);
      para.appendChild(hiddenInput); 
      console.log(hiddenInput);
    }
    else{
      document.getElementById("receiver").value = emailId;
    }
}
var mousemove=0;

function updateChat()
{
	mousemove++;
	//if(mousemove<100)
	//	{return;
	///	}
	//else{
		mousemove=0;
	var request = new XMLHttpRequest()
	//var id="50";
	//Open a new connection, using the GET request on the URL endpoint
	
	
	
	
	
	request.open('GET', currentUrl + '/api/v1/messages/retreivelatest/', true);
	//request.setRequestHeader("Access-Control-Allow-Origin","*");
	request.onload = function () {
		if(request.status==200){
			a=JSON.parse(request.responseText);
			console.log(a);
			console.log(" new messages here");
			if(a==true)getMessagesForChat(document.getElementById("name").value);
			
		}
		else{
			//alert(" BT");
		}
	}
	//Send request
	request.send()
	//}
	}



</script>