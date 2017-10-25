/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu universitetas 2008
 */
package IMAPC.AccountActions;

import java.util.ArrayList;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.URLName;

import org.apache.log4j.Logger;

import IMAPC.GUI;

/**
 * The Class ImapFolder. Sios klases metodai skirti veiksmams su direktorijomis
 */
public class ImapFolder {

	/** Sukuriame logger kuriuo irasinesime visas ivykusias klaidas ir kitus ivykius. */
	Logger logger = Logger.getLogger(ImapFolder.class);

	/**
	 * Garzina direktorija is duotos String reiksmes.
	 * 
	 * @param store naudojama store
	 * @param dir direktorijos pavadinimas
	 * 
	 * @return folder direktorija pagal duota pavadinima
	 */
	public Folder getFolderFromString(Store store, String dir) {

		Folder folder = null;

		if (dir != null) {

			try {

				folder = store.getDefaultFolder();
				folder = folder.getFolder(dir);
				return folder;
			} catch (MessagingException e) {
				logger.error(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}
		return folder;
	}

	/**
	 * Istrinti direktorija.
	 * 
	 * @param store naudojamas store
	 * @param folderPath kuri direktorija turetu buti istrinta
	 * @param delContent ar istrint direktorijos turini
	 * @param newFolderPath kur turetu buti perkeltos zinutes
	 * 
	 * @return true, jei katalogas pasalintas
	 */
	public boolean deletFolder(Store store, String folderPath,
			boolean delContent, String newFolderPath) {
		ImapMessage imapm = new ImapMessage();
		Folder dir = null;
		Folder newDir = null;

		if (store.isConnected()) {

			URLName url = getFolderUrln(folderPath);

			try {
				dir = getFolderFromURL(url, store);

				if (dir.exists()) {

					int messagesInFolder = dir.getMessageCount();
					int subDirInFolder = dir.list().length;

					if ((messagesInFolder == 0) && (subDirInFolder == 0)) {

						closeFolder(dir);

						delFolder(dir);

						logger.info("Folder " + dir + " deleted.");
						logger.warn("Direktorija " + dir + " istrinta.");
						GUI.spausdinti();
						return true;
					} else if ((messagesInFolder != 0) && (subDirInFolder == 0)
							&& delContent) {

						deleteAllMessagesFromFolder(dir);
						delFolder(dir);

					} else if ((messagesInFolder != 0) && (subDirInFolder == 0)
							&& (!delContent) && (newFolderPath != null)) {

						URLName url2 = getFolderUrln(newFolderPath);
						newDir = getFolderFromURL(url2, store);

						if (newDir.exists()) {

							imapm.copyAllMessagesToFolder(store, dir, newDir,
									false);

							delFolder(dir);
							logger.info("Directory " + dir.getFullName()
									+ " deleted content copied to folder "
									+ newDir.getFullName());
							logger
									.info("Direktorija"
											+ dir.getFullName()
											+ " istrina, visos zinutes perkelto i direktorija "
											+ newDir.getFullName());
							GUI.spausdinti();
							return true;
						} else {
							logger.error("Directory " + newDir.getFullName()
									+ " deosn't exist.");
							createFolder(newFolderPath, store);
							deletFolder(store, folderPath, delContent,
									newFolderPath);
						}
					} else if ((messagesInFolder != 0) && (subDirInFolder != 0)
							&& (!delContent) && (newFolderPath != null)) {

						URLName url2 = getFolderUrln(newFolderPath);
						newDir = getFolderFromURL(url2, store);

						if (newDir.exists()) {

							imapm.copyAllMessagesToFolder(store, dir, newDir,
									false);
							logger.info("Messages moved to directory "
									+ newDir.getFullName());
							logger.warn("Zinutes nukopijuotos i direktorija"
									+ newDir.getFullName());
							logger
									.error("Can't delete folder, because subfolder exists,\ndelete folder with all content or copy subfolder content seperatly.");
							GUI.spausdinti();
							return true;
						} else {
							logger.error("Directory " + newDir.getFullName()
									+ " deosn't exist.");
							createFolder(newFolderPath, store);
							deletFolder(store, folderPath, delContent,
									newFolderPath);
						}
					} else if ((messagesInFolder == 0) && (subDirInFolder != 0)
							&& (!delContent)) {

						logger
								.error("Can't delete folder, because subfolder exists,\ndelete folder with all content or copy them subfolder content seperatly.");
						return false;
					} else if ((subDirInFolder != 0) && delContent) {

						if (deleteAllSubFolders(dir)) {

							logger.info("Folder " + dir.getName()
									+ " and all subfolders deleted.");
							logger
									.warn("Direktorija "
											+ dir.getName()
											+ " istrinta su visomis subdirektorijomis.");
							return true;
						} else {
							logger.error("Can't delete folder "
									+ dir.getFullName());
							return false;
						}

					}

				} else {
					logger.error("Folder " + folderPath + " does not exist.");
					return false;
				}
			} catch (MessagingException e) {

				logger.error("dd"+e.getMessage());
				return false;
			}
		} else {
			logger.error("Store not connected.");
			return false;
		}
		return false;
	}

	/**
	 * Paima kataloga is pateikto url.
	 * 
	 * @param urln URLName direktorijai
	 * @param store naudojama store
	 * 
	 * @return katalogas is pateikto url
	 */
	public Folder getFolderFromURL(URLName urln, Store store) {
		Folder folder = null;

		try {
			folder = store.getFolder(urln);
		} catch (MessagingException e) {
			logger.error("Can't get folder from given path." + e.getMessage());
		}

		return folder;
	}

	/**
	 * Jei atidaryta direktorija ja uzdaro ir istrina pazymetas trinti zinutes.
	 * 
	 * @param folder direktorija kuria tikrinsime
	 */
	public void closeFolder(Folder folder) {

		if (folder.isOpen()) {

			try {
				folder.close(true);

			} catch (MessagingException e) {
				logger.error("Can't close folder: " + folder.getFullName()
						+ e.getMessage());
			}
		}

	}

	/**
	 * Uzdaryti kataloga nepasalinant el.laisku pazymetu zyme DELETED.
	 * 
	 * @param folder katalogas kury reikia uzdaryti
	 */
	public void closeFolderNoExpunge(Folder folder) {

		if (folder.isOpen()) {

			try {
				folder.close(false);

			} catch (MessagingException e) {
				logger.error("Can't close folder: " + folder.getFullName()
						+ e.getMessage());
			}
		}
	}

	/**
	 * Istrina direktorija.
	 * 
	 * @param folder direktorija kuria norime istrinti
	 * 
	 * @return true, jei direktorija istrinta
	 */
	private boolean delFolder(Folder folder) {

		boolean istrinta = false;

		try {

			if (folder.isOpen()) {

				folder.close(true);

			}

			istrinta = folder.delete(true);

		} catch (MessagingException e) {

			logger.error("Can't delete folder: " + folder.getFullName()
					+ e.getMessage());
		}
		return istrinta;
	}

	/**
	 * Atidaro direktorija jei dar neatidaryta.
	 * 
	 * @param dir katalogas kuri norime atidaryti.
	 */
	public void openFolder(Folder dir) {
		try {
			if (!dir.isOpen()) {

				dir.open(Folder.READ_WRITE);

			}

		} catch (MessagingException e) {

			logger.error("Can't open folder." + dir.getFullName()
					+ e.getMessage());
		}
	}

	/**
	 * Istrina visas zinutes is direktorijos.
	 * 
	 * @param dir direktorija is kurios tures buti trinamos zinutes
	 * 
	 * @return true, jei zinutes sekmingai istrintos
	 */
	private boolean deleteAllMessagesFromFolder(Folder dir) {

		boolean istrinta = false;
		if (dir instanceof Folder) {

			try {
				openFolder(dir);
				/*
				 * Suskaiciuoja kelios zinutes yra direktorijoje
				 */
				int kelios = dir.getMessageCount();

				if (kelios != 0) {

					/*
					 * Visas zinutems uzdeda flag DELETED
					 */
					openFolder(dir);

					for (int i = 1; i <= kelios; i++) {

						dir.getMessage(i).setFlag(Flags.Flag.DELETED, true);
					}
					/*
					 * Visas zinutes pazymetas DELETED pasalina visam laikui
					 */
					closeFolder(dir);

					if (dir.getMessageCount() == 0) {

						logger.info("Messages from folder " + dir.getFullName()
								+ " deleted.");
						logger.warn("Zinutes direktorijoje "
								+ dir.getFullName() + " istrintos.");

						istrinta = true;
					}

				} else {
					logger.error("Folder" + dir.getFullName()
							+ " has no messages.");
				}

			} catch (MessagingException e) {

				logger.error("Can't delete mesages." + e.getMessage());
			}
		}

		return istrinta;
	}

	/**
	 * Istrina visas direktorijos subdirektorijas bei pacia direktorija.
	 * 
	 * @param folder direktorija kuri turi buti istrinta
	 * 
	 * @return true, jei direktorija istrinta
	 */
	private boolean deleteAllSubFolders(Folder folder) {

		boolean istrinta = false;
		Folder[] subFolders = null;

		try {
			subFolders = folder.list();

			int listIlgis = subFolders.length;
			/*
			 * Rekursija i subdirektorijas
			 */
			for (int i = 0; i < listIlgis; i++) {

				deleteAllSubFolders(subFolders[i]);

			}
			/*
			 * Jei direktorijoje yra ir laisku juos istrina
			 */
			if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {

				deleteAllMessagesFromFolder(folder);

			}
			/*
			 * jei direktorija atidaryta uzdaro ja
			 */
			closeFolder(folder);
			/*
			 * istrina direktorija
			 */
			if (delFolder(folder)) {

				istrinta = true;

			}

		} catch (MessagingException e) {

			logger.error(e.getMessage());
		}
		return istrinta;
	}

	/**
	 * Sukuria direktorija.
	 * 
	 * @param createFolder norimos sukurti direktorijos pavadinimas/kelias
	 * @param store naudojama store
	 * 
	 * @return true, jei sekmingai sukurta
	 */
	public String createFolder(String createFolder, Store store) {

		String created = "";

		if (createFolder != null && store.isConnected()) {

			Folder dir = null;

			Configuration configuration = new Configuration();

			String folder = configuration
					.removeInvalidChars(createFolder, true);

			/*
			 * Jei reikia sukurti direktorija ir subdirektorija
			 */
			String[] direktorija = folder.split("\\/");

			if (direktorija.length >= 1) {
				/*
				 * Jei reikia sukurti tik 1 direktorija root direktorijoje
				 */
				try {
					if (direktorija.length == 1) {
						
						if(direktorija[0].equals("")) {
							
							direktorija[0]="NoName";
							
						}
						
						dir = store.getFolder(direktorija[0]);

						if (!dir.exists()) {

							dir.create(Folder.HOLDS_MESSAGES);
							dir.setSubscribed(true);
							
							created = dir.getFullName();
							
							logger.info("Directory " + direktorija[0]
									+ " created");
							logger.warn("Direktorija " + direktorija[0]
									+ " sukurta.");
							GUI.spausdinti();
						}else {
							created = dir.getFullName();
						}
						/*
						 * Jei reikia sukurti direktorija bei subdirektorijas
						 */
					} else {

						String kelias = "";

						int keliosSub = direktorija.length;
						
						if(keliosSub>6) {
							
							keliosSub = 5;
							
						}

						for (int i = 0; i < keliosSub; i++) {
							
							if(direktorija[i].equals("")) {
								
								direktorija[i]="NoName";
								
							}
							/*
							 * jei ciklas sukamas pirma karta kelias bus
							 * direktorijos pavadinimas
							 */
							if (i == 0) {

								kelias = direktorija[i];
								/*
								 * jei ciklas sukamas nepirma karta kelias bus
								 * kelias iki siol plius prefixas "/" ir
								 * direktorijos pavadinimas
								 */
							} else {

								kelias += "/" + direktorija[i];

							}

							int n = i;
							/*
							 * Jei paskutine subdirektorija
							 */
							if (++n == keliosSub) {

								URLName urln = new URLName(kelias);

								dir = store.getFolder(urln);
								/*
								 * Jei neegzistuoja sukuria
								 */
								if (!dir.exists()) {

									dir.create(Folder.HOLDS_MESSAGES);
									dir.setSubscribed(true);
									created = dir.getFullName();
									logger.info("Directory " + direktorija[i]
											+ " created");
									logger.warn("Direktorija " + direktorija[i]
											+ " sukurta.");
									GUI.spausdinti();

									break;
								}else {
									created = dir.getFullName();
									break;
								}
								/*
								 * Jei si subdirektorija dar nepaskutine
								 */
							} else {

								URLName urln = new URLName(kelias);

								dir = store.getFolder(urln);
								/*
								 * Jei neegzistuoja sukuria
								 */
								if (!dir.exists()) {

									dir.create(Folder.HOLDS_FOLDERS+Folder.HOLDS_MESSAGES);

									dir.setSubscribed(true);

									logger.info("Directory " + direktorija[i]
											+ " created");
									logger.warn("Direktorija " + direktorija[i]
											+ " sukurta.");
									GUI.spausdinti();

								}

							}

						}
					}

				} catch (MessagingException e) {

					logger.error(e.getMessage());
				}

			}

		} else {
			logger.error("Sotore is not connected, or foldername is invalid.");
		}

		return created;
	}

	/**
	 * Patikrina ar egzistuoja tokia direktorija.
	 * 
	 * @param path kelias kuriuo reikia ieskoti direktorijos
	 * @param store naudojama store
	 * @param name direktorijos pavadinimas
	 * 
	 * @return string[] masyva su visais katalogais patiektu vardu.
	 */
	public String[] folderExists(String path, Store store, String name) {

		Folder dir;

		ArrayList<String> list = new ArrayList<String>();
		String[] nerasta = {""};
		try {
			/*
			 * Jei duotas path kuriame ieskome direktorijos
			 */
			if (path != null) {

				URLName urln = new URLName(path);

				dir = store.getFolder(urln);

				if (dir.exists()) {

					String[] keliasUrl = new String[1];

					keliasUrl[0] = dir.getFullName();
					return keliasUrl;
				}else {
					
					return nerasta;
				}
				/*
				 * Jei neduotas path kuriame ieskome direktorijos
				 */
			} else {
				/*
				 * Nuskaitome visas subDirektorijas ir kreipiames y metoda joms
				 * patikrinti
				 */
				dir = store.getDefaultFolder();

				Folder[] direktorijos = dir.list();

				list = subDirectorija(store, direktorijos, name, list);

				String[] kelias = (String[]) list.toArray(new String[list
						.size()]);
				return kelias;
			}

		} catch (MessagingException e) {

			logger.error(e.getMessage());

		}

		return nerasta;
	}

	/**
	 * Iesko bei patikrina subdirektorijas.
	 * 
	 * @param store naudojama store
	 * @param folderList direktorijos subdirektorijos
	 * @param name ieskomos direktorijos pavadinimas, siunciama null grazinami
	 * visi direktoriju pavadinimai
	 * @param path rastos egsituojancios direktorijos atitinkancios iekoma name
	 * 
	 * @return array list<String> su subdirektoriju sakomis
	 */
	private ArrayList<String> subDirectorija(Store store, Folder[] folderList,

	String name, ArrayList<String> path) {

		Folder CurentDirectory;

		int kiek = folderList.length;
		/*
		 * Sukamas ciklas kiekvienaj subDirektorijai
		 */
		for (int i = 0; i < kiek; i++) {

			try {
				/*
				 * Store pakeiciamas i subDirektorijos store
				 */
				CurentDirectory = store.getFolder(folderList[i].toString());
				/*
				 * suskaiciuoja kiek subDirektorija turi subDirektoriju
				 */
				Folder[] subDirectory = CurentDirectory.list();
				/*
				 * jei nepateiktas direktoriju pavadinimas grazinamos visos
				 * egzistuojancios direltorijos
				 */
				if (name == null) {
					/*
					 * Jei dar egzistuoja subdirektoriju daro rekursija
					 */
					if (subDirectory.length != 0) {

						path.add(folderList[i].getFullName());
						subDirectorija(store, subDirectory, name, path);

					} else {

						path.add(folderList[i].getFullName());

					}

				} else {
					/*
					 * jei Direktorija subDirektoriju nebeturi ir ieskomas name
					 * atitinka direktorijos pavadinima grazinamas kelias iki
					 * Direktorijos
					 */
					if ((folderList[i].getName().equals(name))
							&& (subDirectory.length == 0)) {

						path.add(folderList[i].getFullName());
						/*
						 * jei Direktorija turi subDirektoriju ir ieskomas name
						 * atitinka direktorijos pavadinima grazinamas kelias
						 * iki Direktorijos ir tesiama paieska subDirektorijose
						 */
					} else if ((folderList[i].getName().equals(name))
							&& (subDirectory.length != 0)) {

						path.add(folderList[i].getFullName());

						subDirectorija(store, subDirectory, name, path);
						/*
						 * jei Direktorija turi subDirektoriju tesiama paieska
						 * subDirektorijose
						 */
					} else if (subDirectory.length != 0) {

						subDirectorija(store, subDirectory, name, path);
					}
				}
			} catch (MessagingException e) {

				logger.error(e.getMessage());
			}

		}

		return path;
	}

	/**
	 * Pervadina direktorija.
	 * 
	 * @param store naudojama store
	 * @param path direktorijos jei root direktorija path siunciamas null
	 * @param oldName senasis direktorijos pavadinimas
	 * @param newName naujasis direkorijos pavadinimas
	 * 
	 * @return true, jei pavyko pervadinti
	 */
	public boolean renameFolder(Store store, String path, String oldName,
			String newName) {

		boolean renamed = false;
		Folder oldFolder;
		Folder newFolder;
		String oldPath;
		String newPath;
		/*
		 * kuriamas direktorijos kelias jei yra ne root direktorijoje
		 */
		if (!oldName.equals(newName)) {
			if (path != null) {

				oldPath = path + "/" + oldName;
				newPath = path + "/" + newName;
				/*
				 * jei root direktorijoje
				 */
			} else {

				oldPath = oldName;
				newPath = newName;

			}

			URLName urlOld = new URLName(oldPath);
			URLName urlNew = new URLName(newPath);

			try {

				oldFolder = store.getFolder(urlOld);
				newFolder = store.getFolder(urlNew);
				/*
				 * Patikrina ar pervadinama direktorija uzdaryta ir egzistuoja
				 */
				if ((!oldFolder.isOpen()) && (oldFolder.exists())) {

					oldFolder.renameTo(newFolder);

					renamed = true;
					/*
					 * jei direktorija neuzdaryta ja uzdaro ir pervadina
					 */
				} else if ((oldFolder.isOpen()) && (oldFolder.exists())) {

					oldFolder.close(false);

					oldFolder.renameTo(newFolder);

					renamed = true;

				}

			} catch (MessagingException e) {

				logger.error(e.getMessage());

			}

		}

		return renamed;

	}

	/**
	 * Is gautos String reiksmes sukuria URLName kintamajy.
	 * 
	 * @param path direktorijos kelias
	 * 
	 * @return URLName
	 */
	public URLName getFolderUrln(String path) {

		URLName urln = new URLName(path);

		return urln;

	}

}
