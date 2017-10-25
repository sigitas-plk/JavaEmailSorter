/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */
package IMAPC.AccountActions;

import java.io.File;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import javax.mail.Store;

import org.apache.log4j.Logger;

import IMAPC.GUI;
import IMAPC.AccountActions.Constants;

/**
 * Klase ImapActions.
 */
public class ImapActions {

	/** Naudojamas logger. */
	Logger logger = Logger.getLogger(ImapActions.class);

	/**  pasto dezutes turinio saugojimui skirtas kintamasis. */
	private Store store;

	/**
	 * ImapActions konstruktorius.
	 * Sukuria Connection objekta.
	 * 
	 * @param failas prisijungimo duomenu failas
	 */
	public ImapActions(File failas) {
		Connection connection = new Connection(failas);
		this.store = connection.getStore();
	}

	/** Inicijuojame ImapMessage klase. */
	ImapMessage imapm = new ImapMessage();

	/** Inicijuojame ImapFolder klase. */
	ImapFolder imapf = new ImapFolder();

	/**
	 * Gauna visas zinutes su reikalingais parametrais.
	 * 
	 * @param folderName direktorija
	 * 
	 * @return ArrayList su visomis zinutemis bei ju parametrais
	 */
	public String[][] getMessagesWithAllParameters(String folderName) {
		try {
			String[][] msgArray = null;

			if (imapf.folderExists("/", store, folderName)[0].equals("")) {

				Folder folder = imapf.getFolderFromString(store, folderName);

				Message[] messages = imapm.getAllMesaggesFromFolder(folder);

				int kiek = messages.length;

				msgArray = new String[kiek][6];// parametru skaicius 5

				for (int i = 0; i < kiek; i++) {
					int msgNumb = imapm.getMessageNb(messages[i]);
					String[] parameters = getMessageParameters(msgNumb, folder);
					int keli = parameters.length;

					for (int s = 0; s < keli; s++) {

						msgArray[i][s] = parameters[s];

					}

				}
				return msgArray;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Suskaiciuja kiek zinuciu direktorijoje.
	 * 
	 * @param folderName direktorijos pavadinimas
	 * 
	 * @return skaicius kiek zinuciu egzistuoja direktorijoje
	 */
	public int countMessagesInFolder(String folderName) {

		if (folderName != null) {

			if (!imapf.folderExists(folderName, store, null)[0].equals("")) {

				Folder folder = imapf.getFolderFromString(store, folderName);

				int kiek = imapm.countAllMessagesInFolder(folder);

				logger.info("Counted that it is " + kiek
						+ " messages in directory " + folder.getName());
				logger.warn("Suskaiciuota jog rasta " + kiek
						+ " zinuciu direktorijoje " + folder.getName());
				GUI.spausdinti();
				return kiek;
			}
		}
		return 0;
	}

	/**
	 * Nuskaito zinutes parametrus.
	 * 
	 * @param msgNumber el.pasto numeris
	 * @param folder katalogas
	 * 
	 * @return el.laisko parametrus
	 */
	public String[] getMessageParameters(int msgNumber, Folder folder) {

		String[] messageParameters = new String[6];
		if ((msgNumber >= 0) && (folder instanceof Folder)) {

			Message message = imapm.getMessageByNumber(msgNumber, folder);

			messageParameters[0] = "" + msgNumber;
			messageParameters[1] = folder.toString();
			messageParameters[2] = imapm.getMessageSender(message);
			messageParameters[3] = imapm.getMessageSubject(message);

			String[] recipients = imapm.getMessageRecipients(message, null);

			int kiek = recipients.length;

			String recipientsInOneString = null;

			for (int i = 0; i < kiek; i++) {

				if (i == 0) {

					recipientsInOneString = recipients[i];

				} else {

					recipientsInOneString += recipients[i];

				}
			}

			messageParameters[4] = recipientsInOneString;
			messageParameters[5] = imapm.getMessageRecievedWeekDay(message);

			return messageParameters;

		}

		return null;

	}

	/**
	 * Atlikti veiksmus su laisku.
	 * 
	 * @param msgNumber zinutes numeris
	 * @param folderName direktorijos pavadinimas
	 * @param action veiksmas kury turime atlikti
	 * @param path kelias iki direktorijos y kuria kopijuosime zinute
	 * 
	 * @return true, jei pavyko
	 */
	public boolean performActionWithMessage(String msgNumber,
			String folderName, String action, String path) {

		if ((msgNumber != null) && (folderName != null)) {
			int messageNumber = Integer.parseInt(msgNumber);

			Folder folder = imapf.getFolderFromString(store, folderName);

			Message message = imapm.getMessageByNumber(messageNumber, folder);

			if (Constants.DELETE_MESSAGE.equals(action)) {

				deleteMessage(folder, message);
				return true;

			} else if ((action.equals(Constants.MOVE_MESSAGE_TO_FOLDER))
					&& (path != null)) {

				moveMessageToFolder(folder, message, path);
				return true;

			} else if (action.equals(Constants.MARK_MESSAGE_AS_SEEN)) {

				imapm.setFlagForMessage(message, Flags.Flag.SEEN);
				return true;
			} else if (action.equals(Constants.NOTIFY_USER)) {

				logger.fatal("Notify! Message " + msgNumber + " got in folder "
						+ folderName + " user difined action was"
						+ Constants.NOTIFY_USER);
				GUI.spausdintiEvent();
				markAsSeen(msgNumber, folderName);
				return true;
			} else if (action.equals(Constants.FLAG_MESSAGE)) {

				imapm.setFlagForMessage(message, Flags.Flag.FLAGGED);
				return true;
			} else if (action.equals(Constants.MARK_AS_DRAFT)) {

				imapm.setFlagForMessage(message, Flags.Flag.DRAFT);
				return true;
			} else if (action.equals(Constants.MARK_AS_ANSWERED)) {

				imapm.setFlagForMessage(message, Flags.Flag.ANSWERED);
				return true;
			} else {

				return false;
			}

		}

		return false;
	}

	/**
	 * Istrinti zinute.
	 * 
	 * @param folder direktorija kurioje zinute patalpinta
	 * @param message zinute kuria reikia istrinti
	 */
	private void deleteMessage(Folder folder, Message message) {
		imapm.setFlagForMessage(message, Flags.Flag.DELETED);
		logger.info("Message with number " + imapm.getMessageNb(message)
				+ " deleted from directory " + folder.getName());
		logger.warn("Zinute kurios numeris " + imapm.getMessageNb(message)
				+ " istrinta direktorijoje " + folder.getName());
		GUI.spausdinti();

	}

	/**
	 * Perkelti zinuute y direktorija.
	 * 
	 * @param folder direktorija is kurios perkelsim
	 * @param message zinute kuria reikia perkelti
	 * @param path kelias direktorijos kuria y kuria reikia perkelti
	 */
	private void moveMessageToFolder(Folder folder, Message message, String path) {

		if (imapf.folderExists(path, store, null)[0].equals("")) {

			String created = imapf.createFolder(path, store);

			if (!created.equals(path)) {

				path = created;

			}
		}

		if (path.equals("[Google Mail]")) {

			String created = imapf.createFolder("[Google Mail]/NoName", store);

			if (!created.equals(path)) {

				path = created;

			}
		}

		Folder newFolder = imapf.getFolderFromString(store, path);

		imapm.copyMessageToFolder(folder, newFolder, message, false);
		logger.info("Message with number " + imapm.getMessageNb(message)
				+ " moved from folder " + folder.getName() + " to " + path);

		logger.warn("Zinute kurios numeris " + imapm.getMessageNb(message)
				+ " perkelta is direktorijos " + folder.getName() + " i "
				+ path);
		GUI.spausdinti();
		imapf.closeFolderNoExpunge(folder);
		imapf.closeFolder(newFolder);

	}

	/**
	 * Pereiti i nauju zinuciu laukimo stadija.
	 * 
	 * @param folderName direktorijos pavadinimas kuri bus stebima
	 * @param rate kokiu dazniu bus tikrinamas serveris ( jei nepalaikoma idile)
	 * 
	 * @return zinutes su parametrais
	 */
	public String[][] waitForNewMail(String folderName, int rate) {

		String[][] msgArray = null;

		Folder folder = imapf.getFolderFromString(store, folderName);

		int keliosYra = imapm.countAllMessagesInFolder(folder);

		Message[] message = imapm
				.monitorForNewMessages(folder, rate, keliosYra);

		int kelios = message.length;

		msgArray = new String[kelios][6];// parametru skaicius 5

		for (int i = 0; i < kelios; i++) {

			int msgNumb = imapm.getMessageNb(message[i]);

			String[] parameters = getMessageParameters(msgNumb, folder);

			int keli = parameters.length;

			for (int s = 0; s < keli; s++) {

				msgArray[i][s] = parameters[s];

			}

		}
		return msgArray;
	}

	/**
	 * Gauti visas direktorijas su parametrais.
	 * 
	 * @return visas direktorijas su parametrais
	 */
	public String[][] getAllFoldersWithParameters() {

		String[] allFolders = getAllFolders();

		int kiek = allFolders.length;
		/* 
		 * Reikes dvieju parametru direktorijos bei zinuciu joje skaiciaus
		 */
		String[][] foldersWithParameters = new String[kiek][2];

		for (int i = 0; i < kiek; i++) {

			Folder current = imapf.getFolderFromString(store, allFolders[i]);
			foldersWithParameters[i][0] = allFolders[i];
			foldersWithParameters[i][1] = ""
					+ imapm.countAllMessagesInFolder(current);
		}
		return foldersWithParameters;
	}

	/**
	 * Nuskaityti visas direktorijas.
	 * 
	 * @return visas direktorijas
	 */
	public String[] getAllFolders() {
		String[] foldersWithPath = imapf.folderExists(null, store, null);
		return foldersWithPath;
	}

	/**
	 * Atlikti veiksmus su direktorijomis.
	 * 
	 * @param actions veiksmai
	 * 
	 * @return true, jei pavyko
	 */
	public boolean performActionsWithFolders(String[] actions) {
		String path = null;
		Folder folder = imapf.getFolderFromString(store, actions[0]);
		
		if (actions[3] != null) {
			
			path = actions[3];
			
			if (imapf.folderExists(path, store, null)[0].equals("")) {

				String created = imapf.createFolder(path, store);

				if (!created.equals(path)) {

					path = created;

				}
			}

			if (path != null) {

				if (path.equals("[Google Mail]")) {

					String created = imapf.createFolder("[Google Mail]/NoName",
							store);

					if (!created.equals(path)) {

						path = created;

					}
				}
			}
		}
		if (Constants.COPY_MESSAGES_TO_FOLDER.equals(actions[2])) {

			if (path != null) {

				Folder newFolder = imapf.getFolderFromString(store, path);

				try {

					imapm.copyAllMessagesToFolder(store, folder, newFolder,
							true);
					return true;

				} catch (MessagingException e) {

					logger.error("Can't copy messages from folder "
							+ folder.getFullName() + " to folder "
							+ newFolder.getFullName() + " Error:"
							+ e.getMessage());

				}
			} else {
				logger.error("Folder where to copy messages not specified.");
			}
		} else if (Constants.MOVE_MESSAGES_TO_FOLDER.equals(actions[2])) {
			if (path != null) {
				Folder newFolder = imapf.getFolderFromString(store, path);
				try {
					imapm.copyAllMessagesToFolder(store, folder, newFolder,
							false);
					return true;
				} catch (MessagingException e) {
					logger.error("Can't move messages from folder "
							+ folder.getFullName() + " to folder "
							+ newFolder.getFullName() + e.getMessage());
				}

			} else {
				logger.error("Folder where to move messages not specified.");
			}
		} else if (actions[2].equals(Constants.DELETE_FOLDER_AND_COPY_CONTENTS)) {

			if (path != null) {

				imapf.deletFolder(store, actions[0], false, path);
				return true;

			} else {
				logger.error("Folder where to copy messages not specified.");
			}
		} else if (Constants.DELETE_FOLDER_WITH_CONTENT.equals(actions[2])) {

			imapf.deletFolder(store, actions[0], true, null);
			return true;

		} else if (Constants.NOTIFY_USER.equals(actions[2])) {

			logger.fatal("Notify! Folder " + actions[0]
					+ " user difined action was" + Constants.NOTIFY_USER);
			GUI.spausdintiEvent();
			return true;

		}

		return false;

	}

	/**
	 * Pazymeti kaip perskaityta el.laiska
	 * 
	 * @param msgNumber el.laisko numeris
	 * @param folderName katalogo pavadinimas
	 */
	public void markAsSeen(String msgNumber, String folderName) {

		if ((msgNumber != null) && (folderName != null)) {

			int messageNumber = Integer.parseInt(msgNumber);

			Folder folder = imapf.getFolderFromString(store, folderName);

			Message message = imapm.getMessageByNumber(messageNumber, folder);
			if (!imapm.isFlagSet(message, Flags.Flag.SEEN)) {
				imapm.setFlagForMessage(message, Flags.Flag.SEEN);
			}
		}

	}

	/**
	 * Isvalyti kataloga, skirtas pasalinti visas zinutes kurios pazymetos zyme DELETED.
	 * 
	 * @param folderName katalogo pavadinimas
	 */
	public void expungeFolder(String folderName) {

		Folder folder = imapf.getFolderFromString(store, folderName);

		imapf.openFolder(folder);

		imapf.closeFolder(folder);

	}

}
