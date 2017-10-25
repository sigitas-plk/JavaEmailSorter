/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */

package IMAPC.AccountActions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

import IMAPC.AccountActions.Constants;
import IMAPC.GUI;

/**
 * Klase ImapMessage. Sioje klaseje atliekami visi pagrindiniai veiksmai su
 * elektroniniais laiskais
 */
public class ImapMessage {

	/** Sukuriame logger kuriuo irasinesime visas ivykusias klaidas ir kitus ivykius. */
	Logger logger = Logger.getLogger(ImapMessage.class);

	/** The ImapFolder deklaracija. */
	ImapFolder imapf = new ImapFolder();



	/**
	 * Garazina laiska pasirinktu numeriu is direktorijos.
	 * 
	 * @param number laiko ID
	 * @param folder direktorija
	 * 
	 * @return message el.laikas pagal pasirinkta ID
	 */
	public Message getMessageByNumber(int number, Folder folder) {
		Message message = null;
		if ((folder instanceof Folder) && (number >= 0)) {

			try {
				imapf.openFolder(folder);
				int kiek = folder.getMessageCount();
				if (number <= kiek) {
					message = folder.getMessage(number);
				imapf.closeFolderNoExpunge(folder);
					return message;

				} else {
					imapf.closeFolderNoExpunge(folder);
					logger.error("Message with number " + number
							+ " in folder " + folder.getFullName()
							+ " doesn't exist.");
					return null;
				}
			} catch (MessagingException e) {
				logger.error(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}
		return message;
	}

	/**
	 * Tikrina pasto dezute laukiant nauju laisku.
	 * 
	 * @param folder direktorija kuri bus tikrinama
	 * @param rate kokiu dazniu bus tikrinama sekundemis
	 * @param kelios kelios zinutes egzistuoja direktorijoje pries tikrinant
	 * 
	 * @return message[] masyvas su naujais el.laiskais.
	 */
	public Message[] monitorForNewMessages(Folder folder, int rate, int kelios) {
	
		 Message[] messages = null;
		 
		if (folder instanceof Folder) {

			imapf.openFolder(folder);

			while (true) {

				try {

					Thread.sleep(rate);

				} catch (InterruptedException e1) {
					/*
					 * ignoruojame ir tesiame toliau
					 */
				}
				int kiekDabar = countAllMessagesInFolder(folder);
				if(kiekDabar>kelios) {
					
				 messages = getUnseenMessages(folder);
				
				int kiek = messages.length;

				logger.info("Got " + kiek + " new messages.");
				logger.warn("Gautos " + kiek + " naujos zinutes.");
				GUI.spausdinti();
				imapf.closeFolderNoExpunge(folder);
				return messages;
				}

			}

		}
		return messages;
	}


	/**
	 * Grazina laisko gavimo savaites diena.
	 * 
	 * @param message laiskas
	 * 
	 * @return savaites diena kada gauta zinute
	 */
	public String getMessageRecievedWeekDay(Message message) {
		if (message instanceof Message) {
			try {
				
				imapf.openFolder(message.getFolder());				
				Date recievedDate = message.getReceivedDate();
				String weekDay = getWeekDay(recievedDate);
				imapf.closeFolderNoExpunge(message.getFolder());
				return weekDay;
			} catch (MessagingException e) {
				logger.error(e.getMessage());
			}
		}
		return "";
	}

	/**
	 * Is duotos datos grazina savaites diena.
	 * 
	 * @param date data
	 * 
	 * @return savaites diena
	 */
	public String getWeekDay(Date date) {

		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		String dayOfWeek;

		switch (day) {
		case 1:
			dayOfWeek = Constants.SUNDAY;
			break;
		case 2:
			dayOfWeek = Constants.MONDAY;
			break;
		case 3:
			dayOfWeek = Constants.TUESDAY;
			break;
		case 4:
			dayOfWeek = Constants.WEDNESDAY;
			break;
		case 5:
			dayOfWeek = Constants.THURSDAY;
			break;
		case 6:
			dayOfWeek = Constants.FRIDAY;
			break;
		case 7:
			dayOfWeek = Constants.SATURDAY;
			break;
		default:
			dayOfWeek = "";
		}
		return dayOfWeek;
	}

	/**
	 * Grazina nematytas zinutes.
	 * 
	 * @param folder direktorija kurioje tikrinsime
	 * 
	 * @return message masyva su zinutemis nepazymetomis flagu SEEN
	 */
	public Message[] getUnseenMessages(Folder folder) {

		ArrayList<Message> unseenMessages = new ArrayList<Message>();
		
		Message [] messageArray = null;

		if (folder instanceof Folder) {

			imapf.openFolder(folder);

			try {

				Message[] messages = folder.getMessages();

				int kiek = messages.length;

				for (int i = 1; i <= kiek; i++) {

					if (!folder.getMessage(i).isSet(Flags.Flag.SEEN)) {

						unseenMessages.add(folder.getMessage(i));

					}
				}
				int kelios = unseenMessages.size();
				if(kelios!=0) {
				 messageArray = (Message[])unseenMessages.toArray(new Message[kelios]);
				}
				imapf.closeFolder(folder);
			} catch (MessagingException e) {				
				logger.error(e.getMessage());
			}catch(Exception e) {
				logger.error(e.getMessage());
			}
			
			

		}
		
		return messageArray;
	}

	/**
	 * Grazina visas naujas zinutes is pasirinktos direktorijos.
	 * 
	 * @param folder direktorija is kurios bus atrenkamos zinutes
	 * @param flag kokias zinutes norima gauti
	 * 
	 * @return Message masyva su pasirinkto tipo zinutemis
	 */
	public Message[] getMessagesWithFlag(Folder folder, Flag flag) {
		if ((folder instanceof Folder) && (flag instanceof Flags.Flag)) {
			/*
			 * Jei neisunciamas flag kokias zinutes tikrinti
			 */
			if (flag == null) {
				flag = Flags.Flag.RECENT;
			}

			/*
			 * Patikriname ar direktorija atidaryta, jei ne atidarome
			 */
			imapf.openFolder(folder);

			int kelios = countMessagesWithFlag(folder, flag);

			boolean msg = false;

			Message[] messages = new Message[kelios];

			int kelinta = 0;

			try {
				/*
				 * Patikriname kelios zinutes yra direktorijoje
				 */
				int kiek = folder.getMessageCount();

				for (int i = 1; i <= kiek; i++) {

					msg = folder.getMessage(i).isSet(flag);

					if (msg) {

						messages[kelinta] = folder.getMessage(i);

						kelinta++;
					}
				}
				imapf.closeFolder(folder);
				return messages;

			} catch (MessagingException e) {

				logger.error("Can't retrieve message from folder "
						+ folder.getFullName() + e.getMessage());
			}
		}
		return null;

	}

	/**
	 * Sukaiciuoja kiek laisku yra duotoje direktorijoje.
	 * 
	 * @param folder direktorija
	 * 
	 * @return laisku skaicius
	 */
	public int countAllMessagesInFolder(Folder folder) {

		if (folder instanceof Folder) {
			try {
				if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {

					int kelios = folder.getMessageCount();

					return kelios;

				} else {

					return 0;
				}
			} catch (MessagingException e) {
				logger.error(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}

		return 0;
	}

	/**
	 * Suskaiciuoti zinutes duotoje direktorijoje.
	 * 
	 * @param folder kuri direktorijos zinutes bus skaiciuojamos
	 * @param flag kokio tipo zinutes reikes suskaiciuoti
	 * 
	 * @return zinuciu skaiciu
	 */
	public int countMessagesWithFlag(Folder folder, Flag flag) {
		if ((folder instanceof Folder) && (flag instanceof Flags.Flag)) {
			if (flag == null) {

				flag = Flag.RECENT;
			}
			int kelios = 0;

			boolean msg = false;

			imapf.openFolder(folder);

			try {

				int kiek = folder.getMessageCount();

				for (int i = 1; i <= kiek; i++) {

					msg = folder.getMessage(i).isSet(flag);

					if (msg) {

						kelios++;

					}
				}
				imapf.closeFolderNoExpunge(folder);
				return kelios;

			} catch (MessagingException e) {
				logger.error("Can't count messages in folder " + folder
						+ e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return 0;

	}

	/**
	 * Kopijuoti visas zinutes is vienos direktorijos i kita.
	 * 
	 * @param folder direktorija is kurios kopijuoti
	 * @param newFolder direktorija i kuria kopijuoti
	 * @param leaveOriginal ar palikti zinutes
	 * @param store  store
	 * 
	 * @throws MessagingException jei negali perkopijuoti zinuciu/es
	 */
	public void copyAllMessagesToFolder(Store store, Folder folder,
			Folder newFolder, boolean leaveOriginal) throws MessagingException {
		if ((folder instanceof Folder) && (newFolder instanceof Folder)) {
			if (folder.exists()) {

				if (newFolder.exists()) {

					if (!folder.getFullName().equals(newFolder.getFullName())) {
						imapf.openFolder(folder);
						/*
						 * Nuskaito zinutes is folder direktorijos
						 */
						Message[] messages = getAllMesaggesFromFolder(folder);
						
						/*
						 * Kopijuojame zinutes i newFolder direktorija
						 */
						imapf.closeFolder(folder);
						try {
							folder.open(Folder.READ_ONLY);
							folder.copyMessages(messages,newFolder);
							/*
							 * Pazymi zinutes direktorijoje istintas panaudojus
							 * funkcija explunge jos bus visiskai pasalintos is
							 * sitemos
							 */
							if (!leaveOriginal) {

								for (int i = 0; i < messages.length; i++) {

									setFlagForMessage(messages[i], Flag.DELETED);

								}

							}
							imapf.closeFolderNoExpunge(newFolder);
						} catch (MessagingException e) {
							e.printStackTrace();
							logger.error("Can't copy messages from folder "
									+ folder.getFullName() + " to"
									+ newFolder.getFullName() + e.getMessage());
						}
						imapf.closeFolder(folder);
					} else {
						logger.error("Can't copy to same folder "
								+ newFolder.getFullName()
								+ " where mesages are stored. ");
					}

				} else {
					logger.error("Folder " + newFolder.getFullName()
							+ " doesn't exist.");
					imapf.createFolder(newFolder.getFullName(), store);
					copyAllMessagesToFolder(store, folder, newFolder,
							leaveOriginal);
				}
			} else {
				logger.error("Folder " + folder.getFullName()
						+ " doesn't exist.");
			}
		}
	}

	/**
	 * Kopijuoja viena laiska y kita direktorija.
	 * 
	 * @param oldFolder drektorija is kurios kopjuojame
	 * @param newFolder direktorija y kuria kopijuojame
	 * @param message laiksas kuris turi buti kopijuojamas
	 * @param LeaveOriginal ar palikti orginala
	 */
	public void copyMessageToFolder(Folder oldFolder, Folder newFolder,
			Message message, boolean LeaveOriginal) {
		if ((oldFolder instanceof Folder) && (message instanceof Message)
				&& (newFolder instanceof Folder)) {

			Message[] oneMessage = new Message[1];
			oneMessage[0] = message;
			
			try {
				imapf.openFolder(oldFolder);
				
				oldFolder.copyMessages(oneMessage, newFolder);
				/*
				 * Pazymi zinute DELETED panaudojus funkcija explunge ji bus
				 * visiskai pasalinta is sitemos
				 */
				if (!LeaveOriginal) {
					setFlagForMessage(message, Flag.DELETED);
				}
				imapf.closeFolderNoExpunge(oldFolder);
			} catch (MessagingException e) {
				logger.error("Can't copy message: "
						+ message.getMessageNumber() + e.getMessage());

			} catch (Exception e) {

				logger.error(e.getMessage());
			}

		}

	}

	/**
	 * Grazina visas zinutes is direktorijos.
	 * 
	 * @param dir direktorija is kurios imsime zinutes
	 * 
	 * @return Message masyva su visomis zinutemis is direktorijos
	 */
	public Message[] getAllMesaggesFromFolder(Folder dir) {

		Message[] messages = null;
		if (dir instanceof Folder) {
			try {
				imapf.openFolder(dir);

				messages = dir.getMessages();
				imapf.closeFolderNoExpunge(dir);

			} catch (MessagingException e) {
				logger.error("Can't get mesages from folder "
						+ dir.getFullName() + e.getMessage());

			}
		}
		return messages;
	}

	/**
	 * Gauna zinutes numeri.
	 * 
	 * @param message zinute kurios numery norime gauti
	 * 
	 * @return zinutes numeri
	 */
	public int getMessageNb(Message message) {
		if (message instanceof Message) {
			int nb = 0;

			nb = message.getMessageNumber();
			return nb;
		}
		return -1;
	}

	/**
	 * Nustato flag pasirinktai zinutei.
	 * 
	 * @param message zinute kuriai bus nustatytas flag
	 * @param flag koks flag turi buti nustatytas
	 */
	public void setFlagForMessage(Message message, Flag flag) {
		if (message instanceof Message) {
			try {
				imapf.openFolder(message.getFolder());
				
				Flags flags = message.getFlags();

				if (!flags.contains(flag)) {

					message.setFlag(flag, true);
				}
				imapf.closeFolder(message.getFolder());
			} catch (MessagingException e) {
				logger.error("Can't set as" + flag.toString() + " message "
						+ message.getMessageNumber() + e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	/**
	 * Patikrina ar tokia zyme nustatyta el.laiskui.
	 * 
	 * @param message el.laiskas.
	 * @param flag zyme kuria tikrinsime.
	 * 
	 * @return true, jei zyme nustatyta.
	 */
	public boolean isFlagSet(Message message,Flag flag) {
		if(message instanceof Message) {
			try {
				
				return message.isSet(flag);
				
			} catch (MessagingException e) {
			logger.error(e.getMessage());
		}
		}
		return false;
	}
	
	/**
	 * Nuskaitome laiko gavejus.
	 * 
	 * @param message laiskas kuris bus tikrinamas
	 * @param type persiuntimo tipas
	 * 
	 * @return laisko gavejus
	 */
	public String[] getMessageRecipients(Message message, String type) {
		String [] nera = {""};
		if (message instanceof Message) {

			boolean all = false;

			Message.RecipientType recType = Message.RecipientType.TO;

			if (type != null) {
				if (type.equals("CC")) {
					recType = Message.RecipientType.CC;
				} else if (type.equals("BCC")) {
					recType = Message.RecipientType.BCC;
				} else {
					recType = Message.RecipientType.TO;
				}

			} else {
				/*
				 * Jei nenurodyta kokiu gaveju
				 */
				all = true;
			}

			try {
				/*
				 * Jei reikia tik tam tikro tipo gaveju
				 */
				Address[] reciepients;
				imapf.openFolder(message.getFolder());
				if (!all) {
					reciepients = message.getRecipients(recType);
					/*
					 * Jei reikia visu gaveju
					 */
				} else {

					reciepients = message.getAllRecipients();
				}
				imapf.closeFolder(message.getFolder());
				int kiek = reciepients.length;

				if (kiek != 0) {

					String[] rec = new String[kiek];

					for (int i = 0; i < kiek; i++) {

						/*
						 * konvertuojame i string tipa
						 */
						rec[i] = reciepients[i].toString();
					}
					return rec;
				}
			} catch (MessagingException e) {
				logger.error(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}

		return nera;
	}

	/**
	 * Grazina laisko tema.
	 * 
	 * @param message laiskas kuris bus tikrinamas
	 * 
	 * @return laisko tema jei egzistuoja, jei ne grazinama ""
	 */
	public String getMessageSubject(Message message) {

		if (message instanceof Message) {

			try {
			
				imapf.openFolder(message.getFolder());
				String subject = message.getSubject();

				if (subject != null) {

					return subject;

				}
				imapf.closeFolderNoExpunge(message.getFolder());
			} catch (MessagingException e) {
				logger.error("Can't retrieve Subject for message "
						+ message.getMessageNumber() + " in folder "
						+ message.getFolder().toString() +" "+ e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return "";

	}

	/**
	 * Patikrina ar laiskas is atitinkamo siuntejo.
	 * 
	 * @param message zinute kuri bus tikrinama
	 * 
	 * @return grazina laisko siunteja
	 */
	public String getMessageSender(Message message) {
		if (message instanceof Message) {
	
			try {
				imapf.openFolder(message.getFolder());
				Address[] address = message.getFrom();

				if (address.length == 1) {

					InternetAddress from = (InternetAddress) address[0];

					String senderAddress = from.getAddress();
					imapf.closeFolderNoExpunge(message.getFolder());
					return senderAddress;

				}
				imapf.closeFolderNoExpunge(message.getFolder());
			} catch (MessagingException e) {
				logger.error("Can't retrieve from address for message "
						+ message.getMessageNumber() + " in folder "
						+ message.getFolder().toString() + e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return "";
	}

	/*---------------------------------- F I L T R U I ------------------------------------------*/
	/**
	 * Patikrina ar gauta is siuntejo duotu adresu.
	 * 
	 * @param message zinutes kurio bus tikrinamos
	 * @param sender siuntejas
	 * 
	 * @return true, jei zinutes siuntejas yra duotasis pasto adresas
	 */
	public ArrayList<Integer> getAllMessagesFromSender(Message[] message,
			String sender) {

		ArrayList<Integer> msgs = new ArrayList<Integer>();
		if (message instanceof Message[]) {
			int kiek = message.length;
			int numb;

			for (int i = 0; i < kiek; i++) {
				try {
					/*
					 * nuskaitome zinutes laukeli from
					 */
					Address[] address = message[i].getFrom();

					if (address.length == 1) {

						InternetAddress from = (InternetAddress) address[0];

						String senderAddress = from.getAddress();
						/*
						 * tikriname ar siuntejas yra duotasis sender
						 */
						if (senderAddress.equals(sender)) {

							numb = message[i].getMessageNumber();
							msgs.add(numb);
						}
					}

				} catch (MessagingException e) {
					logger.error("Can't retrieve from address for message "
							+ message[i].getMessageNumber() + e.getMessage());
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
		return msgs;
	}

}
