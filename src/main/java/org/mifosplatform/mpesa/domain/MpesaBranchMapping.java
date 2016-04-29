package org.mifosplatform.mpesa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity 
@Table(name="MpesaTxnBranchMapping")
public class MpesaBranchMapping {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="office_id")
	private Long office_id;
	
	@Column(name="MpesaPayBillNumber")
	private String MpesaPayBillNumber;

	public Long getOffice_id() {
		return office_id;
	}

	public void setOffice_id(Long office_id) {
		this.office_id = office_id;
	}

	public String getMpesaPayBillNumber() {
		return MpesaPayBillNumber;
	}

	public void setMpesaPayBillNumber(String MpesaPayBillNumber) {
		this.MpesaPayBillNumber = MpesaPayBillNumber;
	}
	
	
	
}
