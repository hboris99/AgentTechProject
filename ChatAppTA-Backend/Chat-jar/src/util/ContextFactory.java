package util;

import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public abstract class ContextFactory {
	private static final Logger logger = Logger.getLogger(ContextFactory.class.getName());
	private static Context context;

	
	static {
		try {
			Hashtable<String, Object> jndiProps = new Hashtable<>();
			jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			context = new InitialContext(jndiProps);
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, "Context initialization error.", ex);
		}
	}

	public static Context get() {
		
		return context;
}
}
