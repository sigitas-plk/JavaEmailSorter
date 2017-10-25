/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */

package IMAPC.AccountActions;

import javax.mail.*;

import org.apache.log4j.Logger;

import IMAPC.GUI;

import java.io.File;
import java.util.Properties;

/**
 * Klase Connection.
 */
public class Connection {
	
	/** Sukuriame logger kuriuo irasinesime visas ivykusias klaidas ir kitus ivykius. */
	Logger logger = Logger.getLogger(Connection.class);

	/** Failas su login duomenimis. */
	private File failas;

	/** USER_NAME_TAG User Name zyme xml failiuke. */
	private final String USER_NAME_TAG = "UserName";

	/** PASSWORD_TAG Password zyme xml failiuke. */
	private final String PASSWORD_TAG = "Password";

	/** HOST_TAG Serverio zyme xml failiuke. */
	private final String HOST_TAG = "MailServer";

	/** PORT_TAG Porto zyme zml failiuke. */
	private final String PORT_TAG = "Port";

	/** CONNECTION_TYPE_TAG Jungimosi tipo zyme xml failiuke. */
	private final String CONNECTION_TYPE_TAG = "ConnectionType";

	/** DEFAULT_IMAP_CONNECTION numatytasis imap prisijungimo tipas xml failiuke (secured/unsecured). */
	private final String DEFAULT_IMAP_CONNECTION = "Secured";

	/** DEFAULT_IMAP_SERVER numatytasis serveris. */
	private final String DEFAULT_IMAP_SERVER = "imap.google.com";

	/** DEFAULT_IMAP_PORT numatytasis portas. */
	private final String DEFAULT_IMAP_PORT = "143";
	
	/** The DEFAUL t_ ima p_ secur e_ port. */
	private final String DEFAULT_IMAP_SECURE_PORT = "993";

	/** Rysio tipas. */
	boolean secured;

	
	/**
	 * Connection konstruktorius, skirtas nustatyti prisijungimo duomenu failo kintamaji
	 * 
	 * @param failas prisijungimo duomenu failas
	 */
	public Connection(File failas) {
		this.failas = failas;

	}

	/**
	 * Sukuria store.
	 * 
	 * @return store
	 */
	public Store getStore() {
		String[] data = getConfData();
		secured = true;
		Store store = null;
		String protokolas = "imaps";
		Properties props = System.getProperties();
		/*
		 * Jei bus naudojamas saugus prisijungimas bus jungiamasi imaps, o jei
		 * paprastas imap protokolu
		 */
		if (data[4].equals("Unsecured")) {
			secured = false;
		}

		if (!secured) {
			protokolas = "imap";
		} else {

			protokolas = "imaps";
		}

		props.put("mail." + protokolas + ".host", data[0]);
		props.put("mail." + protokolas + ".port", data[1]);
		Session session = Session.getInstance(props);
		session.setDebug(true);

		try {

			store = session.getStore(protokolas);
			store.connect(data[2], data[3]);
		} catch (NoSuchProviderException e) {
			logger.error("Such Provider doesn't exist:" + data[0]+e.getMessage());
			logger.info("Such Provider doesn't exist:" + data[0]);
			logger.warn("Toks el. pasto serveris neegzistuoja:" + data[0]);
			GUI.spausdinti();
			try {
				Thread.sleep(60);
			} catch (InterruptedException e1) {
				//nekreipiam demesio
			}
			System.exit(1);
		} catch (MessagingException e) {
			logger.error("Unable to connect to server: " + data[0]+" "+e.getMessage());
			logger.info("Unable to connect to server: " + data[0]+". System will exit.");
			logger.warn("Nepavyko prisijungti prie pašto serverio: " + data[0]+". Stabdomas programos veikimas.");
			GUI.spausdinti();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				//nekreipiam demesio
			}
			System.exit(1);
		} catch (Exception e) {
			logger.error("Error ocured: " + e.getMessage());
			System.exit(1);
		}

		return store;
	}

	/**
	 * Uzdaro naudojama store.
	 * 
	 * @param store naudojamas store
	 */
	public void closeStore(Store store) {
		try {

			store.close();

		} catch (MessagingException e) {
			logger.error("Can't close store."+e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Nuskaitome duomenis is failo.
	 * 
	 * @return accounto duomenis
	 */
	private String[] getConfData() {
		Configuration configuration = new Configuration();
		String[] data = new String[5];
		String portas;
		String server;
		String user;
		String pass;
		String connectionType;
		String nenuskaityta = "false";

		/*
		 * jei nera faile default reiksme jungiasi prie numatyto serverio
		 */
		if (nenuskaityta.equals(configuration.readLoginData(this.failas,
				HOST_TAG))) {

			server = DEFAULT_IMAP_SERVER;
			/*
			 * nuskaitome hosta is xml failo
			 */
		} else {

			server = configuration.readLoginData(this.failas, HOST_TAG);

		}

		/*
		 * nuskaitome user name
		 */
		user = configuration.readLoginData(this.failas, USER_NAME_TAG);
		if (!user.equals(nenuskaityta)) {

			/*
			 * nuskaitome password
			 */
			pass = configuration.readLoginData(this.failas, PASSWORD_TAG);
			if (!pass.equals(nenuskaityta)) {
				/*
				 * isaugome duomenis y masyva
				 */

				if (nenuskaityta.equals(configuration.readLoginData(
						this.failas, CONNECTION_TYPE_TAG))) {

					connectionType = DEFAULT_IMAP_CONNECTION;

				} else {

					connectionType = configuration.readLoginData(this.failas,
							CONNECTION_TYPE_TAG);
				}

				/*
				 * nuskaitome porta is xml failo
				 */
				String StringPortas = configuration.readLoginData(this.failas,
						PORT_TAG);

				/*
				 * jei xml nera parasyto porto naudojama default reiksme
				 */
				if (StringPortas.equals(nenuskaityta)
						&& connectionType.equals("Secured")) {

					portas = DEFAULT_IMAP_SECURE_PORT;

				} else if (StringPortas.equals(nenuskaityta)
						&& connectionType.equals("Unsecured")) {

					portas = DEFAULT_IMAP_PORT;

				} else {

					portas = StringPortas;
				}

				data[0] = server;
				data[1] = portas;
				data[2] = user;
				data[3] = pass;
				data[4] = connectionType;
			} else {
				logger.error("There is no password data in xml file.");
				logger.info("There is no password data in xml file. System will exit.");
				logger.warn("Xml faile nerastas prisijungimo slaptazodis. Stabdomas programos veikimas.");
				GUI.spausdinti();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					//nekreipiam demesio
				}
				System.exit(1);
			}
		} else {
			logger.error("There is no User Name data in xml file.");
			logger.info("There is no User Name data in xml file. System will exit.");
			logger.warn("Xml faile nerastas vartotojo vardas. Stabdomas programos veikimas.");
			GUI.spausdinti();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e1) {
				//nekreipiam demesio
			}
			System.exit(1);
		}

		return data;
	}

}
