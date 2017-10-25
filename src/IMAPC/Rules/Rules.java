/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */
package IMAPC.Rules;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.base.CopyIdentifiersGlobalExporter;

/**
 * Klase Rules.
 */
public class Rules {
	
	/** Naudojamos Sesijos kintamasis. */
	private StatelessSession session;

	/**
	 * Inicijuoja taisykles.
	 * 
	 * @param file taisykliu failas
	 */
	public Rules(String file) {
		ReadRules readRules = new ReadRules(file);
		this.session = readRules.genarateRules();

	}
	
	/**
	 * tikrina taisykles direktorijoms.
	 * 
	 * @param folder direktorija bei jos parametrai
	 * 
	 * @return veiksmus
	 */
	public String[] startRulesCheckFolder(String[] folder) {
		FolderVariables folderVariables = new FolderVariables();
		folderVariables.setFolder(folder[0]);
		folderVariables.setMessagesInFolder(Integer.parseInt(folder[1]));
		session.setGlobalExporter(new CopyIdentifiersGlobalExporter());
		StatelessSessionResult result = session
				.executeWithResults(folderVariables);
		if ((String) result.getGlobal("folderAction") != null) {
		
			String[] rules = new String[4];

			rules[0] = folder[0];
			rules[1] = folder[1];
			rules[2] = (String) result.getGlobal("folderAction");
			rules[3] = (String) result.getGlobal("folderPath");
			/*
			 * Atstatome globals kitos zinutes tikrinimui
			 */
			session.setGlobal("folderAction", null);
			session.setGlobal("folderPath", null);
			return rules;

		} else {

			return null;

		}

	}
	
	/**
	 * Tikrina taisykles konkreciam laiskui.
	 * 
	 * @param messageParameters laisko parametrai
	 * 
	 * @return string[] su nurodytais veiksmais
	 */
	public String[] checkRule(String[] messageParameters) {

		MessageVariables messageVariables = new MessageVariables();

		messageVariables.setSender(messageParameters[2]);
		messageVariables.setSubject(messageParameters[3]);
		messageVariables.setDay(messageParameters[5]);

		session.setGlobalExporter(new CopyIdentifiersGlobalExporter());
		StatelessSessionResult result = session
				.executeWithResults(messageVariables);
		if ((String) result.getGlobal("action") != null) {
			
			String[] rules = new String[4];
			
			rules[0] = messageParameters[0];
			rules[1] = messageParameters[1];
			rules[2] = (String) result.getGlobal("action");
			rules[3] = (String) result.getGlobal("path");
			/*
			 * Atstatome globals kitos zinutes tikrinimui
			 */
			session.setGlobal("action", null);
			session.setGlobal("path", null);
			return rules;

		} else {

			return null;

		}

	}

}
