/**
 * @author Sigitas Pleikys   IT - 4/1
 * Siauliu Universitetas 2008
 */
package IMAPC;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Klase GUI.
 */
public class GUI {

	/** Naudojamas Logger. */
	Logger logger = Logger.getLogger(GUI.class);

	/** Imapc pagrindine vykdomoji klase. */
	public IMAPC imapc = new IMAPC();

	/** Gui objektas. */
	private static GUI gui = new GUI();

	/** The login failas. */
	public File loginFailas;
	
	/** The event log file. */
	private String eventLogFile = "IMAPConnectorEVENTS.log";

	/** GUI kalba jei true lt jei false en. */
	private boolean language = true;

	/** Taisykliu failas. */
	public String rulesFailas;

	/** Nuskaityti gyja. */
	private static Thread nuskaityti;

	/** Ar nuskaitinejamas failas. */
	private boolean readingFile = false;

	/** Paskutinis nuskaitymas. */
	private long lastReadTime = System.currentTimeMillis();
	
	/** Paskutinis ivykiu nuskaitymas . */
	private long lastEventReadTime = System.currentTimeMillis();
	
	/** Log failas. */
	private String logFile;

	/** LT log failo kelias. */
	public String logFileLT = "IMAPConnectorLT.log";

	/** EN log failo kelias. */
	public String logFileEN = "IMAPConnectorEN.log";

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** Sustabdymo mygtukas. */
	public JButton stopButton;

	/** Isvedimo scroll panel. */
	private JScrollPane isvedimoScrollPane;

	/** Isvedimo text area. */
	private JTextArea isvedimoTextArea;

	/** Pagrindine Panel. */
	private JPanel mainPanel;

	/** Login mygtukas. */
	public JButton loginButton;

	/** Login label. */
	private JLabel loginLabel;

	/** Logo label. */
	private JLabel logoLabel;

	/** Rules mygtukas. */
	public JButton rulesButton;

	/** Rules label. */
	private JLabel rulesLabel;

	/** Start mygtukas. */
	public JButton startButton;

	/** Iconu kelias. */
	private String iconsPath;

	/** Pagrindine frame. */
	private JFrame guiFrame;

	/** Kalbu pasirinkimo dialogas. */
	private JDialog languageDialog;

	/** English mygtukas. */
	private JButton englishButton;

	/** Kalbu pasirinkimo label. */
	private JLabel languageLabel;

	/** Kalbu pasirinkimo panele. */
	private JPanel languagePanel;

	/** Lithuanian mygtukas. */
	private JButton lithuanianButton;

	/** Ivykio lango mygtukas. */
	private JButton notifyButton;

	/** Ivykio lango logo. */
	private JLabel notifyLogo;

	/** Ivykio lango  pagrindine panel. */
	private JPanel notifyPanel;

	/** Ivykio langas. */
	private JDialog notifyDialog;

	/** Ivykio lango Teksto laukas. */
	private JTextArea notifyTextArea;

	/** Ivykio lango Scroll pane. */
	private JScrollPane notifyScrollPane;
	
	/** Nuskaityti ivyki gija. */
	private static Thread nuskaitytiEvent;

	/** Logger nustatymu failas. */
	private static String loggerPropsFile = "IMAPCLoggerProperties.xml";
	
	/** Skaitomas ivykiu failas. */
	private boolean readingEventFile = false;
	
	/** Atidarytas ivykiu langas. */
	private static boolean openedEventWindow = false;
	
	/** Atidarytas gui langas - sis kintamais skirtas testavimui. */
	private static boolean opened = false;
	
/**
 * Main metodas
 * 
 * @param args argumentai
 */
public static void main(String[] args) {
		URL url = Loader.getResource(loggerPropsFile);

		if (url != null) {
			DOMConfigurator.configure(url);
		}
		gui.languageWindow();
	}

	/**
	 * Paleidzia gui ir jo komponentus.
	 */
	public void startGUI() {
		opened = true;
		String labelTekstas;
		/*
		 * Jei false kelia iki mygtuku iconu en direktorijoje jei true lt
		 * direktorijoje
		 */
		if (!language) {
			this.iconsPath = "/img/en/";
			labelTekstas = "File not chosen";
		} else {
			this.iconsPath = "/img/lt/";
			labelTekstas = "Failas nepasirinktas";
		}

		mainPanel = new JPanel();
		startButton = new JButton();
		loginButton = new JButton();
		rulesButton = new JButton();
		stopButton = new JButton();

		logoLabel = new JLabel();
		isvedimoScrollPane = new JScrollPane();
		isvedimoTextArea = new JTextArea();
		loginLabel = new JLabel();
		rulesLabel = new JLabel();

		guiFrame = new JFrame();
		guiFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		guiFrame.setResizable(false);

		mainPanel.setBackground(new Color(64, 64, 64));

		startButton.setIcon(new ImageIcon(getUrl(iconsPath+"start.jpg")));
		
		startButton.setDisabledIcon(new ImageIcon(getUrl(iconsPath+"startDisabled.jpg")));
		
		startButton.setEnabled(false);
		startButton.addMouseListener(new MouseAdapter() {

			public void mouseEntered(MouseEvent evt) {
				startButtonMouseEntered(evt);
			}

			public void mouseExited(MouseEvent evt) {
				startButtonMouseExited(evt);
			}

		});
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				startButtonActionPerformed(evt);
			}
		});

		loginButton.setIcon(new ImageIcon(getUrl(iconsPath + "login.jpg")));
		loginButton.setDisabledIcon(new ImageIcon(getUrl(iconsPath
				+ "loginDisabled.jpg")));
		loginButton.setEnabled(true);
		loginButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				loginButtonMouseEntered(evt);
			}

			public void mouseExited(MouseEvent evt) {
				loginButtonMouseExited(evt);
			}
		});
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				loginButtonActionPerformed(evt);
			}
		});

		rulesButton.setIcon(new ImageIcon(getUrl(iconsPath + "rules.jpg")));
		rulesButton.setDisabledIcon(new ImageIcon(getUrl(iconsPath
				+ "rulesDisabled.jpg")));
		rulesButton.setEnabled(false);
		rulesButton.setVisible(false);
		rulesButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				rulesButtonMouseEntered(evt);
			}

			public void mouseExited(MouseEvent evt) {
				rulesButtonMouseExited(evt);
			}
		});
		rulesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rulesButtonActionPerformed(evt);
			}
		});

		stopButton.setIcon(new ImageIcon(getUrl(iconsPath + "stop.jpg")));
		stopButton
				.setDisabledIcon(new ImageIcon(getUrl(iconsPath + "stopDisabled.jpg")));
		stopButton.setEnabled(false);
		stopButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				stopButtonMouseEntered(evt);
			}

			public void mouseExited(MouseEvent evt) {
				stopButtonMouseExited(evt);
			}
		});
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				stopButtonActionPerformed(evt);
			}
		});

		isvedimoTextArea.setBackground(new Color(85, 255, 0));
		isvedimoTextArea.setColumns(20);
		isvedimoTextArea.setFont(new Font("Arial", 0, 12));
		isvedimoTextArea.setRows(5);
		isvedimoScrollPane.setViewportView(isvedimoTextArea);

		logoLabel.setIcon(new ImageIcon(getUrl("/img/logo.jpg")));

		loginLabel.setFont(new Font("Arial", 1, 14));
		loginLabel.setForeground(new Color(255, 0, 0));
		loginLabel.setText(labelTekstas);

		rulesLabel.setFont(new Font("Arial", 1, 14));
		rulesLabel.setForeground(new Color(255, 0, 0));
		rulesLabel.setText(labelTekstas);
		rulesLabel.setVisible(false);

		GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		mainPanelLayout
				.setHorizontalGroup(mainPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								mainPanelLayout
										.createSequentialGroup()
										.addGroup(
												mainPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addGroup(
																mainPanelLayout
																		.createSequentialGroup()
																		.addGap(
																				123,
																				123,
																				123)
																		.addComponent(
																				logoLabel))
														.addGroup(
																mainPanelLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				mainPanelLayout
																						.createParallelGroup(
																								GroupLayout.Alignment.TRAILING)
																						.addComponent(
																								loginButton,
																								GroupLayout.PREFERRED_SIZE,
																								283,
																								GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								rulesButton,
																								GroupLayout.PREFERRED_SIZE,
																								283,
																								GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				mainPanelLayout
																						.createParallelGroup(
																								GroupLayout.Alignment.LEADING)
																						.addComponent(
																								loginLabel,
																								GroupLayout.Alignment.TRAILING,
																								GroupLayout.PREFERRED_SIZE,
																								210,
																								GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								rulesLabel,
																								GroupLayout.DEFAULT_SIZE,
																								211,
																								Short.MAX_VALUE)))
														.addGroup(
																mainPanelLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				stopButton,
																				GroupLayout.PREFERRED_SIZE,
																				498,
																				GroupLayout.PREFERRED_SIZE))
														.addGroup(
																mainPanelLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				mainPanelLayout
																						.createParallelGroup(
																								GroupLayout.Alignment.TRAILING,
																								false)
																						.addComponent(
																								isvedimoScrollPane,
																								GroupLayout.Alignment.LEADING)
																						.addComponent(
																								startButton,
																								GroupLayout.Alignment.LEADING,
																								GroupLayout.PREFERRED_SIZE,
																								498,
																								GroupLayout.PREFERRED_SIZE))))
										.addContainerGap()));
		mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				mainPanelLayout.createSequentialGroup().addComponent(logoLabel,
						GroupLayout.PREFERRED_SIZE, 81,
						GroupLayout.PREFERRED_SIZE).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED).addComponent(
						isvedimoScrollPane, GroupLayout.PREFERRED_SIZE, 232,
						GroupLayout.PREFERRED_SIZE).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED).addComponent(
						startButton, GroupLayout.PREFERRED_SIZE, 30,
						GroupLayout.PREFERRED_SIZE).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED).addGroup(
						mainPanelLayout.createParallelGroup(
								GroupLayout.Alignment.LEADING).addComponent(
								loginLabel, GroupLayout.DEFAULT_SIZE, 30,
								Short.MAX_VALUE).addComponent(loginButton,
								GroupLayout.PREFERRED_SIZE, 30,
								GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED).addGroup(
						mainPanelLayout.createParallelGroup(
								GroupLayout.Alignment.LEADING).addComponent(
								rulesLabel, GroupLayout.DEFAULT_SIZE, 30,
								Short.MAX_VALUE).addComponent(rulesButton,
								GroupLayout.PREFERRED_SIZE, 30,
								GroupLayout.PREFERRED_SIZE)).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED).addComponent(
						stopButton, GroupLayout.PREFERRED_SIZE, 30,
						GroupLayout.PREFERRED_SIZE).addContainerGap()));

		GroupLayout layout = new GroupLayout(guiFrame.getContentPane());
		guiFrame.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(mainPanel,
				GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(mainPanel,
				GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE));

		guiFrame.pack();
		guiFrame.setTitle("IMAPConnector");
		guiFrame.setVisible(true);
		/**
		 * Gija skirta log failo nuskaitymui
		 */
		nuskaityti = new Thread() {
			public void run() {
				try {

					if (!readingFile) {

						readingFile = true;
						try {
							if (language) {

								logFile = logFileLT;

							} else {

								logFile = logFileEN;
							}

							BufferedReader in = new BufferedReader(
									new FileReader(logFile));
							String str;
							long finalTime = 0;

							while ((str = in.readLine()) != null) {
								StringTokenizer st = new StringTokenizer(str,
										" ");
								String date = st.nextToken();
								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyy/MM/dd-HH:mm:ss:SSS");
								long curentTime = sdf.parse(date).getTime();
								if (curentTime > lastReadTime) {

									StringTokenizer strg = new StringTokenizer(
											str, "=>");
									String txt = strg.nextToken();
									final String txt2 = strg.nextToken();

									final int lines = isvedimoTextArea
											.getLineCount();

									isvedimoTextArea.append(lines + ": " + txt2
											+ "\n");

								}
								finalTime = curentTime;
							}
							if (finalTime == 0) {
								finalTime = System.currentTimeMillis();
							}
							lastReadTime = finalTime;
							in.close();
							readingFile = false;
							this.wait();
						} catch (IOException e) {
						}

					}

				} catch (Exception e) {

				}

			};
		};
		nuskaityti.start();

	}
	
	public URL getUrl(String s) {
		URL u = getClass().getResource(s);
		return u;
	}

	/**
	 * Kalbu pasirinkimo langas.
	 */
	public void languageWindow() {
		languageDialog = new JDialog();
		languagePanel = new JPanel();
		englishButton = new JButton();
		lithuanianButton = new JButton();
		languageLabel = new JLabel();

		languageDialog
				.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		languagePanel.setBackground(new Color(64, 64, 64));

		englishButton.setIcon(new ImageIcon(getUrl("/img/lng/en.jpg")));
		englishButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				englishButtonMouseEntered(evt);
			}

			public void mouseExited(MouseEvent evt) {
				englishButtonMouseExited(evt);
			}
		});
		englishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				englishButtonActionPerformed(evt);
			}
		});

		lithuanianButton.setIcon(new ImageIcon(getUrl("/img/lng/lt.jpg")));
		lithuanianButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				lithuanianButtonMouseEntered(evt);
			}

			public void mouseExited(MouseEvent evt) {
				lithuanianButtonMouseExited(evt);
			}
		});
		lithuanianButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				lithuanianButtonActionPerformed(evt);
			}
		});
		
		languageLabel.setIcon(new ImageIcon(getUrl("/img/lng/langugeLabel.jpg")));

		GroupLayout languagePanelLayout = new GroupLayout(languagePanel);
		languagePanel.setLayout(languagePanelLayout);
		languagePanelLayout.setHorizontalGroup(languagePanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
						languagePanelLayout.createSequentialGroup()
								.addContainerGap().addComponent(
										lithuanianButton,
										GroupLayout.PREFERRED_SIZE, 200,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(englishButton,
										GroupLayout.PREFERRED_SIZE, 200,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(12, Short.MAX_VALUE))
				.addGroup(
						GroupLayout.Alignment.TRAILING,
						languagePanelLayout.createSequentialGroup()
								.addContainerGap(74, Short.MAX_VALUE)
								.addComponent(languageLabel,
										GroupLayout.PREFERRED_SIZE, 304,
										GroupLayout.PREFERRED_SIZE).addGap(50,
										50, 50)));
		languagePanelLayout
				.setVerticalGroup(languagePanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								GroupLayout.Alignment.TRAILING,
								languagePanelLayout
										.createSequentialGroup()
										.addGap(32, 32, 32)
										.addComponent(languageLabel)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED,
												26, Short.MAX_VALUE)
										.addGroup(
												languagePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.TRAILING)
														.addComponent(
																lithuanianButton,
																GroupLayout.PREFERRED_SIZE,
																30,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																englishButton,
																GroupLayout.PREFERRED_SIZE,
																30,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));

		GroupLayout layout = new GroupLayout(languageDialog.getContentPane());
		languageDialog.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(languagePanel,
				GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(languagePanel,
				GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE));
		languageDialog.setResizable(false);
		languageDialog.pack();
		languageDialog.setTitle("Choose language for GUI");
		languageDialog.setVisible(true);

	}

	/**
	 * Ivykiu langas.
	 * Skirtas pranesti vartotojui apie tam tikrus ivykius.
	 */
	public void eventWindow() {
		notifyDialog = new JDialog();
		notifyPanel = new JPanel();
		notifyLogo = new JLabel();
		notifyButton = new JButton();
		notifyScrollPane = new JScrollPane();
		notifyTextArea = new JTextArea();

		notifyDialog.setResizable(false);
		notifyDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		notifyPanel.setBackground(new java.awt.Color(64, 64, 64));

		notifyLogo.setIcon(new ImageIcon(getUrl("/img/notify/notifyLogo.jpg")));

		notifyButton.setIcon(new ImageIcon(getUrl("/img/notify/close.jpg")));
		notifyButton.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				notifyButtonMouseEntered(evt);
			}

			public void mouseExited(MouseEvent evt) {
				notifyButtonMouseExited(evt);
			}
		});
		notifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				notifyButtonActionPerformed(evt);
			}
		});

		notifyTextArea.setBackground(new java.awt.Color(0, 255, 0));
		notifyTextArea.setColumns(20);
		notifyTextArea.setEditable(false);
		notifyTextArea.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
		notifyTextArea.setRows(5);
		notifyScrollPane.setViewportView(notifyTextArea);

		GroupLayout notifyPanelLayout = new GroupLayout(notifyPanel);
		notifyPanel.setLayout(notifyPanelLayout);
		notifyPanelLayout
				.setHorizontalGroup(notifyPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								notifyPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												notifyPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.TRAILING,
																false)
														.addComponent(
																notifyLogo,
																GroupLayout.Alignment.LEADING)
														.addComponent(
																notifyButton,
																GroupLayout.Alignment.LEADING,
																GroupLayout.PREFERRED_SIZE,
																280,
																Short.MAX_VALUE)
														.addComponent(
																notifyScrollPane,
																GroupLayout.Alignment.LEADING))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		notifyPanelLayout.setVerticalGroup(notifyPanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
						notifyPanelLayout.createSequentialGroup().addComponent(
								notifyLogo).addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(notifyScrollPane,
										GroupLayout.DEFAULT_SIZE, 133,
										Short.MAX_VALUE).addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(notifyButton,
										GroupLayout.PREFERRED_SIZE, 30,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap()));

		GroupLayout layout = new GroupLayout(notifyDialog.getContentPane());
		notifyDialog.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(notifyPanel,
				GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(notifyPanel,
				GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				GroupLayout.PREFERRED_SIZE));
		nuskaitytiEvent = new Thread() {
			public void run() {
				try {

					if (!readingEventFile) {

						readingEventFile = true;
						try {

							BufferedReader in = new BufferedReader(
									new FileReader(eventLogFile));
							String str;
							long finalTime = 0;

							while ((str = in.readLine()) != null) {
								StringTokenizer st = new StringTokenizer(str,
										" ");
								String date = st.nextToken();
								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyy/MM/dd-HH:mm:ss:SSS");
								long curentTime = sdf.parse(date).getTime();
								if (curentTime > lastEventReadTime) {

									StringTokenizer strg = new StringTokenizer(
											str, "=>");
									String txt = strg.nextToken();
									final String txt2 = strg.nextToken();

									final int lines = notifyTextArea
											.getLineCount();

									notifyTextArea.append(lines + ": " + txt2
											+ "\n");

								}
								finalTime = curentTime;
							}
							if (finalTime == 0) {
								finalTime = System.currentTimeMillis();
							}
							lastEventReadTime = finalTime;
							in.close();
							readingEventFile = false;
							this.wait();
						} catch (IOException e) {
						}

					}

				} catch (Exception e) {

				}

			};
		};
		nuskaitytiEvent.start();
		notifyDialog.setTitle("User events");
		notifyDialog.pack();
		notifyDialog.setVisible(true);
	}

	/**
	 * Spausdinti suzadina gija skirta spausdinti y text area.
	 */
	public static void spausdinti() {
		
		if (!nuskaityti.isAlive()) {
			nuskaityti.run();
		} else {
			try {
				nuskaityti.join();
			} catch (InterruptedException e) {
			}
		
		}
	}
	
	/**
	 * Spausdinti event.
	 */
	public static void spausdintiEvent() {
		if(openedEventWindow) {
		if (!nuskaitytiEvent.isAlive()) {
			nuskaitytiEvent.run();
		} else {
			try {
				nuskaitytiEvent.join();
			} catch (InterruptedException e) {
			}
		}
		}else {
			gui.eventWindow();
			openedEventWindow = true;
		}
	}

	// -------------- START MIGTUKAS --------------------
	/**
	 * Start mygtukas paspaustas. Sukuria nauja gyja veiksmu sekai su el
	 * pastu atlikti
	 * 
	 * @param evt 
	 */
	private void startButtonActionPerformed(ActionEvent evt) {
		Thread mainThread = new Thread(new Runnable() {
			public void run() {
				imapc.gui = gui;
				imapc.start();

			};
		});
		mainThread.start();

	}

	/**
	 * Start mygtukas pele uzejo.
	 * 
	 * @param evt 
	 */
	private void startButtonMouseEntered(MouseEvent evt) {

		startButton.setIcon(new ImageIcon(getUrl(this.iconsPath + "startOver.jpg")));
	}

	/**
	 * Start mygtukas pele patraukta.
	 * 
	 * @param evt 
	 */
	private void startButtonMouseExited(MouseEvent evt) {
		startButton.setIcon(new ImageIcon(getUrl(this.iconsPath + "start.jpg")));
	}

	// ----------- LOGIN DATA MIGTUKAS ---------------
	/**
	 * Login mygtukas paspaustas.
	 * 
	 * @param evt 
	 */
	private void loginButtonActionPerformed(ActionEvent evt) {
		JFileChooser fc = new JFileChooser("...");
		fc.setDialogTitle("Pasirinkite prisijungimo duomenu faila");
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new MyFilter("xml"));
		int returnVal = fc.showOpenDialog(loginButton);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			String selectedFile = fc.getSelectedFile().getName();
			File file = fc.getSelectedFile();
			this.loginFailas = file;
			imapc.loginFailas = this.loginFailas;
			loginLabel.setForeground(new Color(85, 255, 0));
			loginLabel.setText(" " + selectedFile);
			logger.warn("Pasirinktas prisijungimo nustatymu failas "
					+ selectedFile);
			logger.info("File with login data chosen: " + selectedFile);
			spausdinti();

			if (loginFailas != null) {
				startButton.setEnabled(true);
			}
		}
	}

	/**
	 * Login mygtukas pele uzejo.
	 * 
	 * @param evt 
	 */
	private void loginButtonMouseEntered(MouseEvent evt) {
		loginButton.setIcon(new ImageIcon(getUrl(this.iconsPath + "loginOver.jpg")));
	}

	/**
	 * Login mygtukas pele patraukta.
	 * 
	 * @param evt the evt
	 */
	private void loginButtonMouseExited(MouseEvent evt) {
		loginButton.setIcon(new ImageIcon(getUrl(this.iconsPath + "login.jpg")));
	}

	// ---------- ADD RULES MYGTUKAS ---------------------
	/**
	 * Rules mygtukas paspaustas.
	 * 
	 * @param evt 
	 */
	private void rulesButtonActionPerformed(ActionEvent evt) {
		JFileChooser fc = new JFileChooser("src/Rules");
		fc.setDialogTitle("Pasirinkite taisykliu faila");
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new MyFilter("xls"));
		int returnVal = fc.showOpenDialog(rulesButton);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String selectedFile = fc.getSelectedFile().getName();
			String rulesPath = "/Rules/" + selectedFile;
			this.rulesFailas = rulesPath;
			imapc.rulesFailas = this.rulesFailas;
			rulesLabel.setForeground(new Color(85, 255, 0));
			rulesLabel.setText(" " + selectedFile);
			logger.warn("Pasirinktas prisijungimo nustatymu failas "
					+ selectedFile);
			logger.info("File with rules for e-mail chosen: " + selectedFile);
			spausdinti();
			if ((loginFailas != null) && (rulesFailas != null)) {
				startButton.setEnabled(true);
			}

		}

	}

	/**
	 * Rules mygtukas pele uzejo.
	 * 
	 * @param evt 
	 */
	private void rulesButtonMouseEntered(MouseEvent evt) {
		rulesButton.setIcon(new ImageIcon(getUrl(this.iconsPath + "rulesOver.jpg")));
	}

	/**
	 * Rules mygtukas pele patraukta.
	 * 
	 * @param evt 
	 */
	private void rulesButtonMouseExited(MouseEvent evt) {
		rulesButton.setIcon(new ImageIcon(getUrl(this.iconsPath + "rules.jpg")));
	}

	// ------------------ STOP MIGTUKAS -----------------------
	/**
	 * Exit mygtukas paspaustas.
	 * 
	 * @param evt 
	 */
	private void stopButtonActionPerformed(ActionEvent evt) {
		System.exit(0);
	}

	/**
	 * Exit mygtukas pele uzejo.
	 * 
	 * @param evt 
	 */
	private void stopButtonMouseEntered(MouseEvent evt) {
		stopButton.setIcon(new ImageIcon(getUrl(this.iconsPath + "stopOver.jpg")));
	}

	/**
	 * Exit mygtukas pale patraukta.
	 * 
	 * @param evt 
	 */
	private void stopButtonMouseExited(MouseEvent evt) {
		stopButton.setIcon(new ImageIcon(getUrl(this.iconsPath + "stop.jpg")));
	}

	// --------------- Language Dialogas --------------------

	/**
	 * English mygtukas paspaustas.
	 * 
	 * @param evt 
	 */
	private void englishButtonActionPerformed(ActionEvent evt) {
		this.language = false;
		languageDialog.dispose();
		startGUI();
	}

	/**
	 * English mygtukas pele uzejo.
	 * 
	 * @param evt 
	 */
	private void englishButtonMouseEntered(MouseEvent evt) {
		englishButton.setIcon(new ImageIcon(getUrl("/img/lng/enOver.jpg")));
	}

	/**
	 * English mygtukas pele patraukta.
	 * 
	 * @param evt 
	 */
	private void englishButtonMouseExited(MouseEvent evt) {
		englishButton.setIcon(new ImageIcon(getUrl("/img/lng/en.jpg")));
	}

	/**
	 * Lithuanian mygtukas pele uzejo.
	 * 
	 * @param evt 
	 */
	private void lithuanianButtonMouseEntered(MouseEvent evt) {
		lithuanianButton.setIcon(new ImageIcon(getUrl("/img/lng/ltOver.jpg")));
	}

	/**
	 * Lithuanian mygtukas pele patraukta.
	 * 
	 * @param evt 
	 */
	private void lithuanianButtonMouseExited(MouseEvent evt) {
		lithuanianButton.setIcon(new ImageIcon(getUrl("/img/lng/lt.jpg")));
	}

	/**
	 * Lithuanian mygtukas paspaustas.
	 * 
	 * @param evt the evt
	 */
	private void lithuanianButtonActionPerformed(ActionEvent evt) {
		this.language = true;
		languageDialog.dispose();
		startGUI();
	}

	//---------------------------- EVENT DIALOGAS ------------------------
	/**
	 * Notify lango mygtukas paspaustas.
	 * 
	 * @param evt 
	 */
	private void notifyButtonActionPerformed(ActionEvent evt) {
		openedEventWindow = false;
		notifyDialog.dispose();
	}

	/**
	 * Notify lango mygtukas pele uzejo.
	 * 
	 * @param evt 
	 */
	private void notifyButtonMouseEntered(MouseEvent evt) {
		notifyButton.setIcon(new ImageIcon(getUrl("/img/notify/closeOver.jpg")));
	}

	/**
	 * Notify lango mygtukas pele patraukta.
	 * 
	 * @param evt
	 */
	private void notifyButtonMouseExited(MouseEvent evt) {
		notifyButton.setIcon(new ImageIcon(getUrl("/img/notify/close.jpg")));
	}

}

/**
 * Klase MyFilter.
 */
final class MyFilter extends FileFilter {

	/** Failo tipas. */
	private String tipas;

	/**
	 * My filter construktorius
	 * 
	 * @param tipas
	 *            failo tipas
	 */
	public MyFilter(String tipas) {
		this.tipas = tipas;
	}

	/**
	 * Metodas kuri norime pakeisti.
	 * 
	 * @param file
	 *            failas
	 * 
	 * @return true, jei rodyti
	 */
	public boolean accept(File file) {
		/*
		 * Jei direktorija rodyti
		 */
		if (file.isDirectory()) {
			return true;
		}
		String extension = getExtension(file);
		/*
		 * tikriname ar failo galune xml
		 */
		if (extension.equals(tipas)) {
			return true;
		}
		return false;
	}

	/**
	 * Failo filtro aprasymas.
	 * 
	 * @return filtro aprasymas
	 */
	public String getDescription() {
		return tipas + " failai";
	}

	/**
	 * Gauti failo galune.
	 * 
	 * @param file
	 *            failas
	 * 
	 * @return galune
	 */
	private String getExtension(File file) {
		String s = file.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1)
			return s.substring(i + 1).toLowerCase();
		return "";
	}
}