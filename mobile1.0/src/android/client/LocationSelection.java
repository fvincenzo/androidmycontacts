package android.client;


import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import org.xml.sax.SAXException;
import android.graphics.Point;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Questa activity mostra la nostra posizione sulla mappa, la posizione dei nostri amici e la posizione delle location inserite nel database Permette inoltre di modificare i parametri per le locations tramite un menu
 * @author  Nicolas Tagliani
 * @author  Vincenzo Frascino
 */
public class LocationSelection extends MapActivity implements OnClickListener{

	/**
	 * Intent a cui e' sensibile questa activity
	 */
	public static final String SELECT_LOCATION_ACTION =
		"android.client.action.SELECT_LOCATION";

	private final int SEARCH = Menu.FIRST;
	private final int CHOOSE = Menu.FIRST+1;
	private final int VIEW_LOCATIONS = Menu.FIRST+2;
	private final int CHOOSE_DEFAULT = Menu.FIRST+3;

	private MapView myMap = null;
	private Button select;
	private LinearLayout searchBar;
	private EditText search;
	private Button go;
	private GeoPoint lastSearch;

	/**
	 * @uml.property  name="db"
	 * @uml.associationEnd  
	 */
	private DBHelper db;
	protected List<Overlay> overlays = null; 
	private String lastAddress;
	
	@Override
	protected void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_layout);
		myMap = (MapView)findViewById(R.id.map);
		select = (Button)findViewById(R.id.map_select);
		searchBar = (LinearLayout)findViewById(R.id.search_bar);
		go = (Button)findViewById(R.id.go_search);
		search = (EditText)findViewById(R.id.search_text);

		go.setOnClickListener(this);
		select.setOnClickListener(this);
		db = new DBHelper(this);

		overlays = this.myMap.getOverlays();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
		overlays.add(myLocationOverlay); 
		myMap.invalidate();
	}

	public void onClick(View arg0) {

		if (arg0 == go){
			String text = search.getText().toString();
			if (text.length() > 0){
				gotoAddress(text);
				searchBar.setVisibility(View.INVISIBLE);
			}
		}
		if (arg0 == select){
			selectCurrentPosition();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SEARCH, 0, "Search").setIcon(R.drawable.search_icon);
		menu.add(0, CHOOSE, 0, "Choose here").setIcon(R.drawable.here_icon);
		menu.add(0, VIEW_LOCATIONS, 0,"View locations").setIcon(R.drawable.icon_list);
		menu.add(0, CHOOSE_DEFAULT, 0,"Set Default").setIcon(R.drawable.here_default_icon);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case SEARCH:
			searchBar.setVisibility(View.VISIBLE);
			break;
		case CHOOSE:
			selectCurrentPosition();
			break;
		case CHOOSE_DEFAULT:
			selectDefaultPosition();
			break;
		case VIEW_LOCATIONS:
			startActivity(new Intent(ViewLocations.VIEW_LOCATIONS_ACTION, getIntent().getData()));
			break;

		}
		return super.onOptionsItemSelected(item);
	}
	
	private void selectCurrentPosition(){
		GeoPoint p = myMap.getMapCenter();
		new LocationDialog(this, p, false).show();
	}

	private void selectDefaultPosition(){
		GeoPoint p = myMap.getMapCenter();
		new LocationDialog(this, p, true).show();
	}

	private void gotoAddress(String address){
		try {
			String xmlCode = YahooGeoAPI.getGeoCode(address);
			YahooGeocodeHandler handler = new YahooGeocodeHandler();
			Xml.parse(xmlCode, handler);
			GeoPoint p = new GeoPoint((int)handler.getLatitudeAsLong(), (int)handler.getLongitudeAsLong());
			MapController mc = myMap.getController();
			lastSearch = p;
			lastAddress = address;
			mc.animateTo(p);
			mc.setZoom(21);
		} catch (IOException e){

		} catch (SAXException e) {

		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Classe per mostrare un dialog di selezione del metodo di contatto
	 * 
	 * @author Nicolas Tagliani
	 * @author Vincenzo Frascino
	 *
	 */
	class LocationDialog extends Dialog implements OnClickListener{


		private GeoPoint p;
		private Button setHome;
		private Button setMobile;
		private Button setWork;
		private Button setMail;
		private Button setIM;
		private boolean defaultLocation;
		private Geocoder geoCoder;

		/**
		 * Costrutture del dialog
		 * 
		 * @param context Il contesto associato al dialog
		 * @param p Il punto che stiamo inserendo nel database
		 * @param defaultLocation true se stiamo inserendo la posizione di default false altrimenti. Se è a true il punto è ingorato
		 * 
		 */
		public LocationDialog(Context context, GeoPoint p, boolean defaultLocation) {
			super(context);
			this.p=p;
			this.defaultLocation = defaultLocation;
			this.setTitle("Current position is:");
			setContentView(R.layout.choose_dialog);
			geoCoder = new Geocoder(context);
			setMobile = (Button)findViewById(R.id.set_mobile);
			setHome = (Button)findViewById(R.id.set_home);
			setWork = (Button)findViewById(R.id.set_work);
			setMail = (Button)findViewById(R.id.set_email);
			setIM = (Button)findViewById(R.id.set_im);

			setMobile.setOnClickListener(this);
			setHome.setOnClickListener(this);
			setWork.setOnClickListener(this);
			setMail.setOnClickListener(this);
			setIM.setOnClickListener(this);

		}

		public void onClick(View arg0) {
			if (arg0 == setHome){
				setHome();
				this.dismiss();
			} else
				if (arg0 == setWork){
					setWork();
					this.dismiss();
				} else
					if (arg0 == setMobile){
						setMobile();
						this.dismiss();
					} else
						if (arg0 == setMail){
							setMail();
							this.dismiss();
						} else
							if (arg0 == setIM){
								setIm();
								this.dismiss();
							}

		}

		private void setHome(){
			if (defaultLocation){
				db.setDefaultLocation("HOME");
			}
			else {
				String res = "";
				try {
					Double latitude = Double.parseDouble(String.valueOf(p.getLatitudeE6())) * 1E6;
					Double longitude = Double.parseDouble(String.valueOf(p.getLongitudeE6())) * 1E6;
					List<Address> addr = geoCoder.getFromLocation(latitude, longitude, 1);
					if (addr != null){
						if (!addr.isEmpty()){
							res = addr.get(0).toString();
						}
					}
				} catch (IOException e) {

				}
				if (res.equals("")){
					if (this.p.equals(lastSearch)){
						res = lastAddress;
					}
				}


				db.addLocation(this.p, res, "HOME");
			}
		}
		
		private void setWork(){
			if (defaultLocation){
				db.setDefaultLocation("WORK");
			}
			else {
				String res = "";
				try {
					Double latitude = Double.parseDouble(String.valueOf(p.getLatitudeE6())) * 1E6;
					Double longitude = Double.parseDouble(String.valueOf(p.getLongitudeE6())) * 1E6;
					List<Address> addr = geoCoder.getFromLocation(latitude, longitude, 1);
					if (addr != null){
						if (!addr.isEmpty()){
							res = addr.get(0).toString();
						}
					}
				} catch (IOException e) {

				}
				if (res.equals("")){
					if (this.p.equals(lastSearch)){
						res = lastAddress;
					}
				}

				db.addLocation(this.p, res, "WORK");
			}
		}
		
		private void setMobile(){
			if (defaultLocation){
				db.setDefaultLocation("MOBILE");
			}
			else {
				String res = "";
				try {
					Double latitude = Double.parseDouble(String.valueOf(p.getLatitudeE6())) * 1E6;
					Double longitude = Double.parseDouble(String.valueOf(p.getLongitudeE6())) * 1E6;
					List<Address> addr = geoCoder.getFromLocation(latitude, longitude, 1);
					if (addr != null){
						if (!addr.isEmpty()){
							res = addr.get(0).toString();
						}
					}
				} catch (IOException e) {

				}
				if (res.equals("")){
					if (this.p.equals(lastSearch)){
						res = lastAddress;
					}
				}

				db.addLocation(this.p, res,  "MOBILE");
			}
		}
		
		private void setMail(){
			if (defaultLocation){
				db.setDefaultLocation("MAIL");
			}
			else {
				String res = "";
				try {
					Double latitude = Double.parseDouble(String.valueOf(p.getLatitudeE6())) * 1E6;
					Double longitude = Double.parseDouble(String.valueOf(p.getLongitudeE6())) * 1E6;
					List<Address> addr = geoCoder.getFromLocation(latitude, longitude, 1);
					if (addr != null){
						if (!addr.isEmpty()){
							res = addr.get(0).toString();
						}
					}
				} catch (IOException e) {

				}
				if (res.equals("")){
					if (this.p.equals(lastSearch)){
						res = lastAddress;
					}
				}

				db.addLocation(this.p, res, "MAIL");
			}
		}
		
		private void setIm(){
			if (defaultLocation){
				db.setDefaultLocation("IM");
			}
			else {
				String res = "";
				try {
					Double latitude = Double.parseDouble(String.valueOf(p.getLatitudeE6())) * 1E6;
					Double longitude = Double.parseDouble(String.valueOf(p.getLongitudeE6())) * 1E6;
					List<Address> addr = geoCoder.getFromLocation(latitude, longitude, 1);
					if (addr != null){
						if (!addr.isEmpty()){
							res = addr.get(0).toString();
						}
					}
				} catch (IOException e) {

				}

				if (res.equals("")){
					if (this.p.equals(lastSearch)){
						res = lastAddress;
					}
				}
				db.addLocation(this.p, res, "IM");
			}
		}
	}

	/**
	 * Classe personalizzata per la rappresentazione dei punti sulla mappa. Estendendo la classe Overlay abbiamo il controllo della mappa tramite il metodo draw
	 * 
	 * @author Nicolas Tagliani
	 * @author Vincenzo Frascino
	 * 
	 */
	protected class MyLocationOverlay extends Overlay {
		private Canvas canvas;
		private Paint paint;
		private MapView mV;
		
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
			super.draw(canvas, mapView, shadow);
			this.canvas = canvas;
			this.mV = mapView;
			
			paint = new Paint();
			paint.setTextSize(14);

			LocationManager myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

			Location l = myLocationManager.getLastKnownLocation("gps");


			// Create a Point that represents our GPS-Location
			Double lat = l.getLatitude() * 1E6;
			Double lng = l.getLongitude() * 1E6;

			GeoPoint point = new GeoPoint(lat.intValue(), lng.intValue());
			drawCircle(point , "ME", 156, 192, 36);
			
          //Aggiungo le mie posizioni prendendole dal DBHelper
			List<DBHelper.Location> ll = db.fetchAllRows();

			for (android.client.DBHelper.Location loc : ll ){
				GeoPoint point_others = new GeoPoint(loc.latitude, loc.longitude);
				drawCircle(point_others, loc.preferred, 230, 0, 0);

			}
			
			//aggiungo le posizioni dei miei amici
			String[] projection = new String[] {
					android.provider.Contacts.ContactMethods.PERSON_ID
			};
			Cursor user = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection, "kind="+Settings.MY_CONTACTS_KIND, null, null);
			while (user.moveToNext()){
				int user_id = user.getInt(user.getColumnIndex(android.provider.Contacts.ContactMethods.PERSON_ID));

				String[] projection2 = new String[] {
						android.provider.Contacts.People.NAME
				};
				Cursor nome_c = getContentResolver().query(Contacts.People.CONTENT_URI, projection2, "people._id="+user_id, null, null);
				if (nome_c.moveToNext()){
					String nome = nome_c.getString(nome_c.getColumnIndex(Contacts.People.NAME));

					String[] projection3 = new String[] {
							android.provider.Contacts.ContactMethods.DATA
					};
					Cursor pos_c = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection3, Contacts.ContactMethods.PERSON_ID+"="+user_id+" AND kind="+Contacts.ContactMethods.CONTENT_POSTAL_TYPE, null, null);
					pos_c.moveToNext();
					String pos = pos_c.getString(pos_c.getColumnIndex(Contacts.ContactMethods.DATA));

					StringTokenizer tok = new StringTokenizer(pos.substring(1), ",");
					Double latitude = Double.parseDouble(tok.nextToken()) * 1E6;
					Double longitude = Double.parseDouble(tok.nextToken()) * 1E6;
					GeoPoint point_last = new GeoPoint(latitude.intValue() , longitude.intValue());
					drawCircle(point_last, nome, 0, 0, 200);
				}
			}
			return true;

		}


		private void drawCircle(GeoPoint mypoint, String text, int R, int G, int B ){


			// Create a Point that represents our GPS-Location
			Point myScreenCoords = new Point();

			// Converts lat/lng-Point to OUR coordinates on the screen.
			mV.getProjection().toPixels(mypoint, myScreenCoords);

			// Draw a circle for our location
			RectF oval = new RectF(myScreenCoords.x - 5, myScreenCoords.y + 5,
					myScreenCoords.x + 5, myScreenCoords.y - 5);

			// Setup a color for our location
			paint.setStyle(Style.FILL);
//			paint.setARGB(255, 80, 150, 30); // Nice strong Android-Green    
			paint.setARGB(255, R, G, B); // Nice strong Android-Green    

			// Draw our name
			canvas.drawText(text,
					myScreenCoords.x +9, myScreenCoords.y, paint);

			// Change the paint to a 'Lookthrough' Android-Green
//			paint.setARGB(80, 156, 192, 36);
			paint.setARGB(80, R, G, B);

			paint.setStrokeWidth(1);
			// draw an oval around our location
			canvas.drawOval(oval, paint);

			// With a black stroke around the oval we drew before.
			paint.setARGB(255,0,0,0);
			paint.setStyle(Style.STROKE);
			canvas.drawCircle(myScreenCoords.x, myScreenCoords.y, 5, paint);


		}
	}

}
