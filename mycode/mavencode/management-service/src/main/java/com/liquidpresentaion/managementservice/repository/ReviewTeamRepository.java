package com.liquidpresentaion.managementservice.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.managementservice.model.ReviewTeam;


@Repository
public interface ReviewTeamRepository extends PagingAndSortingRepository<ReviewTeam, Integer>{

	// 最外边多套一层是因为新起的别名不能排序
	@Query(value = "SELECT DISTINCT 		"+ 
			"		PS.u_first_name," + 
			"		PS.u_last_name," + 
			"		PS.pk_id," + 
			"		PS.fullname," + 
			"		PS.ps_count," + 
			"		PS.ps_count_last_year," + 
			"		PS.ps_count_current_month," + 
			"		PS.variance," + 
			"		PS.variancepercent FROM (" +
			"SELECT " + 
			"	PS.*,(PS.ps_Count - PS.ps_Count_last_year) variance, "
			+ " CASE PS.ps_Count_last_year " + 
			"	WHEN 0 THEN " + 
			"		PS.ps_Count*100 " + 
			"	ELSE " + 
			"		round( cast(((PS.ps_Count-PS.ps_Count_last_year)*100) AS numeric)/cast(PS.ps_Count_last_year AS numeric)) " + 
			"END AS variancepercent " +
			" FROM " + 
			"	(" + 
			"	SELECT" + 
			"		u.u_first_name," + 
			"		u.u_last_name," + 
			"		gu.gu_group_pkid," + 
			"		u.pk_id," + 
			"		u.u_first_name || ' ' || u.u_last_name fullname," + 
			"		COALESCE (" + 
			"			(" + 
			"			SELECT COUNT" + 
			"				( 1 ) " + 
			"			FROM" + 
			"				lp_presentation ps " + 
			"			WHERE " + 
			"				ps.ps_create_user_pkid = u.pk_id " + 
			"				AND ps_create_timestamp > :startDate " + 
			"				AND ps_create_timestamp < :endDate " + 
			"			GROUP BY" + 
			"				ps_create_user_pkid " + 
			"			)," + 
			"			0 " + 
			"		) ps_Count," + 
			"		COALESCE (" + 
			"			(" + 
			"			SELECT COUNT" + 
			"				( 1 ) " + 
			"			FROM" + 
			"				lp_presentation ps " + 
			"			WHERE " + 
			"				ps.ps_create_user_pkid = u.pk_id " + 
			"				AND ps_create_timestamp > (date(:startDate) - interval '1Y') " + 
			"				AND ps_create_timestamp < (date(:endDate) - interval '1Y') " + 
			"			GROUP BY" + 
			"				ps_create_user_pkid " + 
			"			)," + 
			"			0 " + 
			"		) ps_Count_last_year," + 
			"		COALESCE (" + 
			"			(" + 
			"			SELECT COUNT" + 
			"				( 1 ) " + 
			"			FROM" + 
			"				lp_presentation ps " + 
			"			WHERE" + 
			"				ps.ps_create_user_pkid = u.pk_id " + 
			"				AND ps_create_timestamp > to_date(to_char(current_date,'YYYY-MM-01'),'yyyy-mm-dd') " + 
			"				AND ps_create_timestamp < (to_date(to_char(current_date+interval'1Months','YYYY-MM-01'),'yyyy-mm-dd')-1) " + 
			"			GROUP BY" + 
			"				ps_create_user_pkid " + 
			"			)," + 
			"			0 " + 
			"		) ps_Count_current_month " + 
			"	FROM " + 
			"		lp_user u" + 
			"		LEFT JOIN ( SELECT gu_user_pkid, gu_group_pkid FROM lp_group_user GROUP BY gu_user_pkid, gu_group_pkid ) gu ON u.pk_id = gu.gu_user_pkid " + 
			"	) ps ) PS " + 
			" WHERE " + 
			"	ps.gu_group_pkid in ( " + 
			"	WITH RECURSIVE r AS ( " + 
			"		SELECT pk_id FROM lp_group WHERE pk_id = :salesGroupId  " + 
			"		UNION ALL " + 
			"		SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id  " + 
			"	) SELECT pk_id FROM	r) "
			,countQuery = "SELECT COUNT(1) FROM (" +
					"SELECT DISTINCT 		"+
					"		PS.u_first_name," +
					"		PS.u_last_name," +
					"		PS.pk_id," +
					"		PS.fullname," +
					"		PS.ps_count," +
					"		PS.ps_count_last_year," +
					"		PS.ps_count_current_month," +
					"		PS.variance," +
					"		PS.variancepercent FROM ( " +
					"SELECT " + 
					"	PS.*,(PS.ps_Count - PS.ps_Count_last_year) variance, "
					+ " CASE PS.ps_Count_last_year " + 
					"	WHEN 0 THEN " + 
					"		PS.ps_Count*100 " + 
					"	ELSE " + 
					"		round( cast(((PS.ps_Count-PS.ps_Count_last_year)*100) AS numeric)/cast(PS.ps_Count_last_year AS numeric)) " + 
					"END AS variancepercent " +
					" FROM " + 
					"	(" + 
					"	SELECT" + 
					"		u.u_first_name," + 
					"		u.u_last_name," + 
					"		gu.gu_group_pkid," + 
					"		u.pk_id," + 
					"		u.u_first_name || ' ' || u.u_last_name fullname," + 
					"		COALESCE (" + 
					"			(" + 
					"			SELECT COUNT" + 
					"				( 1 ) " + 
					"			FROM" + 
					"				lp_presentation ps " + 
					"			WHERE " + 
					"				ps.ps_create_user_pkid = u.pk_id " + 
					"				AND ps_create_timestamp > :startDate " + 
					"				AND ps_create_timestamp < :endDate " + 
					"			GROUP BY" + 
					"				ps_create_user_pkid " + 
					"			)," + 
					"			0 " + 
					"		) ps_Count," + 
					"		COALESCE (" + 
					"			(" + 
					"			SELECT COUNT" + 
					"				( 1 ) " + 
					"			FROM" + 
					"				lp_presentation ps " + 
					"			WHERE " + 
					"				ps.ps_create_user_pkid = u.pk_id " + 
					"				AND ps_create_timestamp > (date(:startDate) - interval '1Y') " + 
					"				AND ps_create_timestamp < (date(:endDate) - interval '1Y') " + 
					"			GROUP BY" + 
					"				ps_create_user_pkid " + 
					"			)," + 
					"			0 " + 
					"		) ps_Count_last_year," + 
					"		COALESCE (" + 
					"			(" + 
					"			SELECT COUNT" + 
					"				( 1 ) " + 
					"			FROM" + 
					"				lp_presentation ps " + 
					"			WHERE" + 
					"				ps.ps_create_user_pkid = u.pk_id " + 
					"				AND ps_create_timestamp > to_date(to_char(current_date,'YYYY-MM-01'),'yyyy-mm-dd') " + 
					"				AND ps_create_timestamp < (to_date(to_char(current_date+interval'1Months','YYYY-MM-01'),'yyyy-mm-dd')-1) " + 
					"			GROUP BY" + 
					"				ps_create_user_pkid " + 
					"			)," + 
					"			0 " + 
					"		) ps_Count_current_month " + 
					"	FROM " + 
					"		lp_user u" + 
					"		LEFT JOIN ( SELECT gu_user_pkid, gu_group_pkid FROM lp_group_user GROUP BY gu_user_pkid, gu_group_pkid ) gu ON u.pk_id = gu.gu_user_pkid " + 
					"	) ps ) PS " + 
					" WHERE " + 
					"	ps.gu_group_pkid in ( " + 
					"	WITH RECURSIVE r AS ( " + 
					"		SELECT pk_id FROM lp_group WHERE pk_id = :salesGroupId  " + 
					"		UNION ALL " + 
					"		SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id  " + 
					"	) SELECT pk_id FROM	r)) A "
			,nativeQuery = true)
	Page<ReviewTeam> findTeamReviewNativeQuery(@Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate, @Param(value = "salesGroupId") Long salesGroupId, Pageable pageable);
	
	// 最外边多套一层是因为新起的别名不能排序
	@Query(value = "SELECT DISTINCT" +
			"		PS.u_first_name," +
			"		PS.u_last_name," +
			"		PS.pk_id," +
			"		PS.fullname," +
			"		PS.ps_count," +
			"		PS.ps_count_last_year," +
			"		PS.ps_count_current_month," +
			"		PS.variance," +
			"		PS.variancepercent FROM ( "+
			"SELECT " + 
			"	PS.*,(PS.ps_Count - PS.ps_Count_last_year) variance, "
			+ "CASE PS.ps_Count_last_year " + 
			"	WHEN 0 THEN " + 
			"		PS.ps_Count*100 " + 
			"	ELSE " + 
			"		round( cast(((PS.ps_Count-PS.ps_Count_last_year)*100) AS numeric)/cast(PS.ps_Count_last_year AS numeric)) " + 
			"END AS variancepercent " + 
			" FROM " + 
			"	(" + 
			"	SELECT" + 
			"		u.u_first_name," + 
			"		u.u_last_name," + 
			"		gu.gu_group_pkid," + 
			"		u.pk_id," + 
			"		u.u_first_name || ' ' || u.u_last_name fullname," + 
			"		COALESCE (" + 
			"			(" + 
			"			SELECT COUNT" + 
			"				( 1 ) " + 
			"			FROM" + 
			"				lp_presentation ps " + 
			"			WHERE" + 
			"				ps.ps_create_user_pkid = u.pk_id " + 
			"				AND ps_create_timestamp > :startDate " + 
			"				AND ps_create_timestamp < :endDate " + 
			"			GROUP BY" + 
			"				ps_create_user_pkid " + 
			"			)," + 
			"			0 " + 
			"		) ps_Count," + 
			"		COALESCE (" + 
			"			(" + 
			"			SELECT COUNT" + 
			"				( 1 ) " + 
			"			FROM" + 
			"				lp_presentation ps " + 
			"			WHERE" + 
			"				ps.ps_create_user_pkid = u.pk_id " + 
			"				AND ps_create_timestamp > (date(:startDate) - interval '1Y') " + 
			"				AND ps_create_timestamp < (date(:endDate) - interval '1Y') " + 
			"			GROUP BY" + 
			"				ps_create_user_pkid " + 
			"			)," + 
			"			0 " + 
			"		) ps_Count_last_year," + 
			"		COALESCE (" + 
			"			(" + 
			"			SELECT COUNT" + 
			"				( 1 ) " + 
			"			FROM " + 
			"				lp_presentation ps " + 
			"			WHERE" + 
			"				ps.ps_create_user_pkid = u.pk_id " + 
			"				AND ps_create_timestamp > to_date(to_char(current_date,'YYYY-MM-01'),'yyyy-mm-dd') " + 
			"				AND ps_create_timestamp < (to_date(to_char(current_date+interval'1Months','YYYY-MM-01'),'yyyy-mm-dd')-1) " + 
			"			GROUP BY" + 
			"				ps_create_user_pkid " + 
			"			)," + 
			"			0 " + 
			"		) ps_Count_current_month " + 
			"	FROM " + 
			"		lp_user u" + 
			"		LEFT JOIN ( SELECT gu_user_pkid, gu_group_pkid FROM lp_group_user GROUP BY gu_user_pkid, gu_group_pkid ) gu ON u.pk_id = gu.gu_user_pkid " + 
			"	) PS ) PS  " + 
			"	where ps.gu_group_pkid in ( " + 
			"	WITH RECURSIVE r AS ( " + 
			"		SELECT pk_id FROM lp_group WHERE pk_id = :salesGroupId  " + 
			"		UNION ALL " + 
			"		SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id  " + 
			"	) SELECT pk_id FROM	r) " + 
			"	AND (" + 
			"		LOWER ( PS.u_first_name ) LIKE'%' || LOWER ( :keyword ) || '%' " + 
			"		OR LOWER ( PS.u_last_name ) LIKE'%' || LOWER ( :keyword ) || '%' " + 
			"	OR LOWER ( PS.fullname ) LIKE'%' || LOWER ( :keyword ) || '%' " + 
			"	)"
			,countQuery = "SELECT COUNT(1) FROM (" +
					"SELECT DISTINCT" +
					"		PS.u_first_name," +
					"		PS.u_last_name," +
					"		PS.pk_id," +
					"		PS.fullname," +
					"		PS.ps_count," +
					"		PS.ps_count_last_year," +
					"		PS.ps_count_current_month," +
					"		PS.variance," +
					"		PS.variancepercent FROM ( "+
					"SELECT " + 
					"	PS.*,(PS.ps_Count - PS.ps_Count_last_year) variance, "
					+ "CASE PS.ps_Count_last_year " + 
					"	WHEN 0 THEN " + 
					"		PS.ps_Count*100 " + 
					"	ELSE " + 
					"		round( cast(((PS.ps_Count-PS.ps_Count_last_year)*100) AS numeric)/cast(PS.ps_Count_last_year AS numeric)) " + 
					"END AS variancepercent " + 
					" FROM " + 
					"	(" + 
					"	SELECT" + 
					"		u.u_first_name," + 
					"		u.u_last_name," + 
					"		gu.gu_group_pkid," + 
					"		u.pk_id," + 
					"		u.u_first_name || ' ' || u.u_last_name fullname," + 
					"		COALESCE (" + 
					"			(" + 
					"			SELECT COUNT" + 
					"				( 1 ) " + 
					"			FROM" + 
					"				lp_presentation ps " + 
					"			WHERE" + 
					"				ps.ps_create_user_pkid = u.pk_id " + 
					"				AND ps_create_timestamp > :startDate " + 
					"				AND ps_create_timestamp < :endDate " + 
					"			GROUP BY" + 
					"				ps_create_user_pkid " + 
					"			)," + 
					"			0 " + 
					"		) ps_Count," + 
					"		COALESCE (" + 
					"			(" + 
					"			SELECT COUNT" + 
					"				( 1 ) " + 
					"			FROM" + 
					"				lp_presentation ps " + 
					"			WHERE" + 
					"				ps.ps_create_user_pkid = u.pk_id " + 
					"				AND ps_create_timestamp > (date(:startDate) - interval '1Y') " + 
					"				AND ps_create_timestamp < (date(:endDate) - interval '1Y') " + 
					"			GROUP BY" + 
					"				ps_create_user_pkid " + 
					"			)," + 
					"			0 " + 
					"		) ps_Count_last_year," + 
					"		COALESCE (" + 
					"			(" + 
					"			SELECT COUNT" + 
					"				( 1 ) " + 
					"			FROM " + 
					"				lp_presentation ps " + 
					"			WHERE" + 
					"				ps.ps_create_user_pkid = u.pk_id " + 
					"				AND ps_create_timestamp > to_date(to_char(current_date,'YYYY-MM-01'),'yyyy-mm-dd') " + 
					"				AND ps_create_timestamp < (to_date(to_char(current_date+interval'1Months','YYYY-MM-01'),'yyyy-mm-dd')-1) " + 
					"			GROUP BY" + 
					"				ps_create_user_pkid " + 
					"			)," + 
					"			0 " + 
					"		) ps_Count_current_month " + 
					"	FROM " + 
					"		lp_user u" + 
					"		LEFT JOIN ( SELECT gu_user_pkid, gu_group_pkid FROM lp_group_user GROUP BY gu_user_pkid, gu_group_pkid ) gu ON u.pk_id = gu.gu_user_pkid " + 
					"	) PS ) PS " + 
					"	where ps.gu_group_pkid in ( " + 
					"	WITH RECURSIVE r AS ( " + 
					"		SELECT pk_id FROM lp_group WHERE pk_id = :salesGroupId  " + 
					"		UNION ALL " + 
					"		SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id  " + 
					"	) SELECT pk_id FROM	r) " + 
					"	AND (" + 
					"		LOWER ( PS.u_first_name ) LIKE'%' || LOWER ( :keyword ) || '%' " + 
					"		OR LOWER ( PS.u_last_name ) LIKE'%' || LOWER ( :keyword ) || '%' " + 
					"	OR LOWER ( PS.fullname ) LIKE'%' || LOWER ( :keyword ) || '%' " + 
					"	)) A"
			,nativeQuery = true)
	Page<ReviewTeam> findTeamReviewNativeQuery(@Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate, @Param(value = "salesGroupId") Long salesGroupId,
                                               @Param(value = "keyword") String keyword, Pageable pageable);
}
