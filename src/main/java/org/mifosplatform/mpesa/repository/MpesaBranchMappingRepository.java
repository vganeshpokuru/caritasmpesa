package org.mifosplatform.mpesa.repository;

import org.mifosplatform.mpesa.domain.MpesaBranchMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface MpesaBranchMappingRepository extends CrudRepository<MpesaBranchMapping, Long> {

	@Query(" from MpesaBranchMapping mbm where mbm.MpesaPayBillNumber =:MpesaPayBillNumber ")
    MpesaBranchMapping getOfficeIdFromDestNumber(@Param("MpesaPayBillNumber") String mpesaPayBillNumber);
		
}
