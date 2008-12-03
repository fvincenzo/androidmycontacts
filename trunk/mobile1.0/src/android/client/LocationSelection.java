package android.client;


import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import org.xml.sax.SAXException;
import com.google.android.maps.Point;
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
import android.view.Menu.Item;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayController;

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
	private Point lastSearch;

	/**
	 * @uml.property  name="db"
	 * @uml.associationEnd  
	 */
	private DBHelper db;
	protected OverlayController myOverlayController = null; 
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

		myOverlayController = myMap.createOverlayController();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
		myOverlayController.add(myLocationOverlay, true); 
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
		menu.add(0, SEARCH, "Search", R.drawable.search_icon);
		menu.add(0, CHOOSE, "Choose here", R.drawable.here_icon);
		menu.add(0, VIEW_LOCATIONS, "View locations", R.drawable.icon_list);
		menu.add(0, CHOOSE_DEFAULT, "Set Default", R.drawable.here_default_icon);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(Item item) {
		switch (item.getId()) {
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
		Point p = myMap.getMapCenter();
		new LocationDialog(this, p, false).show();
	}

	private void selectDefaultPosition(){
		Point p = myMap.getMapCenter();
		new LocationDialog(this, p, true).show();
	}

	private void gotoAddress(String address){
		try {
			String xmlCode = YahooGeoAPI.getGeoCode(address);
			YahooGeocodeHandler handler = new YahooGeocodeHandler();
			Xml.parse(xmlCode, handler);
			Point p = new Point((int)handler.getLatitudeAsLong(), (int)handler.getLongitudeAsLong());
			MapController mc = myMap.getController();
			lastSearch = p;
			lastAddress = address;
			mc.animateTo(p);
			mc.zoomTo(21);
		} catch (IOException e){

		} catch (SAXException e) {

		}
	}

	/**
	 * Classe per mostrare un dialog di selezione del metodo di contatto
	 * 
	 * @author Nicolas Tagliani
	 * @author Vincenzo Frascino
	 *
	 */
	class LocationDialog extends Dialog implements OnClickListener{


		private Point p;
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
		public LocationDialog(Context context, Point p, boolean defaultLocation) {
			super(context);
			this.p=p;
			this.defaultLocation = defaultLocation;
			this.setTitle("Current position is:");
			setContentView(R.layout.choose_dialog);
			geoCoder = new Geocoder();
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
					Address[] addr = geoCoder.getFromLocation(p.getLatitudeE6(), p.getLongitudeE6());
					if (addr != null){
						if (addr.length > 0){
							res = addr[0].toString();
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
					Address[] addr = geoCoder.getFromLocation(p.getLatitudeE6(), p.getLongitudeE6());
					if (addr != null){
						if (addr.length > 0){
							res = addr[0].toString();
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
					Address[] addr = geoCoder.getFromLocation(p.getLatitudeE6(), p.getLongitudeE6());
					if (addr != null){
						if (addr.length > 0){
							res = addr[0].toString();
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
					Address[] addr = geoCoder.getFromLocation(p.getLatitudeE6(), p.getLongitudeE6());
					if (addr != null){
						if (addr.length > 0){
							res = addr[0].toString();
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
					Address[] addr = geoCoder.getFromLocation(p.getLatitudeE6(), p.getLongitudeE6());
					if (addr != null){
						if (addr.length > 0){
							res = addr[0].toString();
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
		private PixelCalculator calculator;
		private Paint paint;
		
		@Override
		public void draw(Canvas canvas, PixelCalculator calculator, boolean shadow) {
			super.draw(canvas, calculator, shadow);
			this.canvas = canvas;
			this.calculator = calculator;


			paint = new Paint();
			paint.setTextSize(14);

			LocationManager myLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

			Location l = myLocationManager.getCurrentLocation("gps");

//			// Setup our "brush"/"pencil"/ whatever...
//			Paint paint = new Paint();
//			paint.setTextSize(14);


			// Create a Point that represents our GPS-Location
			Double lat = l.getLatitude() * 1E6;
			Double lng = l.getLongitude() * 1E6;

			drawCircle(lat.intValue(), lng.intValue(), "ME", 156, 192, 36);
			
          //Aggiungo le mie posizioni prendendole dal DBHelper
			List<DBHelper.Location> ll = db.fetchAllRows();

			for (android.client.DBHelper.Location loc : ll ){
				drawCircle(loc.latitude, loc.longitude, loc.preferred, 230, 0, 0);

			}
			
			//aggiungo le posizioni dei miei amici
			String[] projection = new String[] {
					android.provider.Contacts.ContactMethods.PERSON_ID
			};
			Cursor user = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection, "kind="+Settings.MY_CONTACTS_KIND, null, null);
			while (user.next()){
				int user_id = user.getInt(user.getColumnIndex(android.provider.Contacts.ContactMethods.PERSON_ID));

				String[] projection2 = new String[] {
						android.provider.Contacts.People.NAME
				};
				Cursor nome_c = getContentResolver().query(Contacts.People.CONTENT_URI, projection2, "people._id="+user_id, null, null);
				if (nome_c.next()){
					String nome = nome_c.getString(nome_c.getColumnIndex(Contacts.People.NAME));

					String[] projection3 = new String[] {
							android.provider.Contacts.ContactMethods.DATA
					};
					Cursor pos_c = getContentResolver().query(Contacts.ContactMethods.CONTENT_URI, projection3, Contacts.ContactMethods.PERSON_ID+"="+user_id+" AND kind="+Contacts.ContactMethods.POSTAL_KIND, null, null);
					pos_c.next();
					String pos = pos_c.getString(pos_c.getColumnIndex(Contacts.ContactMethods.DATA));

					StringTokenizer tok = new StringTokenizer(pos.substring(1), ",");
					Double latitude = Double.parseDouble(tok.nextToken()) * 1E6;
					Double longitude = Double.parseDouble(tok.nextToken()) * 1E6;
					drawCircle( latitude.intValue() , longitude.intValue(), nome, 0, 0, 200);
				}
			}

		}


		private void drawCircle(int latitude, int longitde, String text, int R, int G, int B ){


			// Create a Point that represents our GPS-Location
			Point point = new Point(latitude, longitde);

			int[] myScreenCoords = new int[2];
			// Converts lat/lng-Point to OUR coordinates on the screen.
			calculator.getPointXY(point, myScreenCoords);

			// Draw a circle for our location
			RectF oval = new RectF(myScreenCoords[0] - 5, myScreenCoords[1] + 5,
					myScreenCoords[0] + 5, myScreenCoords[1] - 5);

			// Setup a color for our location
			paint.setStyle(Style.FILL);
//			paint.setARGB(255, 80, 150, 30); // Nice strong Android-Green    
			paint.setARGB(255, R, G, B); // Nice strong Android-Green    

			// Draw our name
			canvas.drawText(text,
					myScreenCoords[0] +9, myScreenCoords[1], paint);

			// Change the paint to a 'Lookthrough' Android-Green
//			paint.setARGB(80, 156, 192, 36);
			paint.setARGB(80, R, G, B);

			paint.setStrokeWidth(1);
			// draw an oval around our location
			canvas.drawOval(oval, paint);

			// With a black stroke around the oval we drew before.
			paint.setARGB(255,0,0,0);
			paint.setStyle(Style.STROKE);
			canvas.drawCircle(myScreenCoords[0], myScreenCoords[1], 5, paint);


		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
