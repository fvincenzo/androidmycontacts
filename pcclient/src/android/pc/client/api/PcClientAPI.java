package android.pc.client.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * Classe PcClientAPI fornisce un'implementazione parziale delle api di comunicazione con l'AndroidContactServer
 * Implementa l'interfaccia PcClientAPIInterface
 * 
 * @author Nicolas Tagliani e Vincenzo Frascino
 *
 */
public class PcClientAPI implements PcClientAPIInterface {
	
	private Socket kkSocket = null;
    private PrintWriter out = null;
    @SuppressWarnings("unused")
	private BufferedReader in = null;
    private String Host;
    private String Username;
	private String Password;
    private int connected = 0; 
	
	
	public PcClientAPI() {
		
	}
	
	/**
	 * Metodo Connect consente la connessione ad un AndroidContactServer
	 */
	public void connect() {
		
		try {
			BufferedReader i = new BufferedReader(new FileReader("conf/config.txt"));
			Host = i.readLine();
			Username = i.readLine();
			Password = i.readLine();
			System.out.println("host:"+Host+"username:"+Username+"password:"+Password);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		try {
            kkSocket = new Socket(Host, 4444);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: darkstar.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: darkstar.");
            System.exit(1);
        }
		
        connected = 1;
        
		//Lancio dei comandi
		out.println("FORCELOGIN");
		out.println(Username);
		out.println(Password);
		out.println("END");
		
	}
	
	/**
	 * Metodo mail consente di settare la mail come preferita su AndroidContactServer
	 */
	public boolean mail() {
		
		if(connected == 1) {
			out.println("SETPREFERRED");
			out.println(Username);
			out.println("MAIL");
			out.println("END");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Metodo im consente di settare l' im come preferita su AndroidContactServer
	 */
	public boolean im() {
		
		if(connected == 1) {
			out.println("SETPREFERRED");
			out.println(Username);
			out.println("IM");
			out.println("END");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Metodo home consente di settare l'home come preferita su AndroidContactServer
	 */
	public boolean home() {
		
		if(connected == 1) {
			out.println("SETPREFERRED");
			out.println(Username);
			out.println("HOME");
			out.println("END");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Metodo work consente di settare il work come preferita su AndroidContactServer
	 */
	public boolean work() {
		
		if(connected == 1) {
			out.println("SETPREFERRED");
			out.println(Username);
			out.println("WORK");
			out.println("END");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Metodo mobile consente di settare il mobile come preferita su AndroidContactServer
	 */
	public boolean mobile() {
		
		if(connected == 1) {
			out.println("SETPREFERRED");
			out.println(Username);
			out.println("MOBILE");
			out.println("END");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Metodo Disconnect consente la disconnessione ad un AndroidContactServer
	 */
	public void disconnect(){
	
		connected = 0;
		out.println("QUIT");
		out.println("END");
	}

}
