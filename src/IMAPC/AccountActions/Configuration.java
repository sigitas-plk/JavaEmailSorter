/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */

package IMAPC.AccountActions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import IMAPC.GUI;

/**
 * Klase Configuration.
 */
public class Configuration {
	
	/** Naudojamas logger. */
	Logger logger = Logger.getLogger(Configuration.class);

	/**
	 * Nuskaito login duomenis.
	 * 
	 * @param file failo pavadinimas bei kelias iki jo
	 * @param fieald kuris laukas nuskaitomas Server,UserName ir t.t.
	 * 
	 * @return pasirinkto lauko reiksme
	 */
	public String readLoginData(File file, String fieald) {

		String FiealdValue = "false";

		try {

			SAXBuilder Builder = new SAXBuilder();
			
			Document doc = Builder.build(file);
			
			/*
			 * Paima visus root Elemento elementus
			 */
			Element LoginData = doc.getRootElement();
			List listas = LoginData.getChildren();
			/*
			 * Suskaicioja keli elementai ir suka for cikla kol suranda reikiama
			 * elementa ir td pertraukia cikla paimdamas elemento reiksme,jei
			 * elementas nerastas elemento reiksme islieka numatytoji null
			 */

			int nb = listas.size();

			for (int i = 0; i < nb; i++) {

				Element elementas = (Element) listas.get(i);
				String ElementName = elementas.getName();
				/*
				 * Suradus ieskoma elementa paima elemento reiksme ir stabdo
				 * cikla
				 */
				if (ElementName == fieald) {

					FiealdValue = elementas.getValue();

					break;
				}else {
					FiealdValue = "false";
				}
			}
		} catch (JDOMException e) {
		logger.error(e.getMessage());
		}catch(FileNotFoundException e) {
			logger.error("File not found:"+file.toString()+"\n specify correct path");
			logger.info("File not found:"+file.toString()+"\n specify correct path. System will exit");
			logger.warn("Failas nerastas"+file.toString()+"\n nurodykite teisinga kelia. Stabdomas programos veikimas.");
			GUI.spausdinti();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				//nekreipiam demesio
			}
			System.exit(1);
		} catch (IOException e) {
		logger.error(e.getMessage());
		}

		/*
		 * Pasalina is prisijungimo duomenu tarpus
		 */
		if (FiealdValue != null) {

			FiealdValue = removeInvalidChars(FiealdValue, false);

		}
		/*
		 * Jei reiksme bus palikta tuscia
		 */
		if (FiealdValue == "") {

			FiealdValue = "false";

		}

		return FiealdValue;
	}

	/**
	 * Pasalinami neleistini simboliai is string reiksmes.
	 * 
	 * @param string reiksme kuri turi buti patikrinta del neleistinu simboliu
	 * @param dir ar bus tikrinamas direktorijos pavadinimas
	 * 
	 * @return tekstas pasalinus neleistinus kintamuosius
	 */
	public String removeInvalidChars(String string, boolean dir) {

		String beNegalimuChar;
		String slash = "";
		String space = "";
		String lParam = "";
		String rParam = "";
		/*
		 * jeigu tikrinama ne direktorijos pavadinimas neleidziamas ir simbolis
		 * "/"
		 */
		if (!dir) {

			slash = "\\/";
			space = " ";
			lParam = "\\[";
			rParam = "\\]";		
		}
		
		/*
		 * Neleistini simboliai
		 */
		String[] negalimiChar = { "\\@", "\\^", "\\*", "\\!", "\\?",
				"\\&", "\\%", "\\#", "\\{", "\\}", "\\$", "\\=",
				"\\~", slash,space,lParam,rParam };

		int simboliai = negalimiChar.length;
		/*
		 * Imamas s elementas is masyvo ir tikrinama ar jo nera stringe
		 */
		for (int s = 0; s < simboliai; s++) {

			beNegalimuChar = "";

			StringTokenizer newString = new StringTokenizer(string,
					negalimiChar[s], false);

			while(newString.hasMoreElements()) {

				beNegalimuChar += newString.nextElement();

			}

			string = beNegalimuChar;

		}

		return string;

	}

}
