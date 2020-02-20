package com.liquidpresentaion.authenticationservice.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.ApplicationContextException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import com.liquidpresentaion.authenticationservice.model.LpUserDetails;

public class JpaRepositoryImpl implements UserDetailsService {
	//~ Static fields/initializers =====================================================================================

    public static final String DEF_USERS_BY_USERNAME_QUERY =
            "select u_email as email, u_password as password, true as enabled " +
            "from lp_user  " +
            "where u_email = ?";
    public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
            "select distinct u_email as email, gu_role as authority " +
            "from lp_user u join lp_group_user g on u.pk_id = g.gu_user_pkid " +
            "where u.u_email = ?";
    public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY =
            "select g.id, g.group_name, ga.authority " +
            "from groups g, group_members gm, group_authorities ga " +
            "where gm.username = ? " +
            "and g.id = ga.group_id " +
            "and g.id = gm.group_id";

    //~ Instance fields ================================================================================================

    protected final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private String authoritiesByUsernameQuery;
    private String groupAuthoritiesByUsernameQuery;
    private String usersByUsernameQuery;
    private String rolePrefix = "";
    private boolean usernameBasedPrimaryKey = true;
    private boolean enableAuthorities = false;
    private boolean enableGroups = false;
    
    @PersistenceContext
    private EntityManager em;

    //~ Constructors ===================================================================================================

    public JpaRepositoryImpl() {
        usersByUsernameQuery = DEF_USERS_BY_USERNAME_QUERY;
        authoritiesByUsernameQuery = DEF_AUTHORITIES_BY_USERNAME_QUERY;
        groupAuthoritiesByUsernameQuery = DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY;
    }

    //~ Methods ========================================================================================================

    /**
     * Allows subclasses to add their own granted authorities to the list to be returned in the <tt>UserDetails</tt>.
     *
     * @param username the username, for use by finder methods
     * @param authorities the current granted authorities, as populated from the <code>authoritiesByUsername</code>
     *        mapping
     */
    protected void addCustomAuthorities(String username, List<GrantedAuthority> authorities) {}

    public String getUsersByUsernameQuery() {
        return usersByUsernameQuery;
    }

    protected void initDao() throws ApplicationContextException {
        Assert.isTrue(enableAuthorities || enableGroups, "Use of either authorities or groups must be enabled");
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetails> users = loadUsersByUsername(username);

        if (users.size() == 0) {

            throw new UsernameNotFoundException(
                    messages.getMessage("JdbcDaoImpl.notFound", new Object[]{username}, "Username {0} not found"));
        }

        UserDetails user = users.get(0); // contains no GrantedAuthority[]

        Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();

        dbAuthsSet.addAll(user.getAuthorities());
        
        if (enableAuthorities) {
            dbAuthsSet.addAll(loadUserAuthorities(username));
        }

        if (enableGroups) {
            dbAuthsSet.addAll(loadGroupAuthorities(username));
        }

        List<GrantedAuthority> dbAuths = new ArrayList<GrantedAuthority>(dbAuthsSet);

        addCustomAuthorities(user.getUsername(), dbAuths);

        if (dbAuths.size() == 0) {

            throw new UsernameNotFoundException(
                    messages.getMessage("JdbcDaoImpl.noAuthority",
                            new Object[] {username}, "User {0} has no GrantedAuthority"));
        }

        return createUserDetails(username, user, dbAuths);
    }

    /**
     * Executes the SQL <tt>usersByUsernameQuery</tt> and returns a list of UserDetails objects.
     * There should normally only be one matching user.
     */
    @SuppressWarnings("unchecked")
	protected List<UserDetails> loadUsersByUsername(String username) {
//    	List resultList = em.createNativeQuery(usersByUsernameQuery, LpUserDetails.class).setParameter(1, username).getResultList();
//    	for (Object obj : resultList) {
//			System.out.println("======>>>>>>>>" + obj.getClass().getName());
//		}
        return em.createNativeQuery(usersByUsernameQuery, LpUserDetails.class).setParameter(1, username).getResultList();
    }

    /**
     * Loads authorities by executing the SQL from <tt>authoritiesByUsernameQuery</tt>.
     *
     * @return a list of GrantedAuthority objects for the user
     */
    @SuppressWarnings("unchecked")
	protected List<GrantedAuthority> loadUserAuthorities(String username) {
        return em.createNativeQuery(authoritiesByUsernameQuery, GrantedAuthority.class).setParameter(1, username).getResultList();
    }

    /**
     * Loads authorities by executing the SQL from <tt>groupAuthoritiesByUsernameQuery</tt>.
     *
     * @return a list of GrantedAuthority objects for the user
     */
    @SuppressWarnings("unchecked")
	protected List<GrantedAuthority> loadGroupAuthorities(String username) {
        return em.createNativeQuery(groupAuthoritiesByUsernameQuery, GrantedAuthority.class).setParameter(1, username).getResultList();
    }

    /**
     * Can be overridden to customize the creation of the final UserDetailsObject which is
     * returned by the <tt>loadUserByUsername</tt> method.
     *
     * @param username the name originally passed to loadUserByUsername
     * @param userFromUserQuery the object returned from the execution of the
     * @param combinedAuthorities the combined array of authorities from all the authority loading queries.
     * @return the final UserDetails which should be used in the system.
     */
    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
            List<GrantedAuthority> combinedAuthorities) {
        String returnUsername = userFromUserQuery.getUsername();

        if (!usernameBasedPrimaryKey) {
            returnUsername = username;
        }

        return new User(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(),
                true, true, true, combinedAuthorities);
    }

    /**
     * Allows the default query string used to retrieve authorities based on username to be overridden, if
     * default table or column names need to be changed. The default query is {@link
     * #DEF_AUTHORITIES_BY_USERNAME_QUERY}; when modifying this query, ensure that all returned columns are mapped
     * back to the same column names as in the default query.
     *
     * @param queryString The SQL query string to set
     */
    public void setAuthoritiesByUsernameQuery(String queryString) {
        authoritiesByUsernameQuery = queryString;
    }

    protected String getAuthoritiesByUsernameQuery() {
        return authoritiesByUsernameQuery;
    }

    /**
     * Allows the default query string used to retrieve group authorities based on username to be overridden, if
     * default table or column names need to be changed. The default query is {@link
     * #DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY}; when modifying this query, ensure that all returned columns are mapped
     * back to the same column names as in the default query.
     *
     * @param queryString The SQL query string to set
     */
    public void setGroupAuthoritiesByUsernameQuery(String queryString) {
        groupAuthoritiesByUsernameQuery = queryString;
    }

    /**
     * Allows a default role prefix to be specified. If this is set to a non-empty value, then it is
     * automatically prepended to any roles read in from the db. This may for example be used to add the
     * <tt>ROLE_</tt> prefix expected to exist in role names (by default) by some other Spring Security
     * classes, in the case that the prefix is not already present in the db.
     *
     * @param rolePrefix the new prefix
     */
    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    protected String getRolePrefix() {
        return rolePrefix;
    }

    /**
     * If <code>true</code> (the default), indicates the {@link #getUsersByUsernameQuery()} returns a username
     * in response to a query. If <code>false</code>, indicates that a primary key is used instead. If set to
     * <code>true</code>, the class will use the database-derived username in the returned <code>UserDetails</code>.
     * If <code>false</code>, the class will use the {@link #loadUserByUsername(String)} derived username in the
     * returned <code>UserDetails</code>.
     *
     * @param usernameBasedPrimaryKey <code>true</code> if the mapping queries return the username <code>String</code>,
     *        or <code>false</code> if the mapping returns a database primary key.
     */
    public void setUsernameBasedPrimaryKey(boolean usernameBasedPrimaryKey) {
        this.usernameBasedPrimaryKey = usernameBasedPrimaryKey;
    }

    protected boolean isUsernameBasedPrimaryKey() {
        return usernameBasedPrimaryKey;
    }

    /**
     * Allows the default query string used to retrieve users based on username to be overridden, if default
     * table or column names need to be changed. The default query is {@link #DEF_USERS_BY_USERNAME_QUERY}; when
     * modifying this query, ensure that all returned columns are mapped back to the same column names as in the
     * default query. If the 'enabled' column does not exist in the source database, a permanent true value for this
     * column may be returned by using a query similar to
     * <pre>
     * "select username,password,'true' as enabled from users where username = ?"
     * </pre>
     *
     * @param usersByUsernameQueryString The query string to set
     */
    public void setUsersByUsernameQuery(String usersByUsernameQueryString) {
        this.usersByUsernameQuery = usersByUsernameQueryString;
    }

    protected boolean getEnableAuthorities() {
        return enableAuthorities;
    }

    /**
     * Enables loading of authorities (roles) from the authorities table. Defaults to true
     */
    public void setEnableAuthorities(boolean enableAuthorities) {
        this.enableAuthorities = enableAuthorities;
    }

    protected boolean getEnableGroups() {
        return enableGroups;
    }

    /**
     * Enables support for group authorities. Defaults to false
     * @param enableGroups
     */
    public void setEnableGroups(boolean enableGroups) {
        this.enableGroups = enableGroups;
    }
}
