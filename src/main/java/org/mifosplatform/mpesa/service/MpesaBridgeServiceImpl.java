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
public class MpesaBridgeServiceImpl implements MpesaBridgeService {

	@Value("${mifosurl}")
	private String mifosurl;

	@Value("${mifosusername}")
	private String mifosusername;

	@Value("${mifospassword}")
	private String mifospassword;

	@Value("${tenantIdentifier}")
	private String tenantIdentifier;

	private final Logger logger = LoggerFactory
			.getLogger(MpesaBridgeServiceImpl.class);

	private final MpesaBridgeRepository mpesaBridgeRepository;

	@Autowired
	public MpesaBridgeServiceImpl(
			final MpesaBridgeRepository mpesaBridgeRepository) {
		super();
		this.mpesaBridgeRepository = mpesaBridgeRepository;
	}

	@Override
	@Transactional
	public String storeTransactionDetails(final Long id, final String origin,
			final String dest, final String tStamp, final String text,
			final String user, final String pass, final String mpesaCode,
			final String mpesaAccount, final String mobileNo,
			final Date txnDate, final String txnTime,
			final BigDecimal mpesaAmount, final String sender,
			final String mpesaTxnType, final Long officeId) {
		Mpesa mpesa = null;
		Mpesa response = null;
		String responseData = "";
		try {
			if (id != null && mpesaCode != null
					&& !mpesaCode.equalsIgnoreCase("")) {
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
				String nationaId = null;
				if (officeId != 0) {
					if (text.contains("Acc. ")) {
						nationaId = text.substring(text.indexOf("Acc.") + "Acc.".length()).trim();
					}
				} else {
					if (text.contains("Account Number")) {
						String accNum = text.substring(text.indexOf("Account Number")+ "Account Number".length()).trim();						
						nationaId = accNum.substring(0, accNum.indexOf(" "));
					}
				}
				String MobileNo = mobileNo.substring(3, mobileNo.length());
				String result = branchMap(MobileNo, nationaId);
				String data[] = result.split("=");
				mpesa.setStatus(data[2]);
				mpesa.setClientName(data[0]);
				if(officeId == 0){					
				if (!data[1].equals(" ")) {
					mpesa.setOfficeId(Long.parseLong(data[1]));
				}
				}				
				if (!data[3].equals(" ")) {
					mpesa.setClientId(Long.parseLong(data[3]));
				}
				response = this.mpesaBridgeRepository.save(mpesa);
				if (response != null) {
					responseData = "Thank you for your payment";
				}
			} else {
				logger.info("Empty Parameter passed");
				responseData = "Empty Parameter passed";
			}
		} catch (Exception e) {
			logger.error("Exception while storeTransactionDetails " + e);
			return responseData = e.getMessage();
		}
		return responseData;
	}

		private String loginIntoServerAndGetBase64EncodedAuthenticationKey() {
		final String loginURL = mifosurl
				+ "/mifosng-provider/api/v1/authentication?username="
				+ mifosusername + "&password=" + mifospassword;
		System.out.println(loginURL);
		Client client = null;
		WebResource webResource = null;
		String authenticationKey = null;
		try {
			client = ClientHelper.createClient();
			webResource = client.resource(loginURL);

			ClientResponse response = webResource
					.header("X-mifos-Platform-TenantId", tenantIdentifier)
					.header("Content-Type", "application/json")
					.post(ClientResponse.class);
			String responseData = response.getEntity(String.class);
			JSONObject rootObj = (JSONObject) JSONValue
					.parseWithException(responseData);
			if (rootObj != null && !rootObj.equals("")) {
				authenticationKey = rootObj.get(
						"base64EncodedAuthenticationKey").toString();
			}
		} catch (Exception e) {
			logger.error("Exception while loginIntoServerAndGetBase64EncodedAuthenticationKey "
					+ e);
		}
		return authenticationKey;
	}

		@Override
	public String branchMap(String MobileNo, String nationalId) {
		Boolean nationalIdSearch = false;
		Client client = null;
		String authenticationKey = null;
		WebResource webResource = null;
		String details = "";
		try {
			authenticationKey = loginIntoServerAndGetBase64EncodedAuthenticationKey();
			if (nationalId != null && nationalId != "") {
				client = ClientHelper.createClient();
				webResource = client.resource(mifosurl+ "/mifosng-provider/api/v1/search?query=" + nationalId+ "&resource=clients");
				ClientResponse response = webResource
						.header("X-mifos-Platform-TenantId", tenantIdentifier)
						.header("Content-Type", "application/json")
						.header("Authorization", "Basic " + authenticationKey)
						.get(ClientResponse.class);				
				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ response.getStatus());
				}
				String clientDetailsByNationalId = response	.getEntity(String.class);
			  JSONArray clientsData = (JSONArray) JSONValue	.parseWithException(clientDetailsByNationalId);
				if (clientsData != null) {
					if (clientsData.size() > 0) {
					   for (int j = 0; j < clientsData.size(); j++) {
						JSONObject clientData = (JSONObject) clientsData.get(j);
					if (clientData != null&& clientData.get("entityType").equals("CLIENT")&& clientData.get("entityNationalId") != null) {									
					if (clientData.get("entityNationalId").equals(nationalId)|| clientData.get("entityNationalId") == nationalId) {										
							String ClientName = (String) clientData	.get("entityName");
							nationalIdSearch = true;
							details = ClientName + "="+ clientData.get("parentId") + "="+ "CMP" + "="+ clientData.get("entityId");										
					 }
				   }
				 }
				}
			  }
			}
			if (!nationalIdSearch) {
				String mobileNowithZero = 0 + MobileNo;
				client = ClientHelper.createClient();
				webResource = client.resource(mifosurl+ "/mifosng-provider/api/v1/search?query=" + MobileNo+ "&resource=clients");
				ClientResponse clientsDatasearchByMobileNo = webResource
						.header("X-mifos-Platform-TenantId", tenantIdentifier)
						.header("Content-Type", "application/json")
						.header("Authorization", "Basic " + authenticationKey)
						.get(ClientResponse.class);				
							if (clientsDatasearchByMobileNo.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ clientsDatasearchByMobileNo.getStatus());
				}

				String cilentsDataByMobileNo = clientsDatasearchByMobileNo.getEntity(String.class);						
				JSONArray cilentsData = (JSONArray) JSONValue.parseWithException(cilentsDataByMobileNo);						
				if (cilentsData != null) {
				if (cilentsData.size() > 0) {
					for (int k = 0; k < cilentsData.size(); k++) {
					JSONObject clientData = (JSONObject) cilentsData.get(k);									
				if (clientData != null&& clientData.get("entityType").equals("CLIENT")&& clientData.get("entityMobileNo") != null) {
				if (clientData.get("entityMobileNo").equals(MobileNo)|| clientData.get("entityMobileNo").equals(mobileNowithZero)) {					
					String ClientName = (String) clientData.get("entityName");					
					details = ClientName + "="+ clientData.get("parentId") + "="+ "CMP" + "="+ clientData.get("entityId");				
				}			
				else {						
					details = " " + "=" + " " + "=" + "UNMP"+ "=" + " ";
				}}
				else if (clientData != null) {	
					details = " " + "=" + " " + "=" + "UNMP" + "="+ " ";
				}}
				}else {
					details = " " + "=" + " " + "=" + "UNMP" + "=" + " ";
				}	
				}
			  }					
		} catch (Exception e) {
			logger.error("Exception " + e);
		}
		return details;
	}								
	@Override
	public Collection<Mpesa> retriveUnmappedTransactions(Long officeId) {
		Collection<Mpesa> unmappedTransactionList = null;
		try {
			unmappedTransactionList = this.mpesaBridgeRepository.retriveUnmappedTransactions(officeId);					
		} catch (Exception e) {
			logger.error("Exception while retriveUnmappedTransactions " + e);
		}
		return unmappedTransactionList;
	}

	@Override
	public List<Mpesa> Payment(Long Id) {
		final List<Mpesa> mpesaList = this.mpesaBridgeRepository.retriveTransactionsforPayment(Id);				
		for (Mpesa mpesa : mpesaList) {
			mpesa.setStatus("PAID");
			this.mpesaBridgeRepository.save(mpesa);
		}
		return mpesaList;
	}

	@Override
	public Collection<Mpesa> searchMpesaDetail(String status, String mobileNo,
			Date fromDate, Date toDate, Long officeId) {
		Collection<Mpesa> TransactionList = null;
		try {
			if (mobileNo.equals("") && status.equals("")) {
				TransactionList = this.mpesaBridgeRepository.LikeSearch(
						fromDate, toDate, officeId);			}
			if (mobileNo.equals("") && status != null && status != "") {
				if (status.equals("UNMP")) {
					TransactionList = this.mpesaBridgeRepository.unmappedofficed(status, fromDate, toDate, officeId);
				} else {
					TransactionList = this.mpesaBridgeRepository.search(status,fromDate, toDate, officeId);							
				}
			}
			if (mobileNo != null && mobileNo != "" && status.equals("")&& status == "") {			
				TransactionList = this.mpesaBridgeRepository.likesearch(mobileNo, fromDate, toDate, officeId);
			}

			if (status != null && status != "" && mobileNo != null && mobileNo != "") {
				if (status.equals("UNMP")) {
					TransactionList = this.mpesaBridgeRepository.UnMappedOffice(status, mobileNo, fromDate, toDate,officeId);

				} else {
					TransactionList = this.mpesaBridgeRepository.Exactsearch(status, mobileNo, fromDate, toDate, officeId);
				}
			}

		} catch (Exception e) {
			logger.error("Exception while fetchTransactionByStatus " + e);
		}
		return TransactionList;
	}

}
