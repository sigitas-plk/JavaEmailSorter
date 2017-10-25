/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */
package IMAPC;

import java.io.File;
import javax.mail.Store;
import org.apache.log4j.Logger;
import IMAPC.Rules.Rules;
import IMAPC.AccountActions.ImapActions;

/**
 * Klase IMAPC.
 */
public class IMAPC {

	/** Gui objektas. */
	public GUI gui;

	/** Priemimo dezute. */
	private String folderName = "INBOX";

	/** kas kiek milisekundziu tikrinti el pasta. */
	private int tikrinimoRate = 6000;

	/** Taisykiu failas. */
	public String rulesFailas = "/Rules/IMAPCRules.xls";

	/** Prisijungimo duomenu failas. */
	public File loginFailas;

	/** pasto dezutes turinio saugojimui skirtas kintamasis. */
	private Store store;

	/** Taisykliu objektas. */
	public Rules rules;

	/** ImapActions objektas */
	public ImapActions imapa;

	/** Naudojamas logger. */
	static Logger logger = Logger.getLogger(IMAPC.class);

	/**
	 * Startuojamas elektroninio pasto tikrinimas.
	 */
	public void start() {
		logger.warn("Inicijuota tikrinimo pradzia.");
		logger.info("Starting check.");
		GUI.spausdinti();

		gui.startButton.setEnabled(false);
		gui.loginButton.setEnabled(false);
		gui.stopButton.setEnabled(true);
			
		this.rules = new Rules(rulesFailas);
		this.imapa = new ImapActions(loginFailas);
		folderRulesCheck();
		startChecking();

	}

	/**
	 * Direktoriju taisykliu tikrinimas.
	 */
	private void folderRulesCheck() {
		logger.warn("Inicijuotas katalogu taisykliu tikrinimas.");
		logger.info("Starting check folders.");
		GUI.spausdinti();
		String[][] folderProperties = imapa.getAllFoldersWithParameters();
		int kiek = folderProperties.length;

		for (int i = 0; i < kiek; i++) {
			String[] actions = rules.startRulesCheckFolder(folderProperties[i]);
			if (actions != null) {
				imapa.performActionsWithFolders(actions);
			}
		}
		logger.warn("Katalogu taisykliu tikrinimas baigtas.");
		logger.info("Check of folder rules finished.");
		GUI.spausdinti();
	}

	/**
	 * Pradeda tikrinti el.pasta
	 */
	private void startChecking() {
		logger.warn("Tikrinamos taisykles el. laiskams.");
		logger.info("Checking rules for e-mail.");
		GUI.spausdinti();
		String[][] messageParameters;
		/*
		 * Patikrina visas jau esancias zinutes direktorijoje
		 */

		messageParameters = getAllMessagesWithParametersFromFolder(folderName,
				imapa);

		performActions(rules, imapa, messageParameters);

		imapa.expungeFolder(folderName);
		logger.warn("Taisykliu tikrinimas laiskams baigtas.");
		logger.info("Checking rules for e-mail finished.");
		GUI.spausdinti();

		/*
		 * Laukia nauju laisku ir patikrina taisykles jiems tesiama kol
		 * sustabdoma programos veikimas
		 */
		while (true) {
			long currentTime = System.currentTimeMillis();
			long nextTime = System.currentTimeMillis();
			while (currentTime == nextTime) {
				nextTime = System.currentTimeMillis();
			}

			logger.info("Whaiting for new e-mail.");
			logger.warn("Laukiama nauju laisku.");
			GUI.spausdinti();

			messageParameters = waitForNewMail(imapa, folderName, tikrinimoRate);

			performActions(rules, imapa, messageParameters);

			imapa.expungeFolder(folderName);

		}

	}

	/**
	 * Gauna visus laiskus su parametrais is duotos direktorijos.
	 * 
	 * @param folder direktorijos pavadinimas
	 * @param imapa ImapActions objektas
	 * 
	 * @return String masyva su visu zinuciu reikalingais parametrais
	 */
	private String[][] getAllMessagesWithParametersFromFolder(String folder,
			ImapActions imapa) {

		String[][] msgsWithParameters = imapa
				.getMessagesWithAllParameters(folder);
		return msgsWithParameters;

	}

	/**
	 * Atlikti veiksmus su el laiskais.
	 * 
	 * @param rules rules objektas
	 * @param imapa ImapActions obejktas
	 * @param messagesWithParameters zinuciu masyva su ju parametrais
	 */
	private void performActions(Rules rules, ImapActions imapa,
			String[][] messagesWithParameters) {

		int kiek = messagesWithParameters.length;

		for (int i = 0; i < kiek; i++) {

			String[] actions = rules.checkRule(messagesWithParameters[i]);

			if (actions != null) {

				imapa.performActionWithMessage(actions[0], actions[1],
						actions[2], actions[3]);
			} else {
				imapa.markAsSeen(messagesWithParameters[i][0],
						messagesWithParameters[i][1]);
			}
		}

	}

	/**
	 * Laukti nauju el laisku.
	 * 
	 * @param imapa ImapActions objektas
	 * @param folder direktorijos pavadinimas
	 * @param rate uzklausu daznis jei nepalaikoma idile
	 * 
	 * @return string[][] su naujomis zinutemis
	 */
	private String[][] waitForNewMail(ImapActions imapa, String folder, int rate) {

		return imapa.waitForNewMail(folder, rate);

	}

}
