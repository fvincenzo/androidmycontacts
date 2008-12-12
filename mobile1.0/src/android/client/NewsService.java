package android.client;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class NewsService extends Service {

	private static final String LOG_TAG = "NEWSSERVICE";
	private int counter = 0;
	private Handler serviceHandler = null;
	private WSInvoke ws = null;

    @Override
    public void onStart(Intent i, int startId) {
	  super.onStart( i, startId );
      Log.d( LOG_TAG, "onStart" );
	  serviceHandler = new Handler();
	  serviceHandler.postDelayed( new RunTask(),1000L );
    }

    @Override
    public void onCreate() {
      super.onCreate();
      ws = new WSInvoke();
	  Log.d( LOG_TAG,"onCreate" );
    }

    @Override
	public void onDestroy() {
	  super.onDestroy();
	  Log.d( LOG_TAG,"onDestroy" );
    }

    /**
     * The IAdderService is defined through IDL
     */
    private final INewsService.Stub binder = 
			new INewsService.Stub() {
      public int getCounterValue() {
		return counter;
      }
    };

    /**
     * This internal class implements the requester to te webservice
     * 
     *
     */
    class RunTask implements Runnable {
	  public void run() {
		counter = ws.returnValue();
		serviceHandler.postDelayed( this, 5000L );
	  }
	}


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return binder;
	}

}
