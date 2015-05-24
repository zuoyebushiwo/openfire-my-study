package org.jivesoftware.openfire.user;

import java.io.Externalizable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jivesoftware.util.cache.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.resultsetmanagement.Result;

/**
 * Encapsulates information about a user. New users are created using
 * {@link UserManager#createUser(String, String, String, String)}. All user
 * properties are loaded on demand and are read from the <tt>ofUserProp</tt>
 * database table. The currently-installed {@link UserProvider} is used for
 * setting all other user data and some operations may not be supported
 * depending on the capabilities of the {@link UserProvider}.
 *
 * @author Matt Tucker
 */
public class User implements Cacheable, Externalizable, Result {
	
	private static final Logger Log = LoggerFactory.getLogger(User.class);

    private static final String LOAD_PROPERTIES =
        "SELECT name, propValue FROM ofUserProp WHERE username=?";
    private static final String LOAD_PROPERTY =
        "SELECT propValue FROM ofUserProp WHERE username=? AND name=?";
    private static final String DELETE_PROPERTY =
        "DELETE FROM ofUserProp WHERE username=? AND name=?";
    private static final String UPDATE_PROPERTY =
        "UPDATE ofUserProp SET propValue=? WHERE name=? AND username=?";
    private static final String INSERT_PROPERTY =
        "INSERT INTO ofUserProp (username, name, propValue) VALUES (?, ?, ?)";
    
    // The name of the name visible property
    private static final String NAME_VISIBLE_PROPERTY = "name.visible";
    // The name of the email visible property
    private static final String EMAIL_VISIBLE_PROPERTY = "email.visible";
    
    private String username;
    private String name;
    private String email;
    private Date creationDate;
    private Date modificationDate;

    private Map<String,String> properties = null;
    
    /**
     * Returns the value of the specified property for the given username. This method is
     * an optimization to avoid loading a user to get a specific property.
     *
     * @param username the username of the user to get a specific property value.
     * @param propertyName the name of the property to return its value.
     * @return the value of the specified property for the given username.
     */
    public static String getPropertyValue(String username, String propertyName) {
        String propertyValue = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(LOAD_PROPERTY);
            pstmt.setString(1, username);
            pstmt.setString(2, propertyName);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                propertyValue = rs.getString(1);
            }
        }
        catch (SQLException sqle) {
            Log.error(sqle.getMessage(), sqle);
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        return propertyValue;
    }

}
