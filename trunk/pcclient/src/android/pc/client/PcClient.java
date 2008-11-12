package android.pc.client;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import android.pc.client.api.PcClientAPI;
import android.pc.client.api.PcClientAPIInterface;


/**
 * Classe PcClient: implementazione di un client con le api fornite dalla classe PcClientAPI
 * dialoga con il server e può settare la modalità preferità da cui essere contattati 
 * scegliendola tra quelle presenti sul PC cioè Mail ed IM.
 * 
 * Tale Client è platform indipendent (Testato su Linux Mac e Windows x86)
 *  
 * @author Nicolas Tagliani e Vincenzo Frascino
 *
 */
public class PcClient {

	/**
	 * Costruttore PcClient si occupa di inializzare ed istanziare la API fornite da PcClientAPI
	 * e di istanziare i principali oggetti grafici che costituiscono il client
	 */
	public PcClient() {
		
		final PcClientAPIInterface pcapi = new PcClientAPI();
		
		final TrayIcon trayIcon;

		if (SystemTray.isSupported()) {

		    SystemTray tray = SystemTray.getSystemTray();
		    final Image image = Toolkit.getDefaultToolkit().getImage("res/inactive.png");
		    final Image updatedImage = Toolkit.getDefaultToolkit().getImage("res/active.png");

		    PopupMenu popup = new PopupMenu();
		    trayIcon = new TrayIcon(image, "Android Contact PC Client", popup);

		    ActionListener connectListener = new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		pcapi.connect();
		    		trayIcon.setImage(updatedImage);
		    		trayIcon.displayMessage("Android Contact PC Client", 
		    		           "Connected!",
		    		           TrayIcon.MessageType.INFO);
		    	}
		    };
		    
		    MenuItem connectItem = new MenuItem("Connect");
		    connectItem.addActionListener(connectListener);
		    popup.add(connectItem);
		    
		    ActionListener mailListener = new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		if(pcapi.mail()) {
			    		trayIcon.displayMessage("Android Contact PC Client", 
			    		           "Mail Setted as Preferred!",
			    		           TrayIcon.MessageType.INFO);
		    		} else {
		    			trayIcon.displayMessage("Android Contact PC Client", 
			    		           "Connect Before!",
			    		           TrayIcon.MessageType.ERROR);
		    		}
		    	}
		    };
		    
		    MenuItem mailItem = new MenuItem("Set Mail as Preferred");
		    mailItem.addActionListener(mailListener);
		    popup.add(mailItem);
		    
		    ActionListener imListener = new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		if(pcapi.im()) {
			    		trayIcon.displayMessage("Android Contact PC Client", 
			    		           "IM Setted as Preferred!",
			    		           TrayIcon.MessageType.INFO);
		    		} else {
		    			trayIcon.displayMessage("Android Contact PC Client", 
			    		           "Connect Before!",
			    		           TrayIcon.MessageType.ERROR);
		    		}
		    	}
		    };
		    
		    MenuItem imItem = new MenuItem("Set IM as Preferred");
		    imItem.addActionListener(imListener);
		    popup.add(imItem);
		    
		    ActionListener homeListener = new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		if(pcapi.home()) {
			    		trayIcon.displayMessage("Android Contact PC Client", 
			    		           "HOME Setted as Preferred!",
			    		           TrayIcon.MessageType.INFO);
		    		} else {
		    			trayIcon.displayMessage("Android Contact PC Client", 
			    		           "Connect Before!",
			    		           TrayIcon.MessageType.ERROR);
		    		}
		    	}
		    };
		    
		    MenuItem homeItem = new MenuItem("Set HOME as Preferred");
		    imItem.addActionListener(homeListener);
		    popup.add(homeItem);
		    
		    ActionListener workListener = new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		if(pcapi.work()) {
			    		trayIcon.displayMessage("Android Contact PC Client", 
			    		           "WORK Setted as Preferred!",
			    		           TrayIcon.MessageType.INFO);
		    		} else {
		    			trayIcon.displayMessage("Android Contact PC Client", 
			    		           "Connect Before!",
			    		           TrayIcon.MessageType.ERROR);
		    		}
		    	}
		    };
		    
		    MenuItem workItem = new MenuItem("Set WORK as Preferred");
		    imItem.addActionListener(workListener);
		    popup.add(workItem);
		    
		    ActionListener mobileListener = new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		if(pcapi.mobile()) {
			    		trayIcon.displayMessage("Android Contact PC Client", 
			    		           "MOBILE Setted as Preferred!",
			    		           TrayIcon.MessageType.INFO);
		    		} else {
		    			trayIcon.displayMessage("Android Contact PC Client", 
			    		           "Connect Before!",
			    		           TrayIcon.MessageType.ERROR);
		    		}
		    	}
		    };
		    
		    MenuItem mobileItem = new MenuItem("Set MOBILE as Preferred");
		    imItem.addActionListener(mobileListener);
		    popup.add(mobileItem);
		    
		    ActionListener disconnectListener = new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
		    		pcapi.disconnect();
		    		trayIcon.setImage(image);
		    		trayIcon.displayMessage("Android Contact PC Client", 
		    		           "Disconnected!",
		    		           TrayIcon.MessageType.INFO);
		    	}
		    };
		    
		    MenuItem disconnectItem = new MenuItem("Disconnect");
		    disconnectItem.addActionListener(disconnectListener);
		    popup.add(disconnectItem);
		    
		    ActionListener exitListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("Exiting...");
		            System.exit(0);
		        }
		    };
		            
		    MenuItem defaultItem = new MenuItem("Exit");
		    defaultItem.addActionListener(exitListener);
		    popup.add(defaultItem);
		    
		    ActionListener actionListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            trayIcon.displayMessage("Android Contact PC Client", 
		                "This is the PC Version of Android Contact Client!",
		                TrayIcon.MessageType.INFO);
		        }
		    };
		            
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addActionListener(actionListener);

		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println("TrayIcon could not be added.");
		    }

		} else {

		    System.out.println("PcClient.PcClient():SystemTray not supported!");
		}
		
	}
	
	/**
	 * Metodo main fa partire l'applicazione
	 * @param args
	 */
	public static void main(String[] args) {
		new PcClient();

	}

	
}
