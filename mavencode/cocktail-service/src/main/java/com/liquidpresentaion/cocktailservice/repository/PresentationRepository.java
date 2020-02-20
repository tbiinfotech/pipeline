package com.liquidpresentaion.cocktailservice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.liquidpresentaion.cocktailservice.model.Presentation;

public interface PresentationRepository extends PagingAndSortingRepository<Presentation, Integer>{

	Page<Presentation> findByTitleIgnoreCaseContaining(String keyword, Pageable of);
	Page<Presentation> findByDateBetweenAndTitleIgnoreCaseContaining(Date startDate, Date endDate, String keyword, Pageable of);
	Page<Presentation> findByDateAfterAndTitleIgnoreCaseContaining(Date startDate, String keyword, Pageable of);
	Page<Presentation> findByDateBeforeAndTitleIgnoreCaseContaining(Date endDate, String keyword, Pageable of);
	Page<Presentation> findByDateBetween(Date startDate, Date endDate, Pageable pageRequest);
	Page<Presentation> findByDateAfter(Date startDate, Pageable pageRequest);
	Page<Presentation> findByDateBefore(Date endDate, Pageable pageRequest);
	long countByCreatePkId(int userId);
	List<Presentation> findByCreateDateBetweenAndCreatePkIdIsOrderByCustomerAcctName(Date date, Date date2, Integer createPkId);
	List<Presentation> findByCreateDateBetweenAndCreatePkIdInOrderByCustomerAcctName(Date date, Date date2, List<Integer> createPkId);
	List<Presentation> findByCreateDateBetweenAndIdInOrderByCustomerAcctName(Date date, Date date2, List<Integer> Id);
	boolean existsByTitleIgnoreCase(String title);
	boolean existsByTitleIgnoreCaseAndIdIsNot(String title, int id);
	
//	@Query(value = "select p from Presentation p WHERE (:startDate is null or p.date > :startDate) "
//			+ "and (:endDate is null or p.date < :endDate) "
//			+ "and (:keyword is null or lower(COALESCE(p.title, 'cat')) like lower(concat('%', COALESCE(:keyword::varchar, 'cat'),'%'))) "
//			+ "and p.createPkId = :currentUserPkid ",
//	nativeQuery = false)
	@Query(value = "select * from LP_Presentation p WHERE (:startDate is null or p.ps_date > (CAST(:startDate as Date))) "
			+ "and (:endDate is null or p.ps_date < to_date(:endDate, 'YYYYMMDD')) "
			+ "and (:keyword is null or lower(p.ps_title) like lower(concat('%', :keyword,'%'))) "
			+ "and p.createPkId = :currentUserPkid ",
	nativeQuery = true)
	Page<Presentation> findSelfPresentationByNativeQuery(@Param(value = "startDate")Date startDate, @Param(value = "endDate")Date endDate, @Param(value = "keyword")String keyword, Pageable of, @Param(value = "currentUserPkid")Integer currentUserPkid);
	Page<Presentation> findAll(Example<Presentation> of, Pageable pageRequest);
	Page<Presentation> findAll(Specification<Presentation> presentationsSearchSpecification, Pageable pageRequest);
	
//	@Query(value = "					SELECT * " + 
//			"					FROM " + 
//			"						lp_presentation_cocktail pc " + 
//			"						JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " + 
//			"					WHERE " + 
//			"						pc.pc_cocktail_pkid IN (" + 
//			"						SELECT ct.pk_id FROM " + 
//			"				lp_cocktail ct " + 
//			"				JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid  " + 
//			"				JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id  " + 
//			"				JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " + 
//			"				AND gu.gu_group_pkid IN (" + 
//			"					WITH RECURSIVE r AS (" + 
//			"					SELECT pk_id FROM	lp_group WHERE pk_id = :salesGroupId " + 
//			"					UNION ALL " + 
//			"					SELECT lp_group.pk_id FROM lp_group,r " + 
//			"					WHERE " + 
//			"						lp_group.g_parent_pkid = r.pk_id " + 
//			"					) SELECT pk_id FROM	r " + 
//			"				)" + 
//			"						" + 
//			"						) " + 
//			"						AND ps_create_timestamp > :startDate " + 
//			"						AND ps_create_timestamp < :endDate " + 
//			"						ORDER BY ps_customer_account_name",nativeQuery = true)
//	List<Presentation> findPresentationNativeQuery (@Param(value = "startDate") Date startDate, @Param(value = "endDate")Date endDate,@Param(value = "salesGroupId") Integer salesGroupId);
	
	@Query(value = "SELECT " + 
			"	*  " + 
			"FROM " + 
			"	lp_presentation " + 
			"	P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid " + 
			"	JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid  " + 
			"	AND gu.gu_group_pkid IN ( " + 
			"		WITH RECURSIVE r AS ( " + 
			"		SELECT " + 
			"			pk_id  " + 
			"		FROM " + 
			"			lp_group  " + 
			"		WHERE " + 
			"			pk_id = :salesGroupId UNION ALL " + 
			"		SELECT " + 
			"			lp_group.pk_id  " + 
			"		FROM " + 
			"			lp_group, " + 
			"			r  " + 
			"		WHERE " + 
			"			lp_group.g_parent_pkid = r.pk_id  " + 
			"		) SELECT " + 
			"		pk_id  " + 
			"	FROM " + 
			"		r  " + 
			"	)  " + 
			"WHERE " + 
			"	EXISTS ( SELECT 1 FROM lp_cocktail_group ctg WHERE ctg.ctg_cocktail_pkid = pc.pc_cocktail_pkid )  " + 
			"	AND P.ps_create_timestamp > :startDate  " + 
			"	AND P.ps_create_timestamp < :endDate "+ 
			"ORDER BY ps_customer_account_name"
			,nativeQuery = true)
	List<Presentation> findPresentationNativeQueryBySaleGroup (@Param(value = "startDate") Date startDate, @Param(value = "endDate")Date endDate,@Param(value = "salesGroupId") Integer salesGroupId);

	
	@Query(value = "					SELECT * " + 
			"					FROM " + 
			"						lp_presentation_cocktail pc " + 
			"						JOIN lp_presentation ps ON pc.pc_presentation_pkid = ps.pk_id " + 
			"					WHERE " + 
			"						pc.pc_cocktail_pkid IN (" + 
			"						SELECT ct.pk_id FROM " + 
			"				lp_cocktail ct " + 
			"				JOIN lp_presentation_cocktail pc ON ct.pk_id = pc.pc_cocktail_pkid  " + 
			"				JOIN lp_presentation P ON pc.pc_presentation_pkid = P.pk_id  " + 
			"				JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid " + 
			"				AND gu.gu_group_pkid IN (" + 
			"					WITH RECURSIVE r AS (" + 
			"					SELECT pk_id FROM	lp_group WHERE pk_id = :salesGroupId " + 
			"					UNION ALL " + 
			"					SELECT lp_group.pk_id FROM lp_group,r " + 
			"					WHERE " + 
			"						lp_group.g_parent_pkid = r.pk_id " + 
			"					) SELECT pk_id FROM	r " + 
			"				)" + 
			"						" + 
			"						) " + 
			"						AND ps_create_timestamp > :startDate " + 
			"						AND ps_create_timestamp < :endDate " + 
			"						ORDER BY ps_customer_account_name",nativeQuery = true)
	List<Presentation> findPresentationNativeQuery (@Param(value = "startDate") Date startDate, @Param(value = "endDate")Date endDate,@Param(value = "salesGroupId") Integer salesGroupId);

	
	@Query(value = "SELECT " + 
			"	*  " + 
			"FROM " + 
			"	lp_presentation " + 
			"	P JOIN lp_presentation_cocktail pc ON P.pk_id = pc.pc_presentation_pkid " + 
			"	JOIN lp_group_user gu ON P.ps_create_user_pkid = gu.gu_user_pkid  " + 
			"	AND gu.gu_group_pkid IN ( " + 
			"		WITH RECURSIVE r AS ( " + 
			"		SELECT " + 
			"			pk_id  " + 
			"		FROM " + 
			"			lp_group  " + 
			"		WHERE " + 
			"			pk_id = :salesGroupId UNION ALL " + 
			"		SELECT " + 
			"			lp_group.pk_id  " + 
			"		FROM " + 
			"			lp_group, " + 
			"			r  " + 
			"		WHERE " + 
			"			lp_group.g_parent_pkid = r.pk_id  " + 
			"		) SELECT " + 
			"		pk_id  " + 
			"	FROM " + 
			"		r  " + 
			"	)  " + 
			"WHERE " + 
			"	pc.pc_cocktail_pkid = :cocktailId " + 
			"	AND P.ps_create_timestamp > :startDate  " + 
			"	AND P.ps_create_timestamp < :endDate " + 
			"	ORDER BY P.ps_customer_account_name " ,nativeQuery = true)
	List<Presentation> findPresentationNativeQuery (@Param(value = "startDate") Date startDate, @Param(value = "endDate")Date endDate,
													@Param(value = "salesGroupId") Integer salesGroupId,@Param(value = "cocktailId") Integer cocktailId);
	
	@Query(value = "SELECT * FROM lp_presentation ps " + 
			"		JOIN (SELECT gu_user_pkid,gu_group_pkid FROM (SELECT * FROM lp_user u " + 
			"		LEFT JOIN ( SELECT gu_user_pkid, gu_group_pkid FROM lp_group_user GROUP BY gu_user_pkid, gu_group_pkid ) gu ON u.pk_id = gu.gu_user_pkid ) ps " + 
			"		WHERE " + 
			"		ps.gu_group_pkid in (  " + 
			"		WITH RECURSIVE r AS ( " + 
			"		SELECT pk_id FROM lp_group WHERE pk_id = :salesGroupId " + 
			"		UNION ALL  " + 
			"		SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id  " + 
			"		) SELECT pk_id FROM	r)) gup " + 
			"		ON ps.ps_create_user_pkid = gup.gu_user_pkid " + 
			"		WHERE ps_create_timestamp > :startDate " + 
			"  		AND ps_create_timestamp < :endDate ",nativeQuery = true)
	List<Presentation> findPresentationTeamNativeQuery (@Param(value = "startDate") Date startDate, @Param(value = "endDate")Date endDate,@Param(value = "salesGroupId") Integer salesGroupId);
}
