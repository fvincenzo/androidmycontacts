package android.client;

import java.util.HashMap;
import java.util.Map;
import android.os.DeadObjectException;
import android.util.Log;

/**
 * Questo thread si occupa di effettuare i controlli ciclici sugli amici e sulle eventuali nortifiche da parte del server
 * @author  Nicolas Tagliani
 * @author  Vincenzo Frascino
 */
public class CyclicChecks extends Thread {

	/**
	 * @uml.property  name="service"
	 * @uml.associationEnd  
	 */
	private ServiceInterface.Stub service =  null;
	private boolean run = true;
	private int NOTIFY = 1;
	private int NORMAL = 0;
	private int status = 0;
	private Map<String, String> localMap = new HashMap<String, String>();

	
	/**
	 * Costruttore del thread
	 * 
	 * @param service Il remote service che implementa tutte le funzioni di comunicazione con il server
	 */
	public CyclicChecks(ServiceInterface.Stub service) {
		super();
		this.service = service;
	}

	/**
	 * Imposta lo stato di esecuzione "NORMAL"
	 * Questo metodo viene richiamato dal servizio di backgroud una volta che le notifiche sono state visualizzate e rimosse
	 *
	 */
	public void setNormal(){
		status = NORMAL;
	}

	
	@Override
	public void run(){
		while (run){
			try {
				Thread.sleep(1000*Settings.CLYCLIC_UPDATE_RATE);
				//Controlla i pending friends
				if (status == NORMAL){
					if ( service.pendingFriends().size() > 0 ){
						service.notifyPendings();
						status = NOTIFY;
					}
				}

				for (String friend : service.getFriends()){

					String pref = service.getpreferred(friend);
					//L'utente è gia' presente nella lista locale degli amici
					if (localMap.containsKey(friend)){
						
						if (!pref.equals(localMap.get(friend))){
							//se e' cambiato il modo di essere contattati richiedo anche gli altri dati
							service.insertContact(friend);
							String s = service.checkposition(friend);
							service.setPosition(friend, s);
							setPreferred(friend, pref);
						}
					}
					else {
//						L'utente non è presente nella lista quindi lo inserisco e mi scairco i sui dati per sicurezza
						localMap.put(friend, pref);
						service.insertContact(friend);
						setPreferred(friend, pref);
						String s = service.checkposition(friend);
						service.setPosition(friend, s);

					}
				}
			}
			catch (InterruptedException e) {

			}
			catch (DeadObjectException e){

			}
		}
	}


	/**
	 * Motodo per fermare il thread
	 *
	 */
	public void quit(){
		run = false;
	}


	private void setPreferred(String friend, String pref){
		try {
			if (pref.equals("HOME")){
				Log.v("CheckPreferredMode", "Devo settare HOME ---");
				service.setHome(friend);
			} else
				if (pref.equals("MOBILE")){
					Log.v("CheckPreferredMode", "Devo settare MOBILE ---");
					service.setMobile(friend);
				} else
					if (pref.equals("WORK")){
						Log.v("CheckPreferredMode", "Devo settare WORK ---");
						service.setWork(friend);
					}else
						if (pref.equals("MAIL")){
							Log.v("CheckPreferredMode", "Devo settare MAIL ---");
							service.setMail(friend);
						}else
							if (pref.equals("IM")){
								Log.v("CheckPreferredMode", "Devo settare IM ---");
								service.setIm(friend);
							}
		}catch (DeadObjectException e){

		}
	}
}
