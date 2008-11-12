package android.server;

import java.net.*;
import java.util.Vector;
import java.io.*;

/**
 * Classe che implementa il socket server del sistema. Questa classe si occupa di gestire le richieste che provengono da un client.  Esistera' un'istanza di questa classe per ogni utente connesso.
 * @author  Nicolas Tagliani
 * @author  Vincenzo Frascino
 */
public class SocketServer extends Thread{

	private  String result;
	private Socket clientSocket;
	/**
	 * @uml.property  name="u"
	 * @uml.associationEnd  
	 */
	private User u;
	/**
	 * @uml.property  name="um"
	 * @uml.associationEnd  
	 */
	private UserManager um = UserManager.getHinstance();
	private boolean run = true;
	private boolean logged;
	private String username;

	/**
	 * Costruttore per la classe SocketServer riceve un socket su cui è stata accettata una connessione
	 * 
	 * @param s Il socket su cui è stata accettata una connessione
	 */
	public SocketServer(Socket s) {
		System.out.println("Android Server ver 0.20 (with MySQL Support)");
		clientSocket = s;

	}

	public void run(){

		try {
			Vector<String> command = new Vector<String>();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			//Server version
			out.println("Android Server ver 0.20 (with MySQL Support)");
			//Server Welcome Message
			out.println("Welcome in Darkstar Contact Server.");
			//Server Status
			out.println("Status: up and running.");
			int size = 0;
			while(run) {
				result = "";
				command.add(in.readLine());

				while(!command.lastElement().equals("END")) {
					size ++;
					command.add(in.readLine());
				}


				System.out.println("Command: "+command);

				if(command.firstElement().equals("STATUS")) {
					
					result = "OK";
					out.println(result);


				} else
				if(command.firstElement().equals("REGISTER") && command.size() > 7) {

					result = register(command.get(1),command.get(2),command.get(3),command.get(4),command.get(5),command.get(6),command.get(7));
					out.println(result);


				}
				else 
					if(command.firstElement().equals("LOGIN") && command.size() > 2) {

						result = login(command.get(1),command.get(2));
						out.println(result);
					}else 
						if(command.firstElement().equals("FORCELOGIN") && command.size() > 2) {

							result = forcelogin(command.get(1),command.get(2));
							out.println(result);
						}
						else
							if(command.firstElement().equals("CHANGEPERSONAL") && command.size() > 8) {

								result = changePersonal(command.get(1),command.get(2),command.get(3),command.get(4),command.get(5),command.get(6), command.get(7),command.get(8));
								out.println(result);
							}
							else
								if(command.firstElement().equals("GETPERSONAL") && command.size() > 1) {

									result = getPersonal(command.get(1));
									out.println(result);
								}
								else
									if(command.firstElement().equals("GETUSERS") && command.size() > 0) {

//										System.out.println("GETUSERS");
										result = getusers();
										out.println(result);
									}
									else
										if(command.firstElement().equals("GETNEWS") && command.size() > 0) {

//											System.out.println("GETUSERS");
											result = getNews();
											out.println(result);
										}
										else
										if(command.firstElement().equals("GETFRIENDS") && command.size() > 1) {

//											System.out.println("GETFRIENDS");
//											String u = in.readLine();
											result = getfriends(command.get(1));
											out.println(result);
										}
										else
											if(command.firstElement().equals("UPDATEPOSITION") && command.size() > 2) {

//												System.out.println("UPDATEPOSITION");
//												String u = in.readLine();
//												String p = in.readLine();
												result = updateposition(command.get(1),command.get(2));
												out.println(result);
											}
											else
												if(command.firstElement().equals("ADDFRIEND") && command.size() > 2) {
//													System.out.println("ADDFRIEND");
//													String u = in.readLine();
//													String p = in.readLine();
													result = addfriend(command.get(1),command.get(2));
													out.println(result);
												}
												else
													if(command.firstElement().equals("PENDINGFRIENDS") && command.size() > 1) {
//														System.out.println("PENDINGFRIENDS");
//														String p = in.readLine();
														result = pendingfriends(command.get(1));
														out.println(result);
													}
													else
														if(command.firstElement().equals("ACCEPTFRIEND") && command.size() > 2) {
//															System.out.println("ACCEPTFRIEND");
//															String u = in.readLine();
//															String p = in.readLine();
															result = acceptfriend(command.get(1),command.get(2));
															out.println(result);
														}
														else
															if(command.firstElement().equals("DENYFRIEND") && command.size() > 2) {
//																System.out.println("DENYFRIEND");
//																String u = in.readLine();
//																String p = in.readLine();
																result = denyfriend(command.get(1),command.get(2));
																out.println(result);
															}
															else
																if(command.firstElement().equals("GETUSERDATA") && command.size() > 2) {
//																	System.out.println("GETUSERDATA");
//																	String u = in.readLine();
//																	String f = in.readLine();
																	result = getuserdata(command.get(1),command.get(2));
																	out.println(result);
																}
																else
																	if(command.firstElement().equals("CHECKPOSITION") && command.size() > 2) {
//																		System.out.println("CHECKPOSITION");
//																		String u = in.readLine();
																		result = getposition(command.get(1),command.get(2));
																		out.println(result);
																	}
																	else
																		if(command.firstElement().equals("SETPREFERRED") && command.size() > 2) {
//																			System.out.println("SETPREFERRED");
//																			String u = in.readLine();
//																			String pos = in.readLine();
																			result = setpreferred(command.get(1),command.get(2));
																			out.println(result);
																		}
																		else
																			if(command.firstElement().equals("GETPREFERRED") && command.size() > 2) {
//																				System.out.println("GETPREFERRED");
//																				String u = in.readLine();
																				result = getpreferred(command.get(1),command.get(2));
																				out.println(result);
																			} 
																			else
																				if (command.firstElement().equals("QUIT")){
																					System.out.println("Closing thread: user "+username+" sent QUIT command.");
																					quit();
																				}
				System.out.println("Returning: "+result);
				for ( ; size > 0; size--){
					command.remove(0);
				}
				command.remove(0);


//				out.println(result);
//				System.out.println("Ritorno al client: "+result);
//				result = "";

			}

			/*
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
			 */
		} catch (Exception e) {
			
		}


	}
	
	/**
	 * Ferma il server.
	 *
	 */
	public void quit(){
		try {
			run = false;
			clientSocket.close();
		} catch (IOException e) {

		}
	}

	/**
	 * Registra un utente
	 * 
	 * @param username lo username
	 * @param password la password
	 * @param mobile il numero di cellulare
	 * @param home il numero di casa
	 * @param work il numero del lavoro
	 * @param mail l'indirizzo email
	 * @param im il contatto di istant messenger
	 * 
	 * @return Una stringa contenente OK se ha avuto successo ERROR altrimenti
	 */
	public  String register(String username,String password,String mobile,String home,String work,String mail,String im) {

		if (!logged){
			u = new User(username,password,mobile,home,work,mail,im, "@0.0,0.0", "MOBILE");
			if (um.addUser(u)){
				return login(username,password);
			}
		}
		return "Register ERROR.";
	}	

	/**
	 * Effettua il login di un utente
	 * 
	 * @param uname username
	 * @param pwd password
	 * 
	 * @return Una stringa contenente OK se ha avuto successo ERROR altrimenti
	 */
	public  String login(String uname, String pwd) {
		User u;
		if (!logged){
			if ((u =um.getUser(uname)) != null){
				if (u.getPwd().equals(pwd)){
					logged = true;
					if(! um.unameConnected(uname)) {
						um.setConnected(uname, this);
						username = uname;
						return "Login OK.";
					}
				}
			}
		}
		return "Login ERROR.";
	}

	/**
	 * Forza il login. Se e' gia' presente un altro utente connesso con il nostro nick e la nostra password e' esatta lo disconnette
	 * 
	 * @param uname lo username
	 * @param pwd la password
	 * 
	 * @return Una stringa contenente OK se ha avuto successo ERROR altrimenti
	 */
	public  String forcelogin(String uname, String pwd) {
		User u;
		if (!logged){
			if ((u =um.getUser(uname)) != null){
				if (u.getPwd().equals(pwd)){
					logged = true;
					if(! um.unameConnected(uname)) {
						um.setConnected(uname, this);
					} else {
						um.removeUnameConnected(uname);
						um.setConnected(uname, this);
					}
					username = uname;
					return "Login OK.";
				}
			}
		}
		return "Login ERROR.";
		
	}

	/**
	 * Ritorna la lista degli utenti registrati al sistema separati da $
	 * 
	 * @return La lista degli utenti registrati al sistema separati da $ oppure una stringa vuota
	 */
	public  String getusers() {
		String ret = "";
		if (logged){
			for (String s : um.getUsers()){
				ret += s+"$";
			}
		}
		return ret;
	}


	/**
	 * Ottiene la lista dei nostri amici se il nick indicato e' quello associato a questo thread
	 * 
	 * @param uname il nostro nick
	 * 
	 * @return la lista degli amici separati da $
	 */
	public  String getfriends(String uname) {
		String ret = "";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			if (u!= null){
				for(String s : u.getFriends()){
					ret += s+"$";
				}
			}
		}
		return ret;
	}


	/**
	 * Aggiorna la propria posizione
	 * 
	 * @param uname l'username dell'utente
	 * @param position la posizione da aggiornare nel formato @latitudine,longitudine
	 * @return una stringa contenente OK se ha avuto successo o contente ERROR altrimenti
	 */
	public  String updateposition(String uname, String position){

		String ret = "Update ERROR";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			if (u!= null){
				u.setGeo(position);
				um.commit();
				ret = "Update OK.";
			}
		}
		return ret;
	}
	
	/**
	 * Cerca di aggiungere un amico alla propria lista di amici inserendo uname nella lista dei pendingfriends di friend
	 * 
	 * @param uname il nostro username
	 * @param friend l'username di colui al quale chiediamo di essere amici
	 * 
	 * @return Una stringa contenente OK se ha avuto successo ERROR altrimenti
	 */
	public  String addfriend(String uname,String friend) {

		String ret = "Friend not added. ERROR.";
		if (logged && uname.equals(username)){
			User u = um.getUser(friend);
			if (u!= null){
				u.addPendings(uname);
				um.commit();
				ret = "Friend added. OK.";
			}
		}
		return ret;
	}
	
	/**
	 * Ottiene la lista dei propri pendingfriends
	 * 
	 * @param uname l'username di chi effettua la richiesta
	 * 
	 * @return un elenco di utenti separati da $
	 */
	public  String pendingfriends(String uname) {
		String ret = "";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			if (u!= null){
				for(String s : u.getPendings()){
					ret += s+"$";
				}
			}
		}
		return ret;

	}

	/**
	 * Accetta un pendingfriend come amico
	 * 
	 * @param uname l'username di chi effettua l'operazione
	 * @param friend il nick dell'amico da accettare
	 * 
	 * @return Una stringa contenente OK se ha avuto successo ERROR altrimenti
	 */
	public  String acceptfriend(String uname,String friend) {

		String ret = "Friend not accepted. ERROR.";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			if (u!= null){
				u.addFriend(friend);
				u.removePenginds(friend);
			}
			u = um.getUser(friend);
			if (u!= null){
				u.addFriend(uname);
				um.commit();
				ret = "Friend added. OK.";
			}
		}
		return ret;
	}
	
	/**
	 * Rimuove un amico dalla propria lista di pending friends di fatto rifiutando la sua richiesta
	 * 
	 * @param uname l'username di chi effettua l'operazione
	 * @param friend l'amico da rifiutare
	 * 
	 * @return Una stringa contenente OK se ha avuto successo ERROR altrimenti
	 */
	public  String denyfriend(String uname,String friend) {

		String ret = "Friend not accepted. ERROR.";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			if (u!= null){
				u.removePenginds(friend);
				um.commit();
				ret = "Friend removed from pendings. OK.";
			}
		}
		return ret;
	}

	/**
	 * Ottiene le informazioni di un amico
	 * 
	 * @param uname l'username di chi effettua la richiesta
	 * @param friend l'amico di cui vogliamo avere le informazioni
	 * 
	 * @return una stringa nella forma: username$posizione_geografica$numero_cellulare$numero_lavoro$numero_casa$indirizzo_mail$istant_messenger
	 */
	public  String getuserdata(String uname, String friend) {

		String ret = "";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			User f = um.getUser(friend);
			if (u!= null && f != null){
				if (u.getFriends().contains(friend) && f.getFriends().contains(uname)){
					ret = f.getUserInfo();
				}
			}
		}
		return ret;
	}
	
	/**
	 * Ottiene la posizione di un amico
	 * 
	 * @param uname l'username di chi effettua la richiesta
	 * @param friend l'amico di cui si vuole sapere la posizione
	 * 
	 * @return la posizione formattata come @latitudine,longitudine
	 */
	public  String getposition(String uname, String friend) {
		String ret = "Impossible return position ERROR.";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			User f = um.getUser(friend);
			if (u!= null && f != null){
				if (u.getFriends().contains(friend) && f.getFriends().contains(uname)){
					ret =f.getGeo();
				}
			}
		}
		return ret;
	}
	
	/**
	 * Imposta il proprio modo preferito per essere contattati a pref
	 * 
	 * @param uname l'username di chi effettua l'operazione
	 * @param pref una stringa che deve essere solo MOBILE, HOME, WORK, MAIL o IM a seconda del caso
	 * 
	 * @return Una stringa contenente OK se ha avuto successo ERROR altrimenti
	 */
	public  String setpreferred(String uname, String pref) {
		String ret = "Impossible to set prederred mode. ERROR.";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			if (u!= null){
				u.setPreferredMode(pref);
				um.commit();
				ret = "Preferred mode set to: "+pref+" OK.";
			}
		}
		return ret;
	}
	
	/**
	 * Ottiene il modo con cui un amico preferisce essere contattato
	 * 
	 * @param uname l'utente che effettua la richiesta
	 * @param friend l'amico di cui si vuole sapere come preferisce essere contattato
	 * 
	 * @return HOME, MOBILE, WORK, MAIL o IM.
	 */
	public  String getpreferred(String uname, String friend) {
		String ret= "Impossible return preferred mode. ERROR";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			User f = um.getUser(friend);
			if (u!= null && f != null){
				if (u.getFriends().contains(friend) && f.getFriends().contains(uname)){
					ret =f.getPreferredMode();
				}
			}
		}
		return ret;
	}

	/**
	 * Ottiene le informazioni personali dell'utente
	 * 
	 * @param uname l'utente che effettua la richiesta
	 * 
	 * @return una stringa nella forma: username$numero_cellulare$numero_lavoro$numero_casa$indirizzo_mail$istant_messenger
	 */
	public String getPersonal(String uname){
		String ret= "Impossible return personal data. ERROR";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			if (u!= null){
				ret = u.getPersonal();
			}
		}

		return ret;
	}

	/**
	 * Cambia le informazioni personali di un utente ad eccezione dello username.
	 * 
	 * @param uname l'utente che ha effettuato la richiesta
	 * @param oldPwd la vecchia password
	 * @param pwd la nuova password
	 * @param mobile il numero di cellulare
	 * @param work il numero del lavoro
	 * @param home il numero di casa
	 * @param mail l'indirizzo mail 
	 * @param im l'istant messenger
	 * 
	 * @return Una stringa contenente OK se ha avuto successo ERROR altrimenti
	 */
	public String changePersonal(String uname, String oldPwd, String pwd,String  mobile,String work,String home,String mail,String im){
		String ret= "Impossible to change personal data. ERROR";
		if (logged && uname.equals(username)){
			User u = um.getUser(uname);
			if (u!= null){
				if (u.getPwd().equals(oldPwd)){
					u.setPwd(pwd);
					u.setMobile(mobile);
					u.setWork(work);
					u.setHome(home);
					u.setMail(mail);
					u.setIm(im);
					um.commit();
					ret = "OK";
				}
			}
		}

		return ret;
	}
	
	public String getNews() {
		
		if(logged) {
			return um.getNews();
		} else {
			return "Impossibile ritornare la lista delle News. ERROR";
		}
	}

}	