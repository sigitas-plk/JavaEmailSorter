/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */
package IMAPC.Rules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;

import IMAPC.GUI;

/**
 * Klase ReadRules.
 */
public class ReadRules {

	/**
	 * Sukuriame logger kuriuo irasinesime visas ivykusias klaidas ir kitus
	 * ivykius.
	 */
	Logger logger = Logger.getLogger(Rules.class);

	/** Taisykliu failas. */
	public String rulesFile;

	/**
	 * Generuoja taisykles.
	 * 
	 * @return the stateless session
	 */
	public StatelessSession genarateRules() {
		StatelessSession session = null;
		if (rulesFile != null) {
			/*
			 * Kompiliuojame decision table i atskiras taisykles
			 */
			InputStream stream = this.getClass().getResourceAsStream(rulesFile);
			if (stream != null) {
				SpreadsheetCompiler compiler = new SpreadsheetCompiler();
				String drl = compiler.compile(this.getClass()
						.getResourceAsStream(rulesFile), InputType.XLS);
				System.out.println(drl);// Sukompiliuotos taisykles

				RuleBase ruleBase;
				try {

					ruleBase = buildRuleBase(drl);
					session = ruleBase.newStatelessSession();
					logger.warn("Sugeneruotos taisykles is taisykliu lenteles");
					logger.info("Rules generated from rules table");
					GUI.spausdinti();
					return session;

				} catch (DroolsParserException e) {

					logger.error(e.getMessage());

				} catch (IOException e) {

					logger.error(e.getMessage());

				} catch (Exception e) {

					logger.error(e.getMessage());

				}
			} else {
				logger.error("Rules file " + " not found.");
				logger.warn("Taisykliu failas " + rulesFile
						+ " nerastas. Stabdomas programos veikimas.");
				logger.info("Rules file " + rulesFile
						+ " not found. System will exit");
				GUI.spausdinti();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// nekreipiam demesio
				}
				System.exit(1);
			}
		} else {

			logger.error("No Rules file entered!");

		}

		return session;
	}

	/**
	 * Sudaro RuleBase.
	 * 
	 * @param drl
	 *            taisykle
	 * 
	 * @return ruleBase
	 * 
	 * @throws DroolsParserException
	 * @throws IOException
	 * @throws Exception
	 */
	private RuleBase buildRuleBase(String drl) throws DroolsParserException,
			IOException, Exception {
		/*
		 * Kuriame rule package ir rule base lyg tai butu iprastos taisykles
		 */
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(new StringReader(drl));

		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(builder.getPackage());
		return ruleBase;
	}

	/**
	 * ReadRules konstruktorius.
	 * 
	 * @param rulesFile
	 *            taisykliu failas
	 */
	public ReadRules(String rulesFile) {

		this.rulesFile = rulesFile;
	}

}