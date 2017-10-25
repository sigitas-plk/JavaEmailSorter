/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */
package IMAPC.Rules;

/**
 * Klase FolderVariables.
 */
public class FolderVariables {

	/** Direktorijos pavadinimas. */
	private String folder;

	/** Zinuciu skaicius direktorijoje. */
	private int messagesInFolder;

	/**
	 * Gauna direktorijos pavadinima.
	 * 
	 * @return direktorijos pavadinima
	 */
	public String getFolder() {
		return this.folder;
	}

	/**
	 * Nustato direktorijos pavadinima.
	 * 
	 * @param folder direktorijos pavadinimas
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}


	/**
	 * Grazina zinuciu direktorijoje skaiciu.
	 * 
	 * @return zinuciu direktorijoje skaiciu
	 */
	public int getMessagesInFolder() {
		return this.messagesInFolder;
	}

	/**
	 * Nustato zinuciu direktorijoje skaiciu.
	 * 
	 * @param messagesInFolder zinuciu direktorijoje skaiciu
	 */
	public void setMessagesInFolder(int messagesInFolder) {
		this.messagesInFolder = messagesInFolder;
	}

}
