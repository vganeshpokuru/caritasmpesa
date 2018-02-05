/**
 * Copyright 2015 Sachin Kulkarni
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mifosplatform.mpesa.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.mifosplatform.mpesa.domain.Mpesa;
import org.mifosplatform.mpesa.service.MpesaBridgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mpesa")
public class MifosMpesaController {
	
	private final Logger logger = LoggerFactory.getLogger(MifosMpesaController.class);
	private MpesaBridgeService mpesaBridgeService;
	
	@Value("${mpesausername}")
	private String mpesausername;
	
	@Value("${mpesapassword}")
	private String mpesapassword;
	
	@Autowired
	public MifosMpesaController(final MpesaBridgeService mpesaBridgeService) {
		super();
		this.mpesaBridgeService = mpesaBridgeService;
		
	}
   
	

	@RequestMapping(value = "/transactiondetails", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> storeTransactionDetails(@QueryParam("id") final Long id,@QueryParam("orig") final String orig,
			@QueryParam("dest") final String dest,@QueryParam("tstamp") final String tstamp,@QueryParam("text") final String text,@QueryParam("user")
			final String user,@QueryParam("pass") final String pass,@QueryParam("mpesa_code") final String mpesa_code, @QueryParam("mpesa_acc")
			final String mpesa_acc,@QueryParam("mpesa_msisdn") final String mpesa_msisdn,@QueryParam("mpesa_trx_date") final Date mpesa_trx_date,@QueryParam("mpesa_trx_time")
			final String mpesa_trx_time,@QueryParam("mpesa_amt") final BigDecimal mpesa_amt,@QueryParam("mpesa_sender") final String mpesa_sender){
		System.out.println("mpesa_code: " + mpesa_code);
		String responseMessage = "";
		  StringBuilder requestMsg = new StringBuilder();
		
		   requestMsg.append("transaction failed to following requested parameters  : ");
		   requestMsg.append("id : ");    requestMsg.append(id);
		   requestMsg.append(", orig: "); requestMsg.append(orig);
		   requestMsg.append(", dest :"); requestMsg.append(dest);
		   requestMsg.append(", tstamp: "); requestMsg.append(tstamp);
		   requestMsg.append(", text :"); requestMsg.append(text);
		   requestMsg.append(", user :"); requestMsg.append(user);
		   requestMsg.append(", Pass :");     requestMsg.append(pass);
		   requestMsg.append(", mpesa_code :"); requestMsg.append(mpesa_code);
		   requestMsg.append(", mpesa_acc :"); requestMsg.append(mpesa_acc);
		   requestMsg.append(", mpesa_msisdn : "); requestMsg.append(mpesa_msisdn);
		   requestMsg.append(", mpesa_trx_date :"); requestMsg.append(mpesa_trx_date);
		   requestMsg.append(", mpesa_trx_time :"); requestMsg.append(mpesa_trx_time);
		   requestMsg.append(", mpesa_amt :"); requestMsg.append(mpesa_amt);
		   requestMsg.append(", mpesa_sender: "); requestMsg.append(mpesa_sender);
		   
	       String request = requestMsg.toString();	   
		   
				   
		try{
			String time=" ";
            if(tstamp!=null){
            	String[] date=tstamp.split(" ");            
              if(date.length>=1){            
            	time=date[0];
            }
            }
			Long officeId=(long) 0;
			if(user.equalsIgnoreCase(mpesausername) && pass.equalsIgnoreCase(mpesapassword)){
				responseMessage = this.mpesaBridgeService.storeTransactionDetails(id,orig,dest,tstamp,text,user,pass,mpesa_code,mpesa_acc,
					mpesa_msisdn,mpesa_trx_date,time +" "+mpesa_trx_time,mpesa_amt,mpesa_sender,"PaidIn",officeId);
			}
			if(responseMessage.equalsIgnoreCase(mpesa_code)){
				return new ResponseEntity<String>("CONFLICT:" +responseMessage,HttpStatus.CONFLICT);
			}
		}catch(Exception e){
		//	logger.error("Exception " + e);
			
			logger.error(request, "Error is :" + e);
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(responseMessage,HttpStatus.OK);
		
	}
	@RequestMapping(value = "/transactiondetails", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> storeTransactionDetail(@QueryParam("id") final Long id,@QueryParam("orig") final String orig,
			@QueryParam("dest") final String dest,@QueryParam("tstamp") final String tstamp,@QueryParam("text") final String text,@QueryParam("user")
			final String user,@QueryParam("pass") final String pass,@QueryParam("mpesa_code") final String mpesa_code, @QueryParam("mpesa_acc")
			final String mpesa_acc,@QueryParam("mpesa_msisdn") final String mpesa_msisdn,@QueryParam("mpesa_trx_date") final Date mpesa_trx_date,@QueryParam("mpesa_trx_time")
			final String mpesa_trx_time,@QueryParam("mpesa_amt") final BigDecimal mpesa_amt,@QueryParam("mpesa_sender") final String mpesa_sender
			){
		String responseMessage = "";
		
		  StringBuilder requestMsg = new StringBuilder();
			
		   requestMsg.append("transaction failed to following requested parameters  : ");
		   requestMsg.append("id : ");    requestMsg.append(id);
		   requestMsg.append(", orig: "); requestMsg.append(orig);
		   requestMsg.append(", dest :"); requestMsg.append(dest);
		   requestMsg.append(", tstamp: "); requestMsg.append(tstamp);
		   requestMsg.append(", text :"); requestMsg.append(text);
		   requestMsg.append(", user :"); requestMsg.append(user);
		   requestMsg.append(", Pass :");     requestMsg.append(pass);
		   requestMsg.append(", mpesa_code :"); requestMsg.append(mpesa_code);
		   requestMsg.append(", mpesa_acc :"); requestMsg.append(mpesa_acc);
		   requestMsg.append(", mpesa_msisdn : "); requestMsg.append(mpesa_msisdn);
		   requestMsg.append(", mpesa_trx_date :"); requestMsg.append(mpesa_trx_date);
		   requestMsg.append(", mpesa_trx_time :"); requestMsg.append(mpesa_trx_time);
		   requestMsg.append(", mpesa_amt :"); requestMsg.append(mpesa_amt);
		   requestMsg.append(", mpesa_sender: "); requestMsg.append(mpesa_sender);
		   
	       String request = requestMsg.toString();	   
		   
		
		
		try{
			String time=" ";
			if(tstamp!=null){
            String[] date=tstamp.split(" ");        
                if(date.length>=1){            
            	time=date[0];
            }
			}
			Long officeId=(long) 0;
			if(user.equalsIgnoreCase(mpesausername) && pass.equalsIgnoreCase(mpesapassword)){
			responseMessage = this.mpesaBridgeService.storeTransactionDetails(id,orig,dest,tstamp,text,user,pass,mpesa_code,mpesa_acc,
					mpesa_msisdn,mpesa_trx_date,time+" "+mpesa_trx_time,mpesa_amt,mpesa_sender,"PaidIn",officeId);
			}
			if(responseMessage.equalsIgnoreCase(mpesa_code)){
				return new ResponseEntity<String>("CONFLICT:" +responseMessage,HttpStatus.CONFLICT);
			}
		}catch(Exception e){
			//logger.error("Exception " + e);
			logger.error(request, "Error is :" + e);
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(responseMessage,HttpStatus.OK);
		
	}
	@RequestMapping(value = "/transactiondetail", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> storeTransactionDetail(@QueryParam("id") final Long id,@QueryParam("orig") final String orig,
			@QueryParam("dest") final String dest,@QueryParam("tstamp") final String tstamp,@QueryParam("text") final String text,@QueryParam("user")
			final String user,@QueryParam("pass") final String pass,@QueryParam("mpesa_code") final String mpesa_code, @QueryParam("mpesa_acc")
			final String mpesa_acc,@QueryParam("mpesa_msisdn") final String mpesa_msisdn,@QueryParam("mpesa_trx_date") final Date mpesa_trx_date,@QueryParam("mpesa_trx_time")
			final String mpesa_trx_time,@QueryParam("mpesa_amt") final BigDecimal mpesa_amt,@QueryParam("mpesa_sender") final String mpesa_sender,
			@QueryParam("mpesa_trx_type") final String mpesa_trx_type,@QueryParam("office_Id") final Long office_Id){
		  String responseMessage = "";
		  StringBuilder requestMsg = new StringBuilder();
			
		   requestMsg.append("transaction failed to following requested parameters  : ");
		   requestMsg.append("id : ");    requestMsg.append(id);
		   requestMsg.append(", orig: "); requestMsg.append(orig);
		   requestMsg.append(", dest :"); requestMsg.append(dest);
		   requestMsg.append(", tstamp: "); requestMsg.append(tstamp);
		   requestMsg.append(", text :"); requestMsg.append(text);
		   requestMsg.append(", user :"); requestMsg.append(user);
		   requestMsg.append(", Pass :");     requestMsg.append(pass);
		   requestMsg.append(", mpesa_code :"); requestMsg.append(mpesa_code);
		   requestMsg.append(", mpesa_acc :"); requestMsg.append(mpesa_acc);
		   requestMsg.append(", mpesa_msisdn : "); requestMsg.append(mpesa_msisdn);
		   requestMsg.append(", mpesa_trx_date :"); requestMsg.append(mpesa_trx_date);
		   requestMsg.append(", mpesa_trx_time :"); requestMsg.append(mpesa_trx_time);
		   requestMsg.append(", mpesa_amt :"); requestMsg.append(mpesa_amt);
		   requestMsg.append(", mpesa_sender: "); requestMsg.append(mpesa_sender);
	       String request = requestMsg.toString();	   
		   
		
		try{
			responseMessage = this.mpesaBridgeService.storeTransactionDetails(id,orig,dest,tstamp,text,user,pass,mpesa_code,mpesa_acc,
					mpesa_msisdn,mpesa_trx_date,mpesa_trx_time,mpesa_amt,mpesa_sender,mpesa_trx_type,office_Id);
			
			if(responseMessage.equalsIgnoreCase(mpesa_code)){
				return new ResponseEntity<String>("CONFLICT:" +responseMessage,HttpStatus.CONFLICT);
			}
			
		}catch(Exception e){
		//	logger.error("Exception " + e);
			
			logger.error(request, "Error is :" + e);
			
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		
		
		return new ResponseEntity<String>(responseMessage,HttpStatus.OK);
		
	}
	
	
	/*@RequestMapping(value = "/mpesatransactions", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ArrayList<Mpesa>> retriveAllTransactions(Long  officeId){
		ArrayList<Mpesa> transactionDetails = null;
		try{
			transactionDetails = this.mpesaBridgeService.retriveAllTransactions(officeId);
		}catch(Exception e){
			logger.error("Exception " + e);
		}
		return new ResponseEntity<ArrayList<Mpesa>>(transactionDetails,HttpStatus.OK);
	}*/
	
	@RequestMapping(value = "/getunmappedtransactions", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Collection<Mpesa>> retriveUnmappedTransactions(@QueryParam("officeId")final Long officeId,
			@QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit){
		Page<Mpesa> transactionDetails = null;
		 HttpHeaders responseHeaders = new HttpHeaders();
		 responseHeaders.set("Access-Control-Allow-Origin","*");
		 responseHeaders.set("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
		 //responseHeaders.setOrigin("*");
		try{
			//this.mpesaBridgeService.retriveAllTransactions(officeId);
			transactionDetails = this.mpesaBridgeService.retriveUnmappedTransactions(officeId, offset, limit);
		}catch(Exception e){
			logger.error("Exception " + e);
		}
		HashMap<String, Object> responseData= new HashMap<String, Object>();
		responseData.put("totalFilteredRecords", transactionDetails.getTotalElements());
		responseData.put("pageItems", transactionDetails.getContent());
		return new ResponseEntity(responseData,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/postpayment", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Collection<Mpesa>> completePayment ( @QueryParam("id") final Long id,@QueryParam("officeId") final Long officeId,@QueryParam("clientId") final Long clientId){
		 HttpHeaders responseHeaders = new HttpHeaders();
		List<Mpesa>transactionDetails=null;
		 responseHeaders.set("Access-Control-Allow-Origin","*");
		 responseHeaders.set("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
		 //responseHeaders.setOrigin("*");
		try{
			transactionDetails = this.mpesaBridgeService.Payment(id,officeId,clientId);
					}catch(Exception e){
			logger.error("Exception " + e);
		}
		
		return new ResponseEntity<Collection<Mpesa>>(transactionDetails,HttpStatus.OK);
	}
		
	@RequestMapping(value = "/Search", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Collection<Mpesa>> Search( @QueryParam("status") final String status,
			@QueryParam("FromDate") final String FromDate,@QueryParam("ToDate") final String ToDate,
			@QueryParam("mobileNo") final String mobileNo,
			@QueryParam("officeId")final  Long officeId, @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit){
		 HttpHeaders responseHeaders = new HttpHeaders();
		 Page<Mpesa>transactionDetails=null;
		 responseHeaders.set("Access-Control-Allow-Origin","*");
		 responseHeaders.set("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
		 //responseHeaders.setOrigin("*");
		 try{   
			    Date FromDate1=null;			    
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			    if(FromDate!=null&&FromDate!=""){
			    FromDate1 = formatter.parse(FromDate);			    
			    }
			    else{		    	
	        	    	Date dt =new Date(0);
	        	    	FromDate1=dt;
	        	    }
			    
			    Date ToDate1    = formatter.parse(ToDate);
				transactionDetails = this.mpesaBridgeService.searchMpesaDetail(status,mobileNo,FromDate1,ToDate1,officeId, offset, limit);
						}catch(Exception e){
				logger.error("Exception " + e);
			}
		 HashMap<String, Object> responseData= new HashMap<String, Object>();
		 responseData.put("totalFilteredRecords", transactionDetails.getTotalElements());
		 responseData.put("pageItems", transactionDetails.getContent());
		
		return new ResponseEntity(responseData,HttpStatus.OK);
	}		

	
}
