package com.liquidpresentation.ingredientservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.liquidpresentation.ingredientservice.model.PriceCsv;

public interface PriceCsvRepository extends PagingAndSortingRepository<PriceCsv, Long> {
	List<PriceCsv> findByBatchId(String batchId);
	List<PriceCsv> findByBatchIdOrderByPkIdAsc(String batchId);
	/*@Query(value ="select ic_csv_record," + 
			" string_agg(ic_failure_message,',')" + 
			" from (" + 
			" select *" + 
			" from lp_ingredientpricing_csv  " + 
			" where ic_batch_id = :batchId " + 
			" ORDER BY pk_id asc" + 
			") t" + 
			" group by ic_batch_id, ic_csv_record",nativeQuery = true)
	List<PriceCsv> findErrorCsv(@Param("batchId") String batchId);*/
}
