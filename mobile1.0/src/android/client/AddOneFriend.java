package android.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity che implementa la schermata per l'inserimento di un amico conoscendo il suo nome. Viene richiamata tramite una ADD_FRIEND_ACTION
 * @author  Nicolas Tagliani
 * @author  Vincenzo Frascino
 */
public class AddOneFriend extends Activity implements OnClickListener, ServiceConnection {

	/**
	 * Intent a cui è sensibile questa activity
	 */
	public static final String ADD_FRIEND_ACTION =
		"android.client.action.ADD_A_FRIEND";

	private Button ok;
	private EditText username;
	private boolean ready = false;
	/**
	 * @uml.property  name="service"
	 * @uml.associationEnd  
	 */
	private ServiceInterface service = null;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.add_one_friend);
		ok = (Button)findViewById(R.id.add_friend_ok);
		username = (EditText)findViewById(R.id.add_friend_username);
		ok.setOnClickListener(this);
		bindService(new Intent("android.client.MY_SERVICE"),this,0);

	}

	public void onClick(View arg0) {
		arg0.setSelected(true);
		if (ready){
			if (arg0 == ok){
				try {
					String u = username.getText().toString();
					if (service.addFriend(u)){
						AlertD.show(this, "OK", 0, "User "+u+" added to your pending list", "OK",false);
						username.setText("");
					}
					else {
						AlertD.show(this, "Error", 0, "Impossible to add user "+u, "OK",false);
						
					}
				}catch (RemoteException e){

				}
			}


		}

	}

	public void onServiceConnected(ComponentName name, IBinder service) {
		this.service = ServiceInterface.Stub.asInterface(service);
		ready = true;

	}

	public void onServiceDisconnected(ComponentName arg0) {
		// TODO Auto-generated method stub

	}

}
