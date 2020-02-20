package com.liquidpresentaion.authenticationservice.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.authenticationservice.model.User;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
	public List<User> findByEmail(String email);

	@Query(value = "select distinct ss.gss_supplier_group_pkid from lp_group_sales_supplier ss, "
			+ "(WITH RECURSIVE r AS (  "
			+ "SELECT gr.pk_id FROM lp_group gr join lp_group_user gu on gu.gu_user_pkid = :userPkid and gr.pk_id = gu.gu_group_pkid  "
			+ "UNION ALL  "
			+ "SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id "
			+ ") SELECT distinct pk_id FROM	r "
			+ ") sales "
			+ "where ss.gss_sales_group_pkid = sales.pk_id  "
			+ "UNION ALL "
			+ "SELECT DISTINCT GU_GROUP_PKID FROM LP_GROUP_USER GU JOIN LP_GROUP GR ON GU.GU_GROUP_PKID = GR.PK_ID "
			+ "AND GR.G_TYPE = 'supplier' AND GU.GU_USER_PKID = :userPkid ",
	nativeQuery = true)
	List<BigInteger> findSupplierGroupsByUserPkid(@Param(value = "userPkid")int userPkid);
	
	@Query(value = "select g_distributor_pkid from lp_group where g_parent_pkid = 0 and g_type = 'sales' and pk_id in ( "
			+ "WITH RECURSIVE r AS (  "
			+ "SELECT gr.pk_id, gr.g_parent_pkid FROM lp_group gr join lp_group_user gu on gu.gu_user_pkid = :userPkid and gr.pk_id = gu.gu_group_pkid  "
			+ "UNION ALL  "
			+ "SELECT lp_group.pk_id, lp_group.g_parent_pkid	FROM lp_group,r WHERE lp_group.pk_id = r.g_parent_pkid "
			+ ") SELECT distinct pk_id FROM	r "
			+ ") ",
	nativeQuery = true)
	Optional<BigInteger> findDistributorPkidByUserPkid(@Param(value = "userPkid")int userPkid);
}
