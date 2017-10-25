/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */
package IMAPC.Rules;

/**
 * Klase MessageVariables.
 */
public class MessageVariables {
	
	/** Laisko siuntejas. */
	private String sender;
	
	/** Laisko tema. */
	private String subject;
	
	/** Laisko gavimo savaites diena. */
	private String day;

	/**
	 * Gauna laisko tema.
	 * 
	 * @return subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * Nustato laisko tema.
	 * 
	 * @param subject laisko tema
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gauna savaites diena.
	 * 
	 * @return savaites diena
	 */
	public String getDay() {
		return this.day;
	}

	/**
	 * Nustato savaites diena.
	 * 
	 * @param day savaites diena
	 */
	public void setDay(String day) {
		this.day = day;
	}

	/**
	 * Gauna laisko siunteja.
	 * 
	 * @return sender siunteja
	 */
	public String getSender() {
		return this.sender;
	}

	/**
	 * Nustato laisko siunteja.
	 * 
	 * @param sender laisko siuntejas
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

}
