package org.mifosplatform.mpesa.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.mpesa.configuration.ClientHelper;
import org.mifosplatform.mpesa.domain.Mpesa;
import org.mifosplatform.mpesa.repository.MpesaBridgeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Service
public class MpesaBridgeServiceImpl implements MpesaBridgeService{
	
	@Value("${mifosurl}")
	private String mifosurl;
	
	@Value("${mifosusername}")
	private String mifosusername;
	
	@Value("${mifospassword}")
	private String mifospassword;
	
	@Value("${tenantIdentifier}")
	private String tenantIdentifier;
	
	private final Logger logger = LoggerFactory.getLogger(MpesaBridgeServiceImpl.class);
	
	private final MpesaBridgeRepository mpesaBridgeRepository;
	
	@Autowired
	public MpesaBridgeServiceImpl(final MpesaBridgeRepository mpesaBridgeRepository) {
		super();
		this.mpesaBridgeRepository = mpesaBridgeRepository;
	}

	
	@Override
	@Transactional
	public String storeTransactionDetails(final Long id, final String origin, final String dest,final String tStamp, final String text, final String user, 
			final String pass, final String mpesaCode, final String mpesaAccount, final String mobileNo,final Date txnDate, final String txnTime, 
			final BigDecimal mpesaAmount, final String sender,final String mpesaTxnType,final Long officeId) {
		Mpesa mpesa = null;
		Mpesa response = null;
		String responseData = "";
		try{
			if(id != null && mpesaCode != null && !mpesaCode.equalsIgnoreCase("")){
				mpesa = new Mpesa();
				mpesa.setIpnId(id);
				mpesa.setOrigin(origin);
				mpesa.setDestination(dest);
				mpesa.setTimeStamp(tStamp);
				mpesa.setTestMessage(text);
				mpesa.setUser(user);
				mpesa.setPassword(pass);
				mpesa.setTransactionCode(mpesaCode);
				mpesa.setAccountName(mpesaAccount);
				mpesa.setMobileNo(mobileNo);
				mpesa.setTransactionDate(txnDate);
				mpesa.setTransactionTime(txnTime);
				mpesa.setTransactionAmount(mpesaAmount);
				mpesa.setSender(sender);
				mpesa.setType(mpesaTxnType);
				mpesa.setOfficeId(officeId);
				mpesa.setStatus("R");
				response = this.mpesaBridgeRepository.save(mpesa);
				if(response != null){
					responseData = "Thank you for your payment";
				}
				System.out.println("response " + response);
			}else{
				logger.info("Empty Parameter passed");
				responseData = "Empty Parameter passed";
			}
		}catch(Exception e){
			logger.error("Exception while storeTransactionDetails " + e);
			return responseData = e.getMessage();
		}
		return responseData;
	}


	@Override
	@Transactional
	public ArrayList<Mpesa> retriveAllTransactions(Long officeId) {
		ArrayList<Mpesa> transactionList = null;
		Client client = null;
		WebResource webResource = null;
		Boolean nationalIdSearch=false;
		Long officeid=officeId;		
		
		try{
			final String authenticationKey = loginIntoServerAndGetBase64EncodedAuthenticationKey();
			transactionList = (ArrayList<Mpesa>) this.mpesaBridgeRepository.fetchTransactionforMapping(officeid);
			for(int i=0;i<transactionList.size();i++)
			  {  nationalIdSearch=false;
				 Mpesa mpesaforsearch=transactionList.get(i);
				 String [] nationalIdDetails=mpesaforsearch.getTestMessage().split("Acc. ");
				 String mobileNo=mpesaforsearch.getMobileNo();
				 String nationalId=nationalIdDetails[1];
				 if(nationalId!=null && nationalId!="" ){
				 				 
				 client = ClientHelper.createClient();
					webResource = client
					   .resource(mifosurl+"/mifosng-provider/api/v1/search?query="+nationalId+"&resource=clients");
			 
					ClientResponse response = webResource.header("X-mifos-Platform-TenantId", tenantIdentifier)
								.header("Content-Type", "application/json")
								.header("Authorization","Basic "+authenticationKey)
						        .get(ClientResponse.class);
			 
					if (response.getStatus() != 200) 
					{
					   throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
					}
			   
					String clientDetailsByNationalId = response.getEntity(String.class);
					JSONArray clientsData = (JSONArray) JSONValue.parseWithException(clientDetailsByNationalId);
					if(clientsData!=null)
					{
					  if(clientsData.size()>0)
					    {	
					       for(int j=0;j<clientsData.size();j++)
					         {
					           JSONObject clientData = (JSONObject) clientsData.get(i);
					            if(clientData != null && clientData.get("entityType").equals("CLIENT")&&clientData.get("entityNationalId")!=null)
					             {
						          if(clientData.get("entityNationalId").equals(nationalId)||clientData.get("entityNationalId")==nationalId)
						           {
							         String ClientName=(String) clientData.get("entityName");
							         nationalIdSearch=true;
							         callClientMifosApi(clientData.get("entityId").toString(),authenticationKey,mobileNo,ClientName,officeid);
							         
						           }
					             }
					         }
					    }
					}
				}
				 	
			  
			if(!nationalIdSearch)		
			  {
				String mobileNoforSearch=mobileNo.substring(3,mobileNo.length());
			    String mobileNowithZero=0+mobileNoforSearch;			
				client = ClientHelper.createClient();
				webResource = client
				   .resource(mifosurl+"/mifosng-provider/api/v1/search?query="+mobileNoforSearch+"&resource=clients");
		 
				ClientResponse clientsDatasearchByMobileNo = webResource.header("X-mifos-Platform-TenantId", tenantIdentifier)
							.header("Content-Type", "application/json")
							.header("Authorization","Basic "+authenticationKey)
					        .get(ClientResponse.class);
		 
				if (clientsDatasearchByMobileNo.getStatus() != 200) {
				   throw new RuntimeException("Failed : HTTP error code : "
					+ clientsDatasearchByMobileNo.getStatus());
				}
		  
				String cilentsDataByMobileNo = clientsDatasearchByMobileNo.getEntity(String.class);
				JSONArray cilentsData = (JSONArray) JSONValue.parseWithException(cilentsDataByMobileNo);
				if(cilentsData!=null)
				{
				  if(cilentsData.size()>0)
				    {	
				     for(int k=0;k<cilentsData.size();k++)
				      {
				         JSONObject clientData = (JSONObject) cilentsData.get(k);
				         if(clientData != null && clientData.get("entityType").equals("CLIENT")&&clientData.get("entityMobileNo")!=null)
				           {
					         if(clientData.get("entityMobileNo").equals(mobileNoforSearch)||clientData.get("entityMobileNo").equals(mobileNowithZero))
					           {
						         String ClientName=(String) clientData.get("entityName");
						          callClientMifosApi(clientData.get("entityId").toString(),authenticationKey,mobileNo,ClientName,officeid);
						          System.out.println("mobile no");
					           }
					        else
					          {
						        final List<Mpesa> mpesaList = this.mpesaBridgeRepository.fetchTransactionInfoById(mobileNo,officeid);
						        for(Mpesa mpesa : mpesaList)
						         {
							       if(mpesa.getStatus()!="CMP" && mpesa.getStatus()!="PAID"&& mpesa.getStatus()!="BM")
							         {
								       mpesa.setStatus("UNMP");
							         }
							           this.mpesaBridgeRepository.save(mpesa);
						          }
					           }
			        	}
				     else if(clientData != null)
			       	     {
					          final List<Mpesa> mpesaList = this.mpesaBridgeRepository.fetchTransactionInfoById(mobileNo,officeid);
					          for(Mpesa mpesa : mpesaList)
					          {
						        if(mpesa.getStatus()!="CMP" && mpesa.getStatus()!="PAID"&& mpesa.getStatus()!="BM")
						        {
							      mpesa.setStatus("UNMP");
						        }
						        this.mpesaBridgeRepository.save(mpesa);
					         }					
				         }
				      }
				   }
				
				else
				    {
					   final List<Mpesa> mpesaList = this.mpesaBridgeRepository.fetchTransactionInfoById(mobileNo,officeid);
					   for(Mpesa mpesa : mpesaList)
					    {
						   if(mpesa.getStatus()!="CMP" && mpesa.getStatus()!="PAID"&& mpesa.getStatus()!="BM")
						    {
							 mpesa.setStatus("UNMP");
							 }
						    this.mpesaBridgeRepository.save(mpesa);
					     }
				     }
				}
			}
		 }	
		}catch(Exception e){
			logger.error("Exception " + e);
		}
		return transactionList;
	}

	private String loginIntoServerAndGetBase64EncodedAuthenticationKey() {
		 final String loginURL =mifosurl+"/mifosng-provider/api/v1/authentication?username="+mifosusername+"&password="+mifospassword;
         System.out.println(loginURL);
		Client client = null;
		WebResource webResource = null;
		String authenticationKey = null;
		try{
			client = ClientHelper.createClient();
			webResource = client.resource(loginURL);
			 
					ClientResponse response = webResource.header("X-mifos-Platform-TenantId", tenantIdentifier)
								.header("Content-Type", "application/json")
						        .post(ClientResponse.class);
					String responseData = response.getEntity(String.class);
					JSONObject rootObj = (JSONObject) JSONValue.parseWithException(responseData);
					if(rootObj != null && !rootObj.equals("")){
						authenticationKey = rootObj.get("base64EncodedAuthenticationKey").toString();
					}
		}catch(Exception e){
			logger.error("Exception while loginIntoServerAndGetBase64EncodedAuthenticationKey " + e);
		}
		return authenticationKey;
	}


	private void callClientMifosApi(String clientId,String authenticationKey,String mobileNo,String ClientName,Long officeid) {
		Client client = null;
		WebResource webResource = null;
		try{
			client = ClientHelper.createClient();
			webResource = client.resource(mifosurl+"/mifosng-provider/api/v1/clients/"+clientId+"/accounts");
			 
					ClientResponse response = webResource.header("X-mifos-Platform-TenantId", "default")
								.header("Content-Type", "application/json")
								.header("Authorization","Basic "+authenticationKey)
						        .get(ClientResponse.class);
					String responseData = response.getEntity(String.class);
					/*JSONObject rootObj = (JSONObject) JSONValue.parseWithException(responseData);
					JSONArray loanAccountArray = (JSONArray) rootObj.get("loanAccounts");
					JSONArray savingAccountArray = (JSONArray) rootObj.get("savingsAccounts");*/
					final List<Mpesa> mpesaList = this.mpesaBridgeRepository.fetchTransactionInfoById(mobileNo,officeid);
					for(Mpesa mpesa : mpesaList){
						mpesa.setStatus("CMP");
						mpesa.setClientId(Long.parseLong(clientId));
						mpesa.setClientName(ClientName);
						this.mpesaBridgeRepository.save(mpesa);
					}
					/*if(loanAccountArray != null){
						JSONObject loanAccountNo = (JSONObject) loanAccountArray.get(0);
						if(loanAccountArray.size() <= 1){
							logger.info("Only one loan account do the transaction");
							//doTranasction(loanAccountNo.get("id").toString(),authenticationKey,nationalId);
						}else{
							logger.info("Multiple loan accounts ");
							//final List<Mpesa> mpesaList = this.mpesaBridgeRepository.fetchTransactionInfoByNationalId(nationalId);
							for(Mpesa mpesa : mpesaList){
								mpesa.setStatus("P");
								mpesa.setClientId(Long.parseLong(clientId));
								this.mpesaBridgeRepository.save(mpesa);
							}
						}
					}*/
					/*if(savingAccountArray != null){
						JSONObject savingAccountNo = (JSONObject) savingAccountArray.get(0);
						if(savingAccountNo.size() <= 1){
							logger.info("Only one saving account do the transaction");
							doTranasction(savingAccountNo.get("id").toString(),authenticationKey,nationalId);
						}else{
							logger.info("Multiple saving accounts ");
							//final List<Mpesa> mpesaList = this.mpesaBridgeRepository.fetchTransactionInfoByNationalId(nationalId);
							for(Mpesa mpesa : mpesaList){
								mpesa.setStatus("P");
								this.mpesaBridgeRepository.save(mpesa);
							}
						}
					}*/
					
		}catch(Exception e){
			logger.error("Exception while callClientMifosApi " + e);
		}
		
	}


	private void doTranasction(String mpesaId, String authenticationKey, String nationalId) {
		Client client = null;
		WebResource webResource = null;
		//List<Mpesa> mpesaList = null;
		try{
			/*if(nationalId != null){
				mpesaList = this.mpesaBridgeRepository.fetchTransactionInfoByNationalId(nationalId);
			}else{
				mpesaList = this.mpesaBridgeRepository.fetchTransactionInfoById(phoneNo);
			}*/
			List<Mpesa> mpesaList = this.mpesaBridgeRepository.fetchTransactionInfoByNationalId(nationalId);
			final Mpesa mpesa = mpesaList.get(0);
			DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMM yyyy");
			LocalDate transactionDate = new LocalDate(mpesa.getTransactionDate());
			String date = transactionDate.toString(fmt);
			String json = "{\"transactionAmount\": "+mpesa.getTransactionAmount()+",\"transactionDate\": \""+date+"\",\"locale\": \"en\",\"dateFormat\": \"dd MMMM yyyy\"}";
			client = ClientHelper.createClient();
			webResource = client.resource(mifosurl+"/mifosng-provider/api/v1/loans/"+mpesaId+"/transactions?command=repayment");
			 
					ClientResponse response = webResource.header("X-mifos-Platform-TenantId", tenantIdentifier)
								.header("Content-Type", "application/json")
								.header("Authorization","Basic "+authenticationKey)
						        .post(ClientResponse.class,json);
					String responseData = response.getEntity(String.class);
					logger.info("transaction " + responseData);
					mpesa.setStatus("S");
					this.mpesaBridgeRepository.save(mpesa);
			
		}catch(Exception e){
			logger.error("Exception while doTransaction " + e);
		}
		
	}


	@Override
	public Collection<Mpesa> retriveUnmappedTransactions(Long officeId) {
		Collection<Mpesa> unmappedTransactionList = null;
		try{
			unmappedTransactionList = this.mpesaBridgeRepository.retriveUnmappedTransactions(officeId);
		}catch(Exception e){
			logger.error("Exception while retriveUnmappedTransactions " + e);
		}
		return unmappedTransactionList;
	}
	
	@Override
     public List<Mpesa>Payment(Long Id){
		final List<Mpesa> mpesaList = this.mpesaBridgeRepository.retriveTransactionsforPayment(Id);
		for(Mpesa mpesa : mpesaList){
			mpesa.setStatus("PAID");			
			this.mpesaBridgeRepository.save(mpesa);
		}
       return mpesaList;
     }
	
	 @Override
	public Collection<Mpesa> searchMpesaDetail(String status, String mobileNo,
			Date fromDate, Date toDate,Long officeId) {
		Collection<Mpesa> TransactionList = null;
		try{
			if(mobileNo!=null&& mobileNo!=""&& status.equals("")&& fromDate==null&&toDate!=null){
			
			TransactionList = this.mpesaBridgeRepository.searchByMobileNoTxnDate(mobileNo,toDate,officeId);
			}
			if(toDate!=null&&mobileNo.equals("")&& fromDate==null&& status==""){
				TransactionList = this.mpesaBridgeRepository.toDateSearch(toDate,officeId);
			}
			if(status!=null&&status!=""&& mobileNo.equals("")&& fromDate==null&&toDate!=null){
				if(fromDate==null){
        	    	Date dt =new Date(0);
        	    	fromDate=dt;
        	    }				 				
				TransactionList = this.mpesaBridgeRepository.search(status,fromDate,toDate,officeId);
				fromDate=null;				
			}
			if(fromDate!=null&&toDate!=null&& mobileNo.equals("") && status.equals("")){
				
				TransactionList = this.mpesaBridgeRepository.LikeSearch(fromDate,toDate,officeId);
			}
           if(fromDate!=null&&toDate!=null&& mobileNo.equals("") && status!=null&&status!=""){
				
				TransactionList = this.mpesaBridgeRepository.search(status,fromDate,toDate,officeId);
			}
           if(fromDate!=null&&toDate!=null&& mobileNo!=null&& mobileNo!="" &&status.equals("")&& status==""){
				
				TransactionList = this.mpesaBridgeRepository.likesearch(mobileNo,fromDate,toDate,officeId);
			}
           if(status!=null&&status!=""&& mobileNo!=null&& mobileNo!=""&& toDate!=null){
        	    if(fromDate==null){
        	    	Date dt =new Date(0);
        	    	fromDate=dt;
        	    }				
				TransactionList = this.mpesaBridgeRepository.Exactsearch(status,mobileNo,fromDate,toDate,officeId);
				fromDate=null;
			}
           if(status!=null&&status!=""&& mobileNo!=null&& mobileNo!=""&&fromDate!=null&&toDate!=null){
				
				TransactionList = this.mpesaBridgeRepository.Exactsearch(status,mobileNo,fromDate,toDate,officeId);
			}
			
		}catch(Exception e){
			logger.error("Exception while fetchTransactionByStatus " + e);
		}
		return TransactionList;
	}	
	
	}
	


