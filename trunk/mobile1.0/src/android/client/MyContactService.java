package android.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.Contacts;
import android.provider.Contacts.ContactMethods;
import android.util.Log;
import android.widget.Toast;
import android.client.R;
import android.database.Cursor;

/**
 * @author  gabrielknight
 */
public class MyContactService extends Service {

	private Notification not;
	private NotificationManager mNM;



	/**
	 * @uml.property  name="mBinder"
	 * @uml.associationEnd  
	 */
	private final ServiceInterface.Stub mBinder = new ServiceInterface.Stub() {

		private PrintWriter out;
		private BufferedReader in;
		private Socket socket;
		private String username = "";
		private CyclicChecks cc = new CyclicChecks(this);
		private String password = "";
		protected LocationManager myLocationManager = null;
		private GPSThread gps = null;
		private DBHelper db = null;

		@Override
		public boolean register(String uname, String pwd, String mobile,
				String home, String work, String mail, String im) {

			this.myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			this.db = new DBHelper(getContext());
			this.gps = new GPSThread(this,myLocationManager,db);

			try {
				in.readLine();
				in.readLine();
				in.readLine();
				out.println("REGISTER");
				out.println(uname);
				out.println(pwd);
				if (mobile == null)
					mobile = " ";
				out.println(mobile);
				if (home == null)
					home = " ";
				out.println(home);
				if (work == null)
					work = " ";
				out.println(work);
				if (mail == null)
					mail = " ";
				out.println(mail);
				if (im == null)
					im = " ";
				out.println(im);
				out.println("END");
				String ret;
				ret = in.readLine();
				if (ret.contains("OK")) {
					this.username = uname;
					this.password = pwd;
					if (!cc.isAlive()){
						cc.start();
					}
					if (!gps.isAlive()){
						gps.start();						
					}
					serviceConnectedNotification();
					return true;
				}
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		@Override
		public boolean login(String uname, String pwd) {

			this.myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			this.db = new DBHelper(getContext());
			this.gps = new GPSThread(this,myLocationManager,db);

			try {
				in.readLine();
				in.readLine();
				in.readLine();
				out.println("LOGIN");
				out.println(uname);
				out.println(pwd);
				out.println("END");
				String ret;
				ret = in.readLine();
				if (ret.contains("OK")) {
					this.username = uname;
					this.password = pwd;
					if (!cc.isAlive()){
						cc.start();
					} if (!gps.isAlive()){
						gps.start();						
					}
					serviceConnectedNotification();
					return true;
				}
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		@Override
		public boolean forcelogin(String uname, String pwd) {

			this.myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			this.db = new DBHelper(getContext());
			this.gps = new GPSThread(this,myLocationManager,db);

			try {
				in.readLine();
				in.readLine();
				in.readLine();
				out.println("FORCELOGIN");
				out.println(uname);
				out.println(pwd);
				out.println("END");
				String ret;
				ret = in.readLine();
				if (ret.contains("OK")) {
					this.username = uname;
					this.password = pwd;
					if (!cc.isAlive()){
						cc.start();
					}
					if (!gps.isAlive()){
						gps.start();						
					}
					serviceConnectedNotification();
					return true;
				}
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		@Override
		public boolean changepersonal(String username, String oldPwd, String newPwd, String mobile,
				String home, String work, String mail, String im) {
			try {
				out.println("CHANGEPERSONAL");
				out.println(username);
				if (oldPwd == null){
					out.println(password);
					out.println(password);
				}
				else {
					out.println(oldPwd);
					out.println(newPwd);
				}
				out.println(mobile);
				out.println(home);
				out.println(work);
				out.println(mail);
				out.println(im);
				out.println("END");
				String ret;
				ret = in.readLine();
				if (ret.contains("OK")) {
					return true;
				}
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		@Override
		public boolean setpreferred(String mode) {
			try {
				out.println("SETPREFERRED");
				out.println(username);
				out.println(mode);
				out.println("END");
				String ret;
				ret = in.readLine();
				if (ret.contains("OK")) {
					return true;
				}
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		@Override
		public String getpreferred(String friend) {
			try {
				out.println("GETPREFERRED");
				out.println(username);
				out.println(friend);
				out.println("END");
				String ret;
				ret = in.readLine();
				return ret;
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		public boolean addFriend(String friendName) {
			try {
				if (username != "") {
					out.println("ADDFRIEND");
					out.println(username);
					out.println(friendName);
					out.println("END");
					String tmp = in.readLine();
					if (tmp.contains("OK")) {
						return true;
					}
				}
				return false;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public void denyFriend(String friendName) {
			try {
				if (username != "") {
					out.println("DENYFRIEND");
					out.println(username);
					out.println(friendName);
					out.println("END");
					in.readLine();
				}
			} catch (Exception e) {
			}
		}

		@Override
		public void acceptFriend(String friendName) {
			try {
				if (username != "") {
					out.println("ACCEPTFRIEND");
					out.println(username);
					out.println(friendName);
					out.println("END");
					in.readLine();
				}
			} catch (Exception e) {
			}
		}

		@Override
		public List<String> getFriends() {
			List<String> retV = new LinkedList<String>();
			if (username != "") {
				try {
					out.println("GETFRIENDS");
					out.println(username);
					out.println("END");
					String ret;
					ret = in.readLine();
					StringTokenizer tok = new StringTokenizer(ret, "$");
					while (tok.hasMoreTokens()) {
						retV.add(tok.nextToken());
					}
					return retV;
				} catch (IOException e) {
					return retV;
				}
			}
			return retV;
		}

		@Override
		public List<String> getUserDetails(String friend) {
			try {
				if (username != "") {
					out.println("GETUSERDATA");
					out.println(username);
					out.println(friend);
					out.println("END");
					String ret;
					ret = in.readLine();
					if (!ret.contains("ERROR")) {
						return new ContactUser(ret).toList();
					}
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public List<String> getUsers() {
			List<String> retV = new LinkedList<String>();
			if (username != "") {
				try {
					out.println("GETUSERS");
					out.println("END");
					String ret;
					ret = in.readLine();
					StringTokenizer tok = new StringTokenizer(ret, "$");
					while (tok.hasMoreTokens()) {
						String toAdd = tok.nextToken();
						if (!toAdd.equals(username))
							retV.add(toAdd);
					}
					return retV;
				} catch (IOException e) {
					return retV;
				}
			}
			return retV;
		}

		@Override
		public List<String> getPersonal(){
			List<String> retV = new LinkedList<String>();
			if (username != "") {
				try {
					out.println("GETPERSONAL");
					out.println(username);
					out.println("END");
					String ret;
					ret = in.readLine();
					StringTokenizer tok = new StringTokenizer(ret, "$");
					while (tok.hasMoreTokens()) {
						retV.add(tok.nextToken());

					}
					return retV;
				} catch (IOException e) {
					return retV;
				}
			}
			return retV;

		}

		@Override
		public List<String> pendingFriends() {
			List<String> retV = new LinkedList<String>();
			if (username != "") {
				try {
					out.println("PENDINGFRIENDS");
					out.println(username);
					out.println("END");
					String ret;
					ret = in.readLine();
					StringTokenizer tok = new StringTokenizer(ret, "$");
					while (tok.hasMoreTokens()) {
						retV.add(tok.nextToken());
					}
					return retV;
				} catch (IOException e) {
					return retV;
				} 
			}
			return retV;
		}

		@Override
		public boolean updatePosition(double x_position, double y_position) {
			if (username != "") {
				try {
					out.println("UPDATEPOSITION");
					out.println(username);
					out.println("@" + x_position + "," + y_position);
					out.println("END");
					String ret;
					ret = in.readLine();
					if (ret.contains("OK")) {
						return true;
					}
					return false;
				} catch (IOException e) {
					return false;
				}
			}
			return false;
		}

		@Override
		public boolean connect(String server) {
			try {
				socket = new Socket(server, 4444);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket
						.getInputStream()));
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public boolean isRunning() {
			if (!username.equals("") && socket.isConnected())
				return true;
			return false;
		}


		@Override
		public boolean insertContact(String friend) throws DeadObjectException {

			List<String> info = getUserDetails(friend);
			String name_s = info.get(0);
			String mobile_s = info.get(1);
			String home_s = info.get(2);
			String work_s = info.get(3);
			String email_s = info.get(4);
			String im_s = info.get(5);
			String pos_s = info.get(6);


			Integer person_id = null;

			Cursor personId = getContentResolver().query(Contacts.People.CONTENT_URI, new String[] { BaseColumns._ID },  "name='"+name_s+"'", null, null);
			while (personId.moveToNext()){

				Cursor curPersonId = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, new String[] { Contacts.ContactMethods.PERSON_ID },  "person="+personId.getInt(personId.getColumnIndex(BaseColumns._ID))+" AND kind="+500, null, null);
				if (curPersonId.moveToNext()) {
					person_id = curPersonId.getInt(curPersonId.getColumnIndex(Contacts.ContactMethods.PERSON_ID));
					break;
				}
			}

			if (person_id != null ){

				String projection[] = {
						BaseColumns._ID,
						Contacts.Phones.NUMBER
				};
				Cursor c = getContentResolver().query(Contacts.Phones.CONTENT_URI, projection, "type="+Contacts.Phones.MOBILE_TYPE+" AND person="+person_id, null, null);
				if (c.moveToNext()){
					c.updateString(c.getColumnIndex(Contacts.Phones.NUMBER), mobile_s);
					c.commitUpdates();
				}

				c = getContentResolver().query(Contacts.Phones.CONTENT_URI, projection, "type="+Contacts.Phones.HOME_TYPE+" AND person="+person_id, null, null);
				if (c.moveToNext()){
					c.updateString(c.getColumnIndex(Contacts.Phones.NUMBER), home_s);
					c.commitUpdates();
				}

				c = getContentResolver().query(Contacts.Phones.CONTENT_URI, projection, "type="+Contacts.Phones.WORK_TYPE+" AND person="+person_id, null, null);
				if (c.moveToNext()){
					c.updateString(c.getColumnIndex(Contacts.Phones.NUMBER), work_s);
					c.commitUpdates();
				}

				String projection2[] = {
						BaseColumns._ID,
						Contacts.ContactMethods.DATA
				};
				c = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection2, "label='Mail' AND person="+person_id, null, null);
				if (c.moveToNext()){
					c.updateString(c.getColumnIndex(Contacts.ContactMethods.DATA), email_s);
					c.commitUpdates();
				}

				c = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection2, "label='Messenger' AND person="+person_id, null, null);
				if (c.moveToNext()){
					c.updateString(c.getColumnIndex(Contacts.ContactMethods.DATA), im_s);
					c.commitUpdates();
				}

				c = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection2, "kind="+ContactMethods.POSTAL_KIND+" AND person="+person_id, null, null);
				if (c.moveToNext()){
					c.updateString(c.getColumnIndex(Contacts.ContactMethods.DATA), pos_s);
					c.commitUpdates();
				}
				return true;
			}
			else {

				ContentValues person = new ContentValues();
				person.put(Contacts.People.NAME, name_s);
				Uri newPerson = getContentResolver().insert(
						Contacts.People.CONTENT_URI, person);
				if (newPerson != null) {
					List<String> pathList = newPerson.getPathSegments();
					String pathLeaf = pathList.get(pathList.size() - 1);
					ContentValues number = new ContentValues();
					number.put(Contacts.Phones.PERSON_ID, pathLeaf);
					number.put(Contacts.Phones.NUMBER, mobile_s);
					number.put(Contacts.Phones.TYPE, Contacts.Phones.MOBILE_TYPE);
					Uri phoneUpdate = getContentResolver().insert(
							Contacts.Phones.CONTENT_URI, number);
					if (phoneUpdate == null) {
						return false;
					}
					number = new ContentValues();
					number.put(Contacts.Phones.PERSON_ID, pathLeaf);
					number.put(Contacts.Phones.NUMBER, work_s);
					number.put(Contacts.Phones.TYPE, Contacts.Phones.WORK_TYPE);
					phoneUpdate = getContentResolver().insert(
							Contacts.Phones.CONTENT_URI, number);
					if (phoneUpdate == null) {
						return false;
					}
					number = new ContentValues();
					number.put(Contacts.Phones.PERSON_ID, pathLeaf);
					number.put(Contacts.Phones.NUMBER, home_s);
					number.put(Contacts.Phones.TYPE, Contacts.Phones.HOME_TYPE);
					phoneUpdate = getContentResolver().insert(
							Contacts.Phones.CONTENT_URI, number);
					if (phoneUpdate == null) {
						return false;
					}
					ContentValues email = new ContentValues();
					email.put(Contacts.ContactMethods.PERSON_ID, pathLeaf);
					email.put(Contacts.ContactMethods.KIND,
							Contacts.ContactMethods.EMAIL_KIND);
					email.put(Contacts.ContactMethods.DATA, email_s);
					email.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.EMAIL_KIND_OTHER_TYPE);
					email.put(Contacts.ContactMethods.LABEL, "Mail");
					Uri emailUpdate = getContentResolver().insert(
							Uri.withAppendedPath(newPerson,
									Contacts.ContactMethods.CONTENT_URI.getPath()
									.substring(1)), email);
					if (emailUpdate == null) {
						return false;
					}
					ContentValues im = new ContentValues();
					im.put(Contacts.ContactMethods.PERSON_ID, pathLeaf);
					im.put(Contacts.ContactMethods.KIND,
							Contacts.ContactMethods.EMAIL_KIND);
					im.put(Contacts.ContactMethods.DATA, im_s);
					im.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.EMAIL_KIND_OTHER_TYPE);
					im.put(Contacts.ContactMethods.LABEL, "Messenger");
					Uri imUpdate = getContentResolver().insert(
							Uri.withAppendedPath(newPerson,
									Contacts.ContactMethods.CONTENT_URI.getPath()
									.substring(1)), im);
					if (imUpdate == null) {
						return false;
					}
					ContentValues geo = new ContentValues();
					geo.put(Contacts.ContactMethods.PERSON_ID, pathLeaf);
					geo.put(Contacts.ContactMethods.KIND,
							Contacts.ContactMethods.POSTAL_KIND);
					geo.put(Contacts.ContactMethods.DATA, pos_s);
					geo.put(Contacts.ContactMethods.TYPE,
							Contacts.ContactMethods.POSTAL_KIND_OTHER_TYPE);
					geo.put(Contacts.ContactMethods.LABEL, "Current Position");
					Uri geoUpdate = getContentResolver().insert(
							Uri.withAppendedPath(newPerson,
									Contacts.ContactMethods.CONTENT_URI.getPath()
									.substring(1)), geo);
					if (geoUpdate == null) {
						return false;
					}
					ContentValues myContact = new ContentValues();
					myContact.put(Contacts.ContactMethods.KIND, 500);
					Uri flag = getContentResolver().insert(
							Uri.withAppendedPath(newPerson,
									Contacts.ContactMethods.CONTENT_URI.getPath()
									.substring(1)), myContact);
					if (flag == null) {
						return false;
					}

				}
				return true;
			}
		}



		@Override
		public boolean setHome(String contact) throws DeadObjectException {
			Log.v("MyContactService", "Sono in setHome");
			String[] projection = new String[] {
					android.provider.BaseColumns._ID,
					android.provider.Contacts.People.NOTES,
					android.provider.Contacts.People.PREFERRED_PHONE_ID,
					android.provider.Contacts.People.PREFERRED_EMAIL_ID
			};
			Cursor user = getContentResolver().query(Contacts.People.CONTENT_URI, projection, "name='"+contact+"'", null, null);
			if (user.moveToNext()){
				String[] projection2 = new String[] {
						android.provider.BaseColumns._ID
				};
				int person_id = user.getInt(user.getColumnIndex(android.provider.BaseColumns._ID));
				Cursor tel = getContentResolver().query(Contacts.Phones.CONTENT_URI, projection2, "person='"+person_id+"' AND type='"+Contacts.Phones.HOME_TYPE+"'", null, null);
				if (tel.moveToNext()){
					user.updateToNull(user.getColumnIndex(Contacts.People.NOTES));
					user.updateInt(user.getColumnIndex(Contacts.People.PREFERRED_PHONE_ID), tel.getInt(tel.getColumnIndex(BaseColumns._ID)));
					return user.commitUpdates();
				}
			}
			return false;

		}

		@Override
		public boolean setMobile(String contact) throws DeadObjectException {
			Log.v("MyContactService", "Sono in setMobile");
			String[] projection = new String[] {
					android.provider.BaseColumns._ID,
					android.provider.Contacts.People.NOTES,
					android.provider.Contacts.People.PREFERRED_PHONE_ID,
					android.provider.Contacts.People.PREFERRED_EMAIL_ID
			};
			Cursor user = getContentResolver().query(Contacts.People.CONTENT_URI, projection, "name='"+contact+"'", null, null);
			if (user.moveToNext()){
				String[] projection2 = new String[] {
						android.provider.BaseColumns._ID
				};
				int person_id = user.getInt(user.getColumnIndex(android.provider.BaseColumns._ID));
				Cursor tel = getContentResolver().query(Contacts.Phones.CONTENT_URI, projection2, "person='"+person_id+"' AND type='"+Contacts.Phones.MOBILE_TYPE+"'", null, null);
				if (tel.moveToNext()){
					user.updateToNull(user.getColumnIndex(Contacts.People.NOTES));
					user.updateInt(user.getColumnIndex(Contacts.People.PREFERRED_PHONE_ID), tel.getInt(tel.getColumnIndex(BaseColumns._ID)));
					return user.commitUpdates();
				}
			}
			return false;
		}

		@Override
		public boolean setWork(String contact) throws DeadObjectException {
			Log.v("MyContactService", "Sono in setWork");
			String[] projection = new String[] {
					android.provider.BaseColumns._ID,
					android.provider.Contacts.People.NOTES,
					android.provider.Contacts.People.PREFERRED_PHONE_ID,
					android.provider.Contacts.People.PREFERRED_EMAIL_ID
			};
			Cursor user = getContentResolver().query(Contacts.People.CONTENT_URI, projection, "name='"+contact+"'", null, null);
			if (user.moveToNext()){
				String[] projection2 = new String[] {
						android.provider.BaseColumns._ID
				};
				int person_id = user.getInt(user.getColumnIndex(android.provider.BaseColumns._ID));
				Cursor tel = getContentResolver().query(Contacts.Phones.CONTENT_URI, projection2, "person='"+person_id+"' AND type='"+Contacts.Phones.WORK_TYPE+"'", null, null);
				if (tel.moveToNext()){
					user.updateToNull(user.getColumnIndex(Contacts.People.NOTES));
					user.updateInt(user.getColumnIndex(Contacts.People.PREFERRED_PHONE_ID), tel.getInt(tel.getColumnIndex(BaseColumns._ID)));
					return user.commitUpdates();
				}
			}
			return false;
		}

		public boolean setIm(String contact) throws DeadObjectException {
			String[] projection = new String[] {
					android.provider.BaseColumns._ID,
					android.provider.Contacts.People.NOTES,
					android.provider.Contacts.People.PREFERRED_PHONE_ID,
					android.provider.Contacts.People.PREFERRED_EMAIL_ID
			};
			Cursor user = getContentResolver().query(Contacts.People.CONTENT_URI, projection, "name='"+contact+"'", null, null);
			if (user.moveToNext()){
				String[] projection2 = new String[] {
						android.provider.BaseColumns._ID
				};
				int person_id = user.getInt(user.getColumnIndex(android.provider.BaseColumns._ID));
				Cursor mail = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection2, "person='"+person_id+"' AND label='Messenger'", null, null);
				if (mail.moveToNext()){
					user.updateInt(user.getColumnIndex(Contacts.People.PREFERRED_EMAIL_ID), mail.getInt(mail.getColumnIndex(BaseColumns._ID)));
					user.updateString(user.getColumnIndex(Contacts.People.NOTES), "Best contact with IM");

					user.updateToNull(user.getColumnIndex(Contacts.People.PREFERRED_PHONE_ID));

					return user.commitUpdates();
				}
			}
			return false;
		}

		public boolean setMail(String contact) throws DeadObjectException {
			String[] projection = new String[] {
					android.provider.BaseColumns._ID,
					android.provider.Contacts.People.NOTES,
					android.provider.Contacts.People.PREFERRED_PHONE_ID,
					android.provider.Contacts.People.PREFERRED_EMAIL_ID
			};
			Cursor user = getContentResolver().query(Contacts.People.CONTENT_URI, projection, "name='"+contact+"'", null, null);
			if (user.moveToNext()){
				String[] projection2 = new String[] {
						android.provider.BaseColumns._ID
				};
				int person_id = user.getInt(user.getColumnIndex(android.provider.BaseColumns._ID));
				Cursor mail = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection2, "person='"+person_id+"' AND label='Mail'", null, null);
				if (mail.moveToNext()){
					user.updateInt(user.getColumnIndex(Contacts.People.PREFERRED_EMAIL_ID), mail.getInt(mail.getColumnIndex(BaseColumns._ID)));
					user.updateString(user.getColumnIndex(Contacts.People.NOTES), "Best contact with email");

					user.updateToNull(user.getColumnIndex(Contacts.People.PREFERRED_PHONE_ID));

					return user.commitUpdates();
				}
			}
			return false;
		}

		public void notifyPendings() throws DeadObjectException {
			pendingFriendsNotification();

		}

		public void setNormalStatus() throws DeadObjectException {
			cc.setNormal();
			serviceWorkingNormally();

		}

		@Override
		public void stop(String message){
			cc.quit();
			gps.quit();
		}

		@Override
		public String checkposition(String friendName) throws DeadObjectException {
			try {
				if (username != "") {
					out.println("CHECKPOSITION");
					out.println(username);
					out.println(friendName);
					out.println("END");
					String position = in.readLine();
					return position;
				}
				return null;
			} catch (Exception e) {
				return null;
			}
		}

		public boolean setPosition(String contact, String position) throws DeadObjectException {
			String[] projection = new String[] {
					android.provider.BaseColumns._ID
			};
			Cursor user = getContentResolver().query(Contacts.People.CONTENT_URI, projection, "name='"+contact+"'", null, null);
			if (user.moveToNext()){
				String[] projection2 = new String[] {
						android.provider.BaseColumns._ID,
						android.provider.Contacts.ContactMethods.DATA
				};
				int person_id = user.getInt(user.getColumnIndex(android.provider.BaseColumns._ID));
				Cursor geo = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection2, "person='"+person_id+"' AND kind="+Contacts.ContactMethods.POSTAL_KIND, null, null);
				if (geo.moveToNext()){
					geo.updateString(geo.getColumnIndex(Contacts.ContactMethods.DATA), position);
					return geo.commitUpdates();
				}
			}
			return false;

		}
	};

	@Override
	protected void onCreate() {



		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		serviceStartedNotification();

	}

	@Override
	protected void onDestroy() {
		// Cancel the persistent notification.
		mNM.cancel(R.string.local_service_started);


		// Tell the user we stopped.
		try {
			mBinder.stop(null);
		} catch (DeadObjectException e) {

		}
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();


	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private Context getContext(){
		return this;
	}


	/**
	 * Show notifications while this service is running.
	 */
	private void serviceConnectedNotification(){

//		This is who should be launched if the user selects our notification.
		Intent contentIntent = new Intent(this, MainLoopActivity.class);

		// This is who should be launched if the user selects the app icon in the notification,
		// (in this case, we launch the same activity for both)
		Intent appIntent = new Intent(this, MainLoopActivity.class);

		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.local_service_connected);

		// we use a string id because it is a unique
		// number.  we use it later to cancel the
		// notification
		not = new Notification(
				this,                        // our context
				R.drawable.small_icon_active,      // the icon for the status bar
				text,                        // the text to display in the ticker
				System.currentTimeMillis(),  // the timestamp for the notification
				getText(R.string.app_name), // the title for the notification
				text,                        // the details to display in the notification
				contentIntent,               // the contentIntent (see above)
				R.drawable.my_contacts,  // the app icon
				getText(R.string.app_name), // the name of the app
				appIntent);                 // the appIntent (see above)

		mNM.notify(R.string.local_service_started, not);

	}

	private void serviceStartedNotification() {
		// This is who should be launched if the user selects our notification.
		Intent contentIntent = new Intent(this, MyContatsClient.class);

		// This is who should be launched if the user selects the app icon in the notification,
		// (in this case, we launch the same activity for both)
		Intent appIntent = new Intent(this, MyContatsClient.class);

		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.local_service_started);

		// we use a string id because it is a unique
		// number.  we use it later to cancel the
		// notification
		not = new Notification(
				this,                        // our context
				R.drawable.small_icon_inactive,      // the icon for the status bar
				text,                        // the text to display in the ticker
				System.currentTimeMillis(),  // the timestamp for the notification
				getText(R.string.app_name), // the title for the notification
				text,                        // the details to display in the notification
				contentIntent,               // the contentIntent (see above)
				R.drawable.my_contacts,  // the app icon
				getText(R.string.app_name), // the name of the app
				appIntent);                 // the appIntent (see above)

		mNM.notify(R.string.local_service_started, not);
	}

	private void pendingFriendsNotification() {
		// This is who should be launched if the user selects our notification.
		Intent contentIntent = new Intent(FriendsList.PENDING_ACTION, null);

		// This is who should be launched if the user selects the app icon in the notification,
		// (in this case, we launch the same activity for both)
		Intent appIntent = new Intent(FriendsList.PENDING_ACTION, null);

		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.local_service_pendings);

		// we use a string id because it is a unique
		// number.  we use it later to cancel the
		// notification
		not = new Notification(
				this,                        // our context
				R.drawable.small_icon_notification,      // the icon for the status bar
				text,                        // the text to display in the ticker
				System.currentTimeMillis(),  // the timestamp for the notification
				getText(R.string.app_name), // the title for the notification
				text,                        // the details to display in the notification
				contentIntent,               // the contentIntent (see above)
				R.drawable.my_contacts,  // the app icon
				getText(R.string.app_name), // the name of the app
				appIntent);                 // the appIntent (see above)

		mNM.notify(R.string.local_service_started, not);
	}

	private void serviceWorkingNormally() {
		// This is who should be launched if the user selects our notification.
		Intent contentIntent = new Intent(this, MainLoopActivity.class);

		// This is who should be launched if the user selects the app icon in the notification,
		// (in this case, we launch the same activity for both)
		Intent appIntent = new Intent(this, MainLoopActivity.class);

		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.local_service_running);

		// we use a string id because it is a unique
		// number.  we use it later to cancel the
		// notification
		not = new Notification(
				this,                        // our context
				R.drawable.small_icon_active,      // the icon for the status bar
				text,                        // the text to display in the ticker
				System.currentTimeMillis(),  // the timestamp for the notification
				getText(R.string.app_name), // the title for the notification
				text,                        // the details to display in the notification
				contentIntent,               // the contentIntent (see above)
				R.drawable.my_contacts,  // the app icon
				getText(R.string.app_name), // the name of the app
				appIntent);                 // the appIntent (see above)

		mNM.notify(R.string.local_service_started, not);
	}
}
