package org.mifosplatform.mpesa.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.mifosplatform.mpesa.domain.Mpesa;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaBridgeRepository extends CrudRepository<Mpesa, Long>{
	
	@Query("from Mpesa mpesa WHERE mpesa.status in ('R','UNMP') and mpesa.type!='WithDraw' and mpesa.officeId=:officeId")
	Collection<Mpesa>fetchTransactionforMapping(@Param ("officeId") Long officeId);   

	@Query("from Mpesa mpesa WHERE mpesa.mobileNo like :phoneNo AND mpesa.status in ('R','UNMP') and mpesa.type!='WithDraw' and mpesa.officeId=:officeId ")
	List<Mpesa> fetchTransactionInfoById(@Param("phoneNo") String phoneNo,@Param("officeId") Long officeId);

	@Query(" from Mpesa mpesa WHERE mpesa.status in ('CMP','R','BM') and  mpesa.type!='WithDraw' and mpesa.officeId=:officeId")
	Collection<Mpesa> retriveUnmappedTransactions(@Param("officeId") Long officeId );
	
	@Query(" from Mpesa mpesa WHERE mpesa.id=:id")
	List<Mpesa> retriveTransactionsforPayment(@Param("id") Long id);	
	
	@Query("from Mpesa mpesa WHERE mpesa.accountName = :nationalId AND mpesa.status = 'R'")
	List<Mpesa> fetchTransactionInfoByNationalId(@Param("nationalId") String nationalId);
	
	
	@Query("from Mpesa mpesa WHERE    mpesa.transactionDate between :FromDate and :ToDate and mpesa.officeId =:officeId " )
	List<Mpesa>LikeSearch(@Param("FromDate") Date FromDate,@Param("ToDate") Date ToDate,@Param("officeId") Long officeId );
    
	@Query("from Mpesa mpesa WHERE mpesa.mobileNo like %:phoneNo%  and mpesa.status=:status and mpesa.transactionDate between :FromDate and :ToDate and mpesa.officeId =:officeId" )
	List<Mpesa>Exactsearch(@Param("status") String status,@Param("phoneNo") String phoneNo,@Param("FromDate") Date FromDate,@Param("ToDate") Date ToDate,@Param("officeId") Long officeId);
    
    @Query("from Mpesa mpesa WHERE mpesa.mobileNo like %:phoneNo%  and mpesa.status=:status")
    List<Mpesa>search(@Param("status") String status,@Param("phoneNo") String phoneNo);
    
	@Query("from Mpesa mpesa WHERE mpesa.mobileNo like %:phoneNo% and  mpesa.transactionDate between :FromDate and :ToDate and mpesa.officeId =:officeId ")
	List<Mpesa>likesearch(@Param("phoneNo") String phoneNo,@Param("FromDate") Date FromDate,@Param("ToDate") Date ToDate,@Param("officeId") Long officeId);
	
	@Query("from Mpesa mpesa WHERE  mpesa.status=:status and mpesa.transactionDate between :FromDate and :ToDate and mpesa.officeId =:officeId" )
	List<Mpesa>search(@Param("status") String status,@Param("FromDate") Date FromDate,@Param("ToDate") Date ToDate,@Param("officeId") Long officeId);
    
	@Query("from Mpesa mpesa WHERE mpesa.transactionDate <= :ToDate and mpesa.officeId =:officeId " )
	List<Mpesa>toDateSearch( @Param("ToDate") Date ToDate,@Param("officeId") Long officeId);
	
	@Query("from Mpesa mpesa Where mpesa.mobileNo like %:phoneNo% and transactionDate <= :ToDate and mpesa.officeId =:officeId ")
	List<Mpesa>searchByMobileNoTxnDate(@Param("phoneNo") String phoneNo,@Param("ToDate") Date ToDate,@Param("officeId") Long officeId); 

    


    
	//    @Query("from Mpesa mpesa WHERE " +
//    		"" +
//    		"if(:phoneNo!=null,mpesa.mobileNo like %:phoneNo%,' ') And if(:status!=null,mpesa.status =:status,' ')"
//			 +"And if(:FromDate!=null,if(:ToDate!=null, mpesa.transactionDate between :FromDate and :ToDate,' '),' ')")
//    
//	List<Mpesa>search(@Param("status") String status,@Param("phoneNo") String phoneNo,@Param("FromDate") Date FromDate,@Param("ToDate") Date ToDate);
////	
//	
//   @Query("from Mpesa mpesa WHERE mpesa.mobileNo like %:phoneNo% and mpesa.status =:status and mpesa.transactionDate between :FromDate and :ToDate")
//		   	
//	List<Mpesa>search(@Param("status") String status,@Param("phoneNo") String phoneNo,@Param("FromDate") Date FromDate,@Param("ToDate") Date ToDate);
//   
//    @Query("from Mpesa mpesa WHERE mpesa.mobileNo like if(:phoneNo!=null,:phoneNo,' ')" +" union "+
//   		   "from Mpesa mpesa WHERE  mpesa.status = if(:status!=null,:status,' ')"+"  union  "+
//		   "from Mpesa mpesa WHERE mpesa.transactionDate between if (:FromDate!=null,:FromDate,' ') and :ToDate")
//	List<Mpesa>search(@Param("status") String status,@Param("phoneNo") String phoneNo,@Param("FromDate") Date FromDate,@Param("ToDate") Date ToDate);

   		    		 

	
}
