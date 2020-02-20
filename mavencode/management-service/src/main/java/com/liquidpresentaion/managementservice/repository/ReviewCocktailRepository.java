package com.liquidpresentaion.managementservice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.managementservice.model.ReviewCocktail;


@Repository
public interface ReviewCocktailRepository extends PagingAndSortingRepository<ReviewCocktail, Integer>{

	// 最外边多套一层是因为新起的别名不能排序
	@Query(value = "WITH cocktailCount1 AS (" + 
			"	SELECT" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp," + 
			"		COUNT ( 1 ) AS cc_count " + 
			"	FROM" + 
			"		lp_presentation" + 
			"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" + 
			"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " + 
			"		AND gu.gu_group_pkid IN (" + 
			"			WITH RECURSIVE r AS (" + 
			"			SELECT" + 
			"				pk_id " + 
			"			FROM" + 
			"				lp_group " + 
			"			WHERE" + 
			"				pk_id = :salesGroupId UNION ALL" + 
			"			SELECT" + 
			"				lp_group.pk_id " + 
			"			FROM" + 
			"				lp_group," + 
			"				r " + 
			"			WHERE" + 
			"				lp_group.g_parent_pkid = r.pk_id " + 
			"			) SELECT" + 
			"			pk_id " + 
			"		FROM" + 
			"			r " + 
			"		) " + 
			"	WHERE " + 
			"		ps_create_timestamp > :startDate AND ps_create_timestamp < :endDate" + 
			"	GROUP BY" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp " + 
			"	) " + 
			"	" + 
			", cocktailCount2 AS (" + 
			"	SELECT" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp," + 
			"		COUNT ( 1 ) AS cc_count " + 
			"	FROM" + 
			"		lp_presentation" + 
			"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" + 
			"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " + 
			"		AND gu.gu_group_pkid IN (" + 
			"			WITH RECURSIVE r AS (" + 
			"			SELECT" + 
			"				pk_id " + 
			"			FROM" + 
			"				lp_group " + 
			"			WHERE" + 
			"				pk_id = :salesGroupId UNION ALL" + 
			"			SELECT" + 
			"				lp_group.pk_id " + 
			"			FROM" + 
			"				lp_group," + 
			"				r " + 
			"			WHERE" + 
			"				lp_group.g_parent_pkid = r.pk_id " + 
			"			) SELECT" + 
			"			pk_id " + 
			"		FROM" + 
			"			r " + 
			"		) " + 
			"		WHERE ps_create_timestamp > ( to_date( :startDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " + 
			"					AND ps_create_timestamp < ( to_date( :endDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " + 
			"	GROUP BY" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp " + 
			"	) " + 
			"	" + 
			", cocktailCount3 AS (" + 
			"	SELECT" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp," + 
			"		COUNT ( 1 ) AS cc_count " + 
			"	FROM" + 
			"		lp_presentation" + 
			"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" + 
			"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " + 
			"		AND gu.gu_group_pkid IN (" + 
			"			WITH RECURSIVE r AS (" + 
			"			SELECT" + 
			"				pk_id " + 
			"			FROM" + 
			"				lp_group " + 
			"			WHERE" + 
			"				pk_id = :salesGroupId UNION ALL" + 
			"			SELECT" + 
			"				lp_group.pk_id " + 
			"			FROM" + 
			"				lp_group," + 
			"				r " + 
			"			WHERE" + 
			"				lp_group.g_parent_pkid = r.pk_id " + 
			"			) SELECT" + 
			"			pk_id " + 
			"		FROM" + 
			"			r " + 
			"		) " + 
			"		WHERE ps_create_timestamp > date_trunc( 'month', CURRENT_DATE )" + 
			"	GROUP BY" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp " + 
			"	) "+
			"SELECT * FROM (SELECT *, " + 
			"										(ps_Count - ps_Count_last_year) variance,    " + 
			"										CASE ps_Count_last_year " + 
			"										WHEN 0 THEN   " + 
			"														ps_Count*100    " + 
			"												ELSE     " + 
			"														round( cast(((ps_Count-ps_Count_last_year)*100) AS numeric)/cast(ps_Count_last_year AS numeric))    " + 
			"												END AS variancepercent    " + 
			"										FROM (    " + 
			"									SELECT DISTINCT    " + 
			"										ct.pk_id,      " + 
			"										ct.ct_name,   " + 
			"										ct.ct_brand_pkid,    " + 
			"										ct.ct_supplier_pkid,    " + 
			"										b.br_name brand_Name,  " + 
			"										gr.g_name supplier_Group_Name,    " + 
			"										COALESCE (  " + 
			"											(   " + 
			"											SELECT SUM(cc_count) as  cocktailCount " + 
			"											FROM " + 
			"					 							cocktailCount1 cc " + 
			"											WHERE " + 
			"												cc.pc_cocktail_pkid = ct.pk_id " + 
			"											),     " + 
			"											0      " + 
			"										) ps_Count,  " + 
			"										COALESCE (    " + 
			"											(   " + 
			"											SELECT SUM(cc_count) as cocktailCount " + 
			"											FROM " + 
			"					 							cocktailCount2 cc" + 
			"											WHERE " + 
			"												cc.pc_cocktail_pkid = ct.pk_id   " + 
			"											),   " + 
			"											0   " + 
			"										) ps_Count_last_year,   " + 
			"										COALESCE (   " + 
			"											(   " + 
			"											SELECT SUM(cc_count) as  cocktailCount " + 
			"											FROM " + 
			"					 							cocktailCount3 cc " + 
			"											WHERE " + 
			"												cc.pc_cocktail_pkid = ct.pk_id  " + 
			"											), " + 
			"											0    " + 
			"										) ps_Count_current_month     " + 
			"									 FROM      " + 
			"										lp_cocktail ct   " + 
			"										LEFT OUTER JOIN lp_brand b ON ct.ct_brand_pkid = b.pk_id     " + 
			"										LEFT OUTER JOIN lp_group gr ON ct.ct_supplier_pkid = gr.pk_id     " + 
			"										JOIN lp_cocktail_group ctg ON ctg.ctg_cocktail_pkid = ct.pk_id   " + 
			"										JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid     " + 
			"										JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id    " + 
			"										JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid   " + 
			"										AND ctg.ctg_group_pkid IN (   " + 
			"											WITH RECURSIVE r AS (    " + 
			"											SELECT pk_id FROM	lp_group WHERE pk_id = :salesGroupId    " + 
			"											UNION ALL    " + 
			"											SELECT lp_group.pk_id FROM lp_group,r    " + 
			"											WHERE   " + 
			"												lp_group.g_parent_pkid = r.pk_id     " + 
			"											) SELECT pk_id FROM	r     " + 
			"										)    " + 
			"						  				) PC) PC",
			countQuery = "WITH cocktailCount1 AS (" +
					"	SELECT" +
					"		pc_cocktail_pkid," +
					"		ps_create_timestamp," +
					"		COUNT ( 1 ) AS cc_count " +
					"	FROM" +
					"		lp_presentation" +
					"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" +
					"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
					"		AND gu.gu_group_pkid IN (" +
					"			WITH RECURSIVE r AS (" +
					"			SELECT" +
					"				pk_id " +
					"			FROM" +
					"				lp_group " +
					"			WHERE" +
					"				pk_id = :salesGroupId UNION ALL" +
					"			SELECT" +
					"				lp_group.pk_id " +
					"			FROM" +
					"				lp_group," +
					"				r " +
					"			WHERE" +
					"				lp_group.g_parent_pkid = r.pk_id " +
					"			) SELECT" +
					"			pk_id " +
					"		FROM" +
					"			r " +
					"		) " +
					"	WHERE " +
					"		ps_create_timestamp > :startDate AND ps_create_timestamp < :endDate" +
					"	GROUP BY" +
					"		pc_cocktail_pkid," +
					"		ps_create_timestamp " +
					"	) " +
					"	" +
					", cocktailCount2 AS (" +
					"	SELECT" +
					"		pc_cocktail_pkid," +
					"		ps_create_timestamp," +
					"		COUNT ( 1 ) AS cc_count " +
					"	FROM" +
					"		lp_presentation" +
					"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" +
					"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
					"		AND gu.gu_group_pkid IN (" +
					"			WITH RECURSIVE r AS (" +
					"			SELECT" +
					"				pk_id " +
					"			FROM" +
					"				lp_group " +
					"			WHERE" +
					"				pk_id = :salesGroupId UNION ALL" +
					"			SELECT" +
					"				lp_group.pk_id " +
					"			FROM" +
					"				lp_group," +
					"				r " +
					"			WHERE" +
					"				lp_group.g_parent_pkid = r.pk_id " +
					"			) SELECT" +
					"			pk_id " +
					"		FROM" +
					"			r " +
					"		) " +
					"		WHERE ps_create_timestamp > ( to_date( :startDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
					"					AND ps_create_timestamp < ( to_date( :endDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
					"	GROUP BY" +
					"		pc_cocktail_pkid," +
					"		ps_create_timestamp " +
					"	) " +
					"	" +
					", cocktailCount3 AS (" +
					"	SELECT" +
					"		pc_cocktail_pkid," +
					"		ps_create_timestamp," +
					"		COUNT ( 1 ) AS cc_count " +
					"	FROM" +
					"		lp_presentation" +
					"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" +
					"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
					"		AND gu.gu_group_pkid IN (" +
					"			WITH RECURSIVE r AS (" +
					"			SELECT" +
					"				pk_id " +
					"			FROM" +
					"				lp_group " +
					"			WHERE" +
					"				pk_id = :salesGroupId UNION ALL" +
					"			SELECT" +
					"				lp_group.pk_id " +
					"			FROM" +
					"				lp_group," +
					"				r " +
					"			WHERE" +
					"				lp_group.g_parent_pkid = r.pk_id " +
					"			) SELECT" +
					"			pk_id " +
					"		FROM" +
					"			r " +
					"		) " +
					"		WHERE ps_create_timestamp > date_trunc( 'month', CURRENT_DATE )" +
					"	GROUP BY" +
					"		pc_cocktail_pkid," +
					"		ps_create_timestamp " +
					"	) "+
					"SELECT count(1) FROM (SELECT *, " +
					"										(ps_Count - ps_Count_last_year) variance,    " +
					"										CASE ps_Count_last_year " +
					"										WHEN 0 THEN   " +
					"														ps_Count*100    " +
					"												ELSE     " +
					"														round( cast(((ps_Count-ps_Count_last_year)*100) AS numeric)/cast(ps_Count_last_year AS numeric))    " +
					"												END AS variancepercent    " +
					"										FROM (    " +
					"									SELECT DISTINCT    " +
					"										ct.pk_id,      " +
					"										ct.ct_name,   " +
					"										ct.ct_brand_pkid,    " +
					"										ct.ct_supplier_pkid,    " +
					"										b.br_name brand_Name,  " +
					"										gr.g_name supplier_Group_Name,    " +
					"										COALESCE (  " +
					"											(   " +
					"											SELECT SUM(cc_count) as  cocktailCount " +
					"											FROM " +
					"					 							cocktailCount1 cc " +
					"											WHERE " +
					"												cc.pc_cocktail_pkid = ct.pk_id " +
					"											),     " +
					"											0      " +
					"										) ps_Count,  " +
					"										COALESCE (    " +
					"											(   " +
					"											SELECT SUM(cc_count) as cocktailCount " +
					"											FROM " +
					"					 							cocktailCount2 cc" +
					"											WHERE " +
					"												cc.pc_cocktail_pkid = ct.pk_id   " +
					"											),   " +
					"											0   " +
					"										) ps_Count_last_year,   " +
					"										COALESCE (   " +
					"											(   " +
					"											SELECT SUM(cc_count) as  cocktailCount " +
					"											FROM " +
					"					 							cocktailCount3 cc " +
					"											WHERE " +
					"												cc.pc_cocktail_pkid = ct.pk_id  " +
					"											), " +
					"											0    " +
					"										) ps_Count_current_month     " +
					"									 FROM      " +
					"										lp_cocktail ct   " +
					"										LEFT OUTER JOIN lp_brand b ON ct.ct_brand_pkid = b.pk_id     " +
					"										LEFT OUTER JOIN lp_group gr ON ct.ct_supplier_pkid = gr.pk_id     " +
					"										JOIN lp_cocktail_group ctg ON ctg.ctg_cocktail_pkid = ct.pk_id   " +
					"										JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid     " +
					"										JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id    " +
					"										JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid   " +
					"										AND ctg.ctg_group_pkid IN (   " +
					"											WITH RECURSIVE r AS (    " +
					"											SELECT pk_id FROM	lp_group WHERE pk_id = :salesGroupId    " +
					"											UNION ALL    " +
					"											SELECT lp_group.pk_id FROM lp_group,r    " +
					"											WHERE   " +
					"												lp_group.g_parent_pkid = r.pk_id     " +
					"											) SELECT pk_id FROM	r     " +
					"										)    " +
					"						  				) PC) PC",
			nativeQuery = true)
	List<ReviewCocktail> findCocktailReviewQueryByCocktailGroup(@Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate,
                                                                @Param(value = "salesGroupId") Long salesGroupId);
	
	@Query(value = "WITH cocktailCount1 AS (" +
            "	SELECT" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp," +
            "		COUNT ( 1 ) AS cc_count " +
            "	FROM" +
            "		lp_presentation_cocktail pc" +
            "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
            "	WHERE" +
            "		ps_create_timestamp > :startDate " +
            "		AND ps_create_timestamp < :endDate " +
            "	GROUP BY" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp " +
            "	)," +
            "	cocktailCount2 AS (" +
            "	SELECT" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp," +
            "		COUNT ( 1 ) AS cc_count " +
            "	FROM" +
            "		lp_presentation_cocktail pc" +
            "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
            "	WHERE" +
            "		ps_create_timestamp > ( to_date( :startDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
            "		AND ps_create_timestamp < ( to_date( :endDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
            "	GROUP BY" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp " +
            "	)," +
            "	cocktailCount3 AS (" +
            "	SELECT" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp," +
            "		COUNT ( 1 ) AS cc_count " +
            "	FROM" +
            "		lp_presentation_cocktail pc" +
            "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
            "	WHERE" +
            "		ps_create_timestamp > date_trunc( 'month', CURRENT_DATE ) " +
            "	GROUP BY" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp " +
            "	) " +
            "SELECT * FROM (" +
            "	SELECT" +
            "		*," +
            "		( ps_Count - ps_Count_last_year ) variance," +
            "	CASE" +
            "			ps_Count_last_year " +
            "			WHEN 0 THEN" +
            "			ps_Count * 100 ELSE round(" +
            "			CAST ((( ps_Count - ps_Count_last_year ) * 100 ) AS NUMERIC ) / CAST ( ps_Count_last_year AS NUMERIC )) " +
            "		END AS variancepercent " +
            "	FROM (" +
            "		SELECT DISTINCT" +
            "			ct.pk_id," +
            "			ct.ct_name," +
            "			ct.ct_brand_pkid," +
            "			ct.ct_supplier_pkid," +
            "			b.br_name brand_Name," +
            "			gr.g_name supplier_Group_Name," +
            "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount1 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count," +
            "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount2 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count_last_year," +
            "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount3 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count_current_month " +
            "		FROM" +
            "			lp_cocktail ct" +
            "			LEFT OUTER JOIN lp_brand b ON ct.ct_brand_pkid = b.pk_id" +
            "			LEFT OUTER JOIN lp_group gr ON ct.ct_supplier_pkid = gr.pk_id" +
            "			JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid" +
            "			JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id" +
            "			JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
            "			AND gu.gu_group_pkid IN (" +
            "				WITH RECURSIVE r AS (" +
            "				SELECT" +
            "					pk_id " +
            "				FROM" +
            "					lp_group " +
            "				WHERE" +
            "					pk_id = :salesGroupId UNION ALL" +
            "				SELECT" +
            "					lp_group.pk_id " +
            "				FROM" +
            "					lp_group," +
            "					r " +
            "				WHERE" +
            "					lp_group.g_parent_pkid = r.pk_id " +
            "				) SELECT" +
            "				pk_id " +
            "			FROM r )" +
            "		) PC) PC ",
            countQuery = "WITH cocktailCount1 AS (" +
                    "	SELECT" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp," +
                    "		COUNT ( 1 ) AS cc_count " +
                    "	FROM" +
                    "		lp_presentation_cocktail pc" +
                    "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
                    "	WHERE" +
                    "		ps_create_timestamp > :startDate " +
                    "		AND ps_create_timestamp < :endDate " +
                    "	GROUP BY" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp " +
                    "	)," +
                    "	cocktailCount2 AS (" +
                    "	SELECT" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp," +
                    "		COUNT ( 1 ) AS cc_count " +
                    "	FROM" +
                    "		lp_presentation_cocktail pc" +
                    "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
                    "	WHERE" +
                    "		ps_create_timestamp > ( to_date( :startDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
                    "		AND ps_create_timestamp < ( to_date( :endDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
                    "	GROUP BY" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp " +
                    "	)," +
                    "	cocktailCount3 AS (" +
                    "	SELECT" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp," +
                    "		COUNT ( 1 ) AS cc_count " +
                    "	FROM" +
                    "		lp_presentation_cocktail pc" +
                    "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
                    "	WHERE" +
                    "		ps_create_timestamp > date_trunc( 'month', CURRENT_DATE ) " +
                    "	GROUP BY" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp " +
                    "	) " +
                    "SELECT count(1) FROM (" +
                    "	SELECT" +
                    "		*," +
                    "		( ps_Count - ps_Count_last_year ) variance," +
                    "	CASE" +
                    "			ps_Count_last_year " +
                    "			WHEN 0 THEN" +
                    "			ps_Count * 100 ELSE round(" +
                    "			CAST ((( ps_Count - ps_Count_last_year ) * 100 ) AS NUMERIC ) / CAST ( ps_Count_last_year AS NUMERIC )) " +
                    "		END AS variancepercent " +
                    "	FROM (" +
                    "		SELECT DISTINCT" +
                    "			ct.pk_id," +
                    "			ct.ct_name," +
                    "			ct.ct_brand_pkid," +
                    "			ct.ct_supplier_pkid," +
                    "			b.br_name brand_Name," +
                    "			gr.g_name supplier_Group_Name," +
                    "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount1 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count," +
                    "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount2 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count_last_year," +
                    "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount3 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count_current_month " +
                    "		FROM" +
                    "			lp_cocktail ct" +
                    "			LEFT OUTER JOIN lp_brand b ON ct.ct_brand_pkid = b.pk_id" +
                    "			LEFT OUTER JOIN lp_group gr ON ct.ct_supplier_pkid = gr.pk_id" +
                    "			JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid" +
                    "			JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id" +
                    "			JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
                    "			AND gu.gu_group_pkid IN (" +
                    "				WITH RECURSIVE r AS (" +
                    "				SELECT" +
                    "					pk_id " +
                    "				FROM" +
                    "					lp_group " +
                    "				WHERE" +
                    "					pk_id = :salesGroupId UNION ALL" +
                    "				SELECT" +
                    "					lp_group.pk_id " +
                    "				FROM" +
                    "					lp_group," +
                    "					r " +
                    "				WHERE" +
                    "					lp_group.g_parent_pkid = r.pk_id " +
                    "				) SELECT" +
                    "				pk_id " +
                    "			FROM r )" +
                    "		) PC) PC ",
			nativeQuery = true)
	Page<ReviewCocktail> findCocktailReviewNativeQuery(@Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate,
                                                       @Param(value = "salesGroupId") Long salesGroupId,
                                                       Pageable pageable);
	
	// 最外边多套一层是因为新起的别名不能排序
	@Query(value = "WITH cocktailCount1 AS (" + 
			"	SELECT" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp," + 
			"		COUNT ( 1 ) AS cc_count " + 
			"	FROM" + 
			"		lp_presentation" + 
			"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" + 
			"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " + 
			"		AND gu.gu_group_pkid IN (" + 
			"			WITH RECURSIVE r AS (" + 
			"			SELECT" + 
			"				pk_id " + 
			"			FROM" + 
			"				lp_group " + 
			"			WHERE" + 
			"				pk_id = :salesGroupId UNION ALL" + 
			"			SELECT" + 
			"				lp_group.pk_id " + 
			"			FROM" + 
			"				lp_group," + 
			"				r " + 
			"			WHERE" + 
			"				lp_group.g_parent_pkid = r.pk_id " + 
			"			) SELECT" + 
			"			pk_id " + 
			"		FROM" + 
			"			r " + 
			"		) " + 
			"	WHERE " + 
			"		ps_create_timestamp > :startDate AND ps_create_timestamp < :endDate" + 
			"	GROUP BY" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp " + 
			"	) " + 
			"	" + 
			", cocktailCount2 AS (" + 
			"	SELECT" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp," + 
			"		COUNT ( 1 ) AS cc_count " + 
			"	FROM" + 
			"		lp_presentation" + 
			"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" + 
			"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " + 
			"		AND gu.gu_group_pkid IN (" + 
			"			WITH RECURSIVE r AS (" + 
			"			SELECT" + 
			"				pk_id " + 
			"			FROM" + 
			"				lp_group " + 
			"			WHERE" + 
			"				pk_id = :salesGroupId UNION ALL" + 
			"			SELECT" + 
			"				lp_group.pk_id " + 
			"			FROM" + 
			"				lp_group," + 
			"				r " + 
			"			WHERE" + 
			"				lp_group.g_parent_pkid = r.pk_id " + 
			"			) SELECT" + 
			"			pk_id " + 
			"		FROM" + 
			"			r " + 
			"		) " + 
			"		WHERE ps_create_timestamp > ( to_date( :startDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " + 
			"					AND ps_create_timestamp < ( to_date( :endDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " + 
			"	GROUP BY" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp " + 
			"	) " + 
			"	" + 
			", cocktailCount3 AS (" + 
			"	SELECT" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp," + 
			"		COUNT ( 1 ) AS cc_count " + 
			"	FROM" + 
			"		lp_presentation" + 
			"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" + 
			"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " + 
			"		AND gu.gu_group_pkid IN (" + 
			"			WITH RECURSIVE r AS (" + 
			"			SELECT" + 
			"				pk_id " + 
			"			FROM" + 
			"				lp_group " + 
			"			WHERE" + 
			"				pk_id = :salesGroupId UNION ALL" + 
			"			SELECT" + 
			"				lp_group.pk_id " + 
			"			FROM" + 
			"				lp_group," + 
			"				r " + 
			"			WHERE" + 
			"				lp_group.g_parent_pkid = r.pk_id " + 
			"			) SELECT" + 
			"			pk_id " + 
			"		FROM" + 
			"			r " + 
			"		) " + 
			"		WHERE ps_create_timestamp > date_trunc( 'month', CURRENT_DATE )" + 
			"	GROUP BY" + 
			"		pc_cocktail_pkid," + 
			"		ps_create_timestamp " + 
			"	) "+
			" SELECT * FROM (SELECT *, " + 
			"										(ps_Count - ps_Count_last_year) variance,    " + 
			"										CASE ps_Count_last_year " + 
			"										WHEN 0 THEN   " + 
			"														ps_Count*100    " + 
			"												ELSE     " + 
			"														round( cast(((ps_Count-ps_Count_last_year)*100) AS numeric)/cast(ps_Count_last_year AS numeric))    " + 
			"												END AS variancepercent    " + 
			"										FROM (    " + 
			"									SELECT DISTINCT    " + 
			"										ct.pk_id,      " + 
			"										ct.ct_name,   " + 
			"										ct.ct_brand_pkid,    " + 
			"										ct.ct_supplier_pkid,    " + 
			"										b.br_name brand_Name,  " + 
			"										gr.g_name supplier_Group_Name,    " + 
			"										COALESCE (  " + 
			"											(   " + 
			"											SELECT SUM(cc_count) as  cocktailCount " + 
			"											FROM " + 
			"					 							cocktailCount1 cc " + 
			"											WHERE " + 
			"												cc.pc_cocktail_pkid = ct.pk_id " + 
			"											),     " + 
			"											0      " + 
			"										) ps_Count,  " + 
			"										COALESCE (    " + 
			"											(   " + 
			"											SELECT SUM(cc_count) as cocktailCount " + 
			"											FROM " + 
			"					 							cocktailCount2 cc" + 
			"											WHERE " + 
			"												cc.pc_cocktail_pkid = ct.pk_id   " + 
			"											),   " + 
			"											0   " + 
			"										) ps_Count_last_year,   " + 
			"										COALESCE (   " + 
			"											(   " + 
			"											SELECT SUM(cc_count) as  cocktailCount " + 
			"											FROM " + 
			"					 							cocktailCount3 cc " + 
			"											WHERE " + 
			"												cc.pc_cocktail_pkid = ct.pk_id  " + 
			"											), " + 
			"											0    " + 
			"										) ps_Count_current_month     " + 
			"									 FROM      " + 
			"										lp_cocktail ct   " + 
			"										LEFT OUTER JOIN lp_brand b ON ct.ct_brand_pkid = b.pk_id     " + 
			"										LEFT OUTER JOIN lp_group gr ON ct.ct_supplier_pkid = gr.pk_id     " + 
			"										JOIN lp_cocktail_group ctg ON ctg.ctg_cocktail_pkid = ct.pk_id   " + 
			"										JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid     " + 
			"										JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id    " + 
			"										JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid   " + 
			"										AND ctg.ctg_group_pkid IN (   " + 
			"											WITH RECURSIVE r AS (    " + 
			"											SELECT pk_id FROM	lp_group WHERE pk_id = :salesGroupId    " + 
			"											UNION ALL    " + 
			"											SELECT lp_group.pk_id FROM lp_group,r    " + 
			"											WHERE   " + 
			"												lp_group.g_parent_pkid = r.pk_id     " + 
			"											) SELECT pk_id FROM	r     " + 
			"										)    " + 
			"						  				) PC " + 
			"	WHERE " + 
			"		LOWER ( ct_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " + 
			"		OR LOWER ( brand_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " + 
			"	OR LOWER ( supplier_group_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " + 
			"	) P ",
					countQuery = "WITH cocktailCount1 AS (" +
						"	SELECT" +
						"		pc_cocktail_pkid," +
						"		ps_create_timestamp," +
						"		COUNT ( 1 ) AS cc_count " +
						"	FROM" +
						"		lp_presentation" +
						"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" +
						"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
						"		AND gu.gu_group_pkid IN (" +
						"			WITH RECURSIVE r AS (" +
						"			SELECT" +
						"				pk_id " +
						"			FROM" +
						"				lp_group " +
						"			WHERE" +
						"				pk_id = :salesGroupId UNION ALL" +
						"			SELECT" +
						"				lp_group.pk_id " +
						"			FROM" +
						"				lp_group," +
						"				r " +
						"			WHERE" +
						"				lp_group.g_parent_pkid = r.pk_id " +
						"			) SELECT" +
						"			pk_id " +
						"		FROM" +
						"			r " +
						"		) " +
						"	WHERE " +
						"		ps_create_timestamp > :startDate AND ps_create_timestamp < :endDate" +
						"	GROUP BY" +
						"		pc_cocktail_pkid," +
						"		ps_create_timestamp " +
						"	) " +
						"	" +
						", cocktailCount2 AS (" +
						"	SELECT" +
						"		pc_cocktail_pkid," +
						"		ps_create_timestamp," +
						"		COUNT ( 1 ) AS cc_count " +
						"	FROM" +
						"		lp_presentation" +
						"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" +
						"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
						"		AND gu.gu_group_pkid IN (" +
						"			WITH RECURSIVE r AS (" +
						"			SELECT" +
						"				pk_id " +
						"			FROM" +
						"				lp_group " +
						"			WHERE" +
						"				pk_id = :salesGroupId UNION ALL" +
						"			SELECT" +
						"				lp_group.pk_id " +
						"			FROM" +
						"				lp_group," +
						"				r " +
						"			WHERE" +
						"				lp_group.g_parent_pkid = r.pk_id " +
						"			) SELECT" +
						"			pk_id " +
						"		FROM" +
						"			r " +
						"		) " +
						"		WHERE ps_create_timestamp > ( to_date( :startDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
						"					AND ps_create_timestamp < ( to_date( :endDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
						"	GROUP BY" +
						"		pc_cocktail_pkid," +
						"		ps_create_timestamp " +
						"	) " +
						"	" +
						", cocktailCount3 AS (" +
						"	SELECT" +
						"		pc_cocktail_pkid," +
						"		ps_create_timestamp," +
						"		COUNT ( 1 ) AS cc_count " +
						"	FROM" +
						"		lp_presentation" +
						"		P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid" +
						"		JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
						"		AND gu.gu_group_pkid IN (" +
						"			WITH RECURSIVE r AS (" +
						"			SELECT" +
						"				pk_id " +
						"			FROM" +
						"				lp_group " +
						"			WHERE" +
						"				pk_id = :salesGroupId UNION ALL" +
						"			SELECT" +
						"				lp_group.pk_id " +
						"			FROM" +
						"				lp_group," +
						"				r " +
						"			WHERE" +
						"				lp_group.g_parent_pkid = r.pk_id " +
						"			) SELECT" +
						"			pk_id " +
						"		FROM" +
						"			r " +
						"		) " +
						"		WHERE ps_create_timestamp > date_trunc( 'month', CURRENT_DATE )" +
						"	GROUP BY" +
						"		pc_cocktail_pkid," +
						"		ps_create_timestamp " +
						"	) "+
						" SELECT count(1) FROM (SELECT *, " +
						"										(ps_Count - ps_Count_last_year) variance,    " +
						"										CASE ps_Count_last_year " +
						"										WHEN 0 THEN   " +
						"														ps_Count*100    " +
						"												ELSE     " +
						"														round( cast(((ps_Count-ps_Count_last_year)*100) AS numeric)/cast(ps_Count_last_year AS numeric))    " +
						"												END AS variancepercent    " +
						"										FROM (    " +
						"									SELECT DISTINCT    " +
						"										ct.pk_id,      " +
						"										ct.ct_name,   " +
						"										ct.ct_brand_pkid,    " +
						"										ct.ct_supplier_pkid,    " +
						"										b.br_name brand_Name,  " +
						"										gr.g_name supplier_Group_Name,    " +
						"										COALESCE (  " +
						"											(   " +
						"											SELECT SUM(cc_count) as  cocktailCount " +
						"											FROM " +
						"					 							cocktailCount1 cc " +
						"											WHERE " +
						"												cc.pc_cocktail_pkid = ct.pk_id " +
						"											),     " +
						"											0      " +
						"										) ps_Count,  " +
						"										COALESCE (    " +
						"											(   " +
						"											SELECT SUM(cc_count) as cocktailCount " +
						"											FROM " +
						"					 							cocktailCount2 cc" +
						"											WHERE " +
						"												cc.pc_cocktail_pkid = ct.pk_id   " +
						"											),   " +
						"											0   " +
						"										) ps_Count_last_year,   " +
						"										COALESCE (   " +
						"											(   " +
						"											SELECT SUM(cc_count) as  cocktailCount " +
						"											FROM " +
						"					 							cocktailCount3 cc " +
						"											WHERE " +
						"												cc.pc_cocktail_pkid = ct.pk_id  " +
						"											), " +
						"											0    " +
						"										) ps_Count_current_month     " +
						"									 FROM      " +
						"										lp_cocktail ct   " +
						"										LEFT OUTER JOIN lp_brand b ON ct.ct_brand_pkid = b.pk_id     " +
						"										LEFT OUTER JOIN lp_group gr ON ct.ct_supplier_pkid = gr.pk_id     " +
						"										JOIN lp_cocktail_group ctg ON ctg.ctg_cocktail_pkid = ct.pk_id   " +
						"										JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid     " +
						"										JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id    " +
						"										JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid   " +
						"										AND ctg.ctg_group_pkid IN (   " +
						"											WITH RECURSIVE r AS (    " +
						"											SELECT pk_id FROM	lp_group WHERE pk_id = :salesGroupId    " +
						"											UNION ALL    " +
						"											SELECT lp_group.pk_id FROM lp_group,r    " +
						"											WHERE   " +
						"												lp_group.g_parent_pkid = r.pk_id     " +
						"											) SELECT pk_id FROM	r     " +
						"										)    " +
						"						  				) PC " +
						"	WHERE " +
						"		LOWER ( ct_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " +
						"		OR LOWER ( brand_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " +
						"	OR LOWER ( supplier_group_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " +
						"	) P ",
					nativeQuery = true)
	Page<ReviewCocktail> findCocktailReviewQueryByCocktailGroup(@Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate, @Param(value = "keyword") String keyword,
                                                                @Param(value = "salesGroupId") Long salesGroupId, Pageable pageable);
	
	@Query(value = "WITH cocktailCount1 AS (" +
            "	SELECT" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp," +
            "		COUNT ( 1 ) AS cc_count " +
            "	FROM" +
            "		lp_presentation_cocktail pc" +
            "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
            "	WHERE" +
            "		ps_create_timestamp > :startDate " +
            "		AND ps_create_timestamp < :endDate " +
            "	GROUP BY" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp " +
            "	)," +
            "	cocktailCount2 AS (" +
            "	SELECT" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp," +
            "		COUNT ( 1 ) AS cc_count " +
            "	FROM" +
            "		lp_presentation_cocktail pc" +
            "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
            "	WHERE" +
            "		ps_create_timestamp > ( to_date( :startDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
            "		AND ps_create_timestamp < ( to_date( :endDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
            "	GROUP BY" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp " +
            "	)," +
            "	cocktailCount3 AS (" +
            "	SELECT" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp," +
            "		COUNT ( 1 ) AS cc_count " +
            "	FROM" +
            "		lp_presentation_cocktail pc" +
            "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
            "	WHERE" +
            "		ps_create_timestamp > date_trunc( 'month', CURRENT_DATE ) " +
            "	GROUP BY" +
            "		pc_cocktail_pkid," +
            "		ps_create_timestamp " +
            "	) " +
            "SELECT * FROM (" +
            "	SELECT" +
            "		*," +
            "		( ps_Count - ps_Count_last_year ) variance," +
            "	CASE" +
            "			ps_Count_last_year " +
            "			WHEN 0 THEN" +
            "			ps_Count * 100 ELSE round(" +
            "			CAST ((( ps_Count - ps_Count_last_year ) * 100 ) AS NUMERIC ) / CAST ( ps_Count_last_year AS NUMERIC )) " +
            "		END AS variancepercent " +
            "	FROM (" +
            "		SELECT DISTINCT" +
            "			ct.pk_id," +
            "			ct.ct_name," +
            "			ct.ct_brand_pkid," +
            "			ct.ct_supplier_pkid," +
            "			b.br_name brand_Name," +
            "			gr.g_name supplier_Group_Name," +
            "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount1 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count," +
            "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount2 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count_last_year," +
            "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount3 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count_current_month " +
            "		FROM" +
            "			lp_cocktail ct" +
            "			LEFT OUTER JOIN lp_brand b ON ct.ct_brand_pkid = b.pk_id" +
            "			LEFT OUTER JOIN lp_group gr ON ct.ct_supplier_pkid = gr.pk_id" +
            "			JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid" +
            "			JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id" +
            "			JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
            "			AND gu.gu_group_pkid IN (" +
            "				WITH RECURSIVE r AS (" +
            "				SELECT" +
            "					pk_id " +
            "				FROM" +
            "					lp_group " +
            "				WHERE" +
            "					pk_id = :salesGroupId UNION ALL" +
            "				SELECT" +
            "					lp_group.pk_id " +
            "				FROM" +
            "					lp_group," +
            "					r " +
            "				WHERE" +
            "					lp_group.g_parent_pkid = r.pk_id " +
            "				) SELECT" +
            "				pk_id " +
            "			FROM r )" +
            "		) PC"+
            "	WHERE " +
            "		LOWER ( ct_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " +
            "		OR LOWER ( brand_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " +
            "	OR LOWER ( supplier_group_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " +
            " ) PC",
            countQuery = "WITH cocktailCount1 AS (" +
                    "	SELECT" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp," +
                    "		COUNT ( 1 ) AS cc_count " +
                    "	FROM" +
                    "		lp_presentation_cocktail pc" +
                    "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
                    "	WHERE" +
                    "		ps_create_timestamp > :startDate " +
                    "		AND ps_create_timestamp < :endDate " +
                    "	GROUP BY" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp " +
                    "	)," +
                    "	cocktailCount2 AS (" +
                    "	SELECT" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp," +
                    "		COUNT ( 1 ) AS cc_count " +
                    "	FROM" +
                    "		lp_presentation_cocktail pc" +
                    "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
                    "	WHERE" +
                    "		ps_create_timestamp > ( to_date( :startDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
                    "		AND ps_create_timestamp < ( to_date( :endDate, 'YYYYMMDD' ) - INTERVAL '1 year' ) " +
                    "	GROUP BY" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp " +
                    "	)," +
                    "	cocktailCount3 AS (" +
                    "	SELECT" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp," +
                    "		COUNT ( 1 ) AS cc_count " +
                    "	FROM" +
                    "		lp_presentation_cocktail pc" +
                    "		JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " +
                    "	WHERE" +
                    "		ps_create_timestamp > date_trunc( 'month', CURRENT_DATE ) " +
                    "	GROUP BY" +
                    "		pc_cocktail_pkid," +
                    "		ps_create_timestamp " +
                    "	) " +
                    "SELECT count(1) FROM (" +
                    "	SELECT" +
                    "		*," +
                    "		( ps_Count - ps_Count_last_year ) variance," +
                    "	CASE" +
                    "			ps_Count_last_year " +
                    "			WHEN 0 THEN" +
                    "			ps_Count * 100 ELSE round(" +
                    "			CAST ((( ps_Count - ps_Count_last_year ) * 100 ) AS NUMERIC ) / CAST ( ps_Count_last_year AS NUMERIC )) " +
                    "		END AS variancepercent " +
                    "	FROM (" +
                    "		SELECT DISTINCT" +
                    "			ct.pk_id," +
                    "			ct.ct_name," +
                    "			ct.ct_brand_pkid," +
                    "			ct.ct_supplier_pkid," +
                    "			b.br_name brand_Name," +
                    "			gr.g_name supplier_Group_Name," +
                    "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount1 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count," +
                    "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount2 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count_last_year," +
                    "			COALESCE ( ( SELECT SUM ( cc_count ) AS cocktailCount FROM cocktailCount3 cc WHERE cc.pc_cocktail_pkid = ct.pk_id ), 0 ) ps_Count_current_month " +
                    "		FROM" +
                    "			lp_cocktail ct" +
                    "			LEFT OUTER JOIN lp_brand b ON ct.ct_brand_pkid = b.pk_id" +
                    "			LEFT OUTER JOIN lp_group gr ON ct.ct_supplier_pkid = gr.pk_id" +
                    "			JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid" +
                    "			JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id" +
                    "			JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " +
                    "			AND gu.gu_group_pkid IN (" +
                    "				WITH RECURSIVE r AS (" +
                    "				SELECT" +
                    "					pk_id " +
                    "				FROM" +
                    "					lp_group " +
                    "				WHERE" +
                    "					pk_id = :salesGroupId UNION ALL" +
                    "				SELECT" +
                    "					lp_group.pk_id " +
                    "				FROM" +
                    "					lp_group," +
                    "					r " +
                    "				WHERE" +
                    "					lp_group.g_parent_pkid = r.pk_id " +
                    "				) SELECT" +
                    "				pk_id " +
                    "			FROM r )" +
                    "		) PC"+
                    "	WHERE " +
                    "		LOWER ( ct_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " +
                    "		OR LOWER ( brand_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " +
                    "	OR LOWER ( supplier_group_name ) LIKE'%' || LOWER ( :keyword ) || '%'  " +
                    " ) PC",
					nativeQuery = true)
	Page<ReviewCocktail> findCocktailReviewNativeQuery(@Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate,
                                                       @Param(value = "keyword") String keyword, @Param(value = "salesGroupId") Long salesGroupId,
                                                       Pageable pageable);
	
	
	@Query(value = "select ct.pk_id, ct.ct_name, ct.ct_brand_pkid, ct.ct_supplier_pkid, b.br_name brand_Name, gr.g_name supplier_Group_Name,  "
			+ "coalesce ( (select count(1) cocktailCount from lp_presentation_cocktail pc join lp_presentation ps on pc.pc_presentation_pkid = ps.pk_id "
					+ "where ct.pk_id = pc.pc_cocktail_pkid and ps_create_timestamp > :startDate and ps_create_timestamp < :endDate "
					+ "group by pc_cocktail_pkid), 0) ps_Count, "
			+ "coalesce ( (select count(1) cocktailCount from lp_presentation_cocktail pc join lp_presentation ps on pc.pc_presentation_pkid = ps.pk_id "
					+ "where ct.pk_id = pc.pc_cocktail_pkid and ps_create_timestamp > (to_date(:startDate, 'YYYYMMDD') - INTERVAL '1 year') and ps_create_timestamp < (to_date(:endDate, 'YYYYMMDD') - INTERVAL '1 year') "
					+ "group by pc_cocktail_pkid), 0) ps_Count_last_year, "
			+ "coalesce ( (select count(1) cocktailCount from lp_presentation_cocktail pc join lp_presentation ps on pc.pc_presentation_pkid = ps.pk_id "
					+ "where ct.pk_id = pc.pc_cocktail_pkid and ps_create_timestamp > date_trunc('month', CURRENT_DATE) "
					+ "group by pc_cocktail_pkid), 0) ps_Count_current_month "
			+ "from lp_cocktail ct "
			+ "left outer join lp_brand b on ct.ct_brand_pkid = b.pk_id "
			+ "left outer join lp_group gr on ct.ct_supplier_pkid = gr.pk_id " 
			+ "where ct.ct_create_user_pkid in (select distinct gu_user_pkid from lp_group_user where gu_group_pkid = :salesGroupId) ", 
			nativeQuery = true)
	List<ReviewCocktail> findCocktailReviewNativeQuery(@Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate, @Param(value = "salesGroupId") Integer salesGroupId);
	
	@Query(value = "select ct.pk_id, ct.ct_name, ct.ct_brand_pkid, ct.ct_supplier_pkid, b.br_name brand_Name, gr.g_name supplier_Group_Name,  "
			+ "coalesce ( (select count(1) cocktailCount from lp_presentation_cocktail pc join lp_presentation ps on pc.pc_presentation_pkid = ps.pk_id "
					+ "where ct.pk_id = pc.pc_cocktail_pkid and ps_create_timestamp > :startDate and ps_create_timestamp < :endDate "
					+ "group by pc_cocktail_pkid), 0) ps_Count, "
			+ "coalesce ( (select count(1) cocktailCount from lp_presentation_cocktail pc join lp_presentation ps on pc.pc_presentation_pkid = ps.pk_id "
					+ "where ct.pk_id = pc.pc_cocktail_pkid and ps_create_timestamp > (to_date(:startDate, 'YYYYMMDD') - INTERVAL '1 year') and ps_create_timestamp < (to_date(:endDate, 'YYYYMMDD') - INTERVAL '1 year') "
					+ "group by pc_cocktail_pkid), 0) ps_Count_last_year, "
			+ "coalesce ( (select count(1) cocktailCount from lp_presentation_cocktail pc join lp_presentation ps on pc.pc_presentation_pkid = ps.pk_id "
					+ "where ct.pk_id = pc.pc_cocktail_pkid and ps_create_timestamp > date_trunc('month', CURRENT_DATE) "
					+ "group by pc_cocktail_pkid), 0) ps_Count_current_month "
			+ "from lp_cocktail ct "
			+ "left outer join lp_brand b on ct.ct_brand_pkid = b.pk_id "
			+ "left outer join lp_group gr on ct.ct_supplier_pkid = gr.pk_id " 
			+ "where ct.ct_create_user_pkid in (select distinct gu_user_pkid from lp_group_user where gu_group_pkid = :salesGroupId) " 
			+ "and ( LOWER ( ct.ct_name ) LIKE'%' || LOWER ( :keyword ) || '%' "
			+ "or LOWER ( b.br_name ) LIKE'%' || LOWER ( :keyword ) || '%'  "
			+ "or LOWER ( gr.g_name ) LIKE'%' || LOWER ( :keyword ) || '%' )", 
			nativeQuery = true)
	List<ReviewCocktail> findCocktailReviewNativeQuery(@Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate, @Param(value = "keyword") String keyword, @Param(value = "salesGroupId") Integer salesGroupId);
		
	@Query(value = "select ps.pk_id as id,  ps_customer_account_id as customerAccountId, ps_customer_account_name as customerAcctName "
					+ "from lp_presentation ps join lp_presentation_cocktail pc on ps.pk_id = pc.pc_presentation_pkid and pc.pc_cocktail_pkid = :cocktailPkid "
					+ "where ps_create_timestamp > :startDate and ps_create_timestamp < :endDate "
					+ "order by ps.pk_id desc ",
			nativeQuery = true)
	List<Presentation> findPresentationNativeQuery(@Param(value = "cocktailPkid") Integer cocktailPkid, @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate);
	public static interface Presentation {

		int getId();
	    String getCustomerAccountId();
	    String getCustomerAcctName();
	  }
	
}
