select group0_.pk_id as pk_id1_1_, group0_.g_cocktails_limit as g_cockta2_1_, group0_.g_create_date as g_create3_1_, 
group0_.g_distributor_pkid as g_distri4_1_, group0_.g_include_default_cocktails as g_includ5_1_, group0_.g_name as g_name6_1_, 
group0_.g_parent_pkid as g_parent7_1_, group0_.g_state as g_state8_1_, group0_.g_type as g_type9_1_ 
from lp_group group0_ where group0_.g_type='sales'

select * from lp_cocktail;
from lp_group group0_ where group0_.g_type='sales'


update lp_group set g_parent_pkid = 2 where g_parent_pkid = 1
commit;
select * from mappingingredient

insert into lp_group_user(gu_user_pkid,gu_group_pkid, gu_role ) values (2,4,'MANAGER')

drop table lp_group_user

CREATE TABLE lp_group_user
(
    pk_id BIGSERIAL NOT NULL,
    gu_user_pkid bigint,
    gu_group_pkid bigint,
    gu_role character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT lp_group_user_pkey PRIMARY KEY (pk_id)
)
;