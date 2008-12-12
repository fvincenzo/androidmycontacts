package android.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.client.NewsService.RunTask;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class News extends Activity {

	private static final String LOG_TAG = "NEWSSERVICECLIENT";
    private INewsService counterService;
    private NewsServiceConnection conn;
    private boolean started = false;
    
    private int news = 0;
    private int news2 = 0;
    private Context ctx;
    private Handler serviceHandler = null;
    public static final String DATE_FORMAT_NOW = "dd-MM-yyyy HH:mm:ss";
    
    private ImageButton newsb; 

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.newsstat);
        
        ctx = this;
        
        newsb = (ImageButton)findViewById(R.id.opensite);
        
        newsb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent("android.intent.action.VIEW", Uri
						.parse("http:// www.google.com"));
				startActivity(myIntent);
			}

		});
         
        Button startServiceButton = (Button) findViewById(R.id.startservice );
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
			  startService();
			  initService();
     		}
        });
        Button stopServiceButton = (Button) findViewById(R.id.stopservice );
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
              releaseService();
			  stopService();
     		}
        });
        Button invokeServiceButton = (Button) findViewById(R.id.invokeservice );
        invokeServiceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
			  viewService();
     		}
        });
    }

    @Override
    protected void onDestroy() {
	  super.onDestroy();
	  releaseService();
    }

	private void initService() {
	  if( conn == null ) {
	    conn = new NewsServiceConnection();
	    Intent i = new Intent();
	    i.setClassName( "android.client", 
		"android.client.NewsService" );
	    bindService( i, conn, Context.BIND_AUTO_CREATE);
		updateServiceStatus();
		Log.d( LOG_TAG, "bindService()" );
	  } else {
       
      }
	}

	private void releaseService() {
	  if( conn != null ) {
	    unbindService( conn );	  
	    conn = null;
		updateServiceStatus();
		Log.d( LOG_TAG, "unbindService()" );
	  } else {
        
	  }
	}

    private void startService() {
	  if( started ) {
        
	  } else {
	    Intent i = new Intent();
	    i.setClassName( "android.client", 
			"android.client.NewsService" );
	    startService(i);
	    Log.d( LOG_TAG, "startService()" );
		started = true;
		updateServiceStatus();
		
		serviceHandler = new Handler();
  	  	serviceHandler.postDelayed( new RunTask(),5000L );
		
	  }
	}

    private void stopService() {
	  if( !started ) {
        
	  } else {
	    Intent i = new Intent();
	    i.setClassName( "android.client", 
			"android.client.NewsService" );
	    stopService( i );
	    Log.d( LOG_TAG, "stopService()" );
		started = false;
		updateServiceStatus();
	  }
    }

    private void invokeService() {
	  if( conn == null ) {
      
	  } else {
		try {
		  int ctr = counterService.getCounterValue();
		  news = ctr;
		} catch( RemoteException ex ) {
		  Log.e( LOG_TAG, "DeadObjectException" );
		}
	  }
	}
    
    private void viewService() {
    	
    	TextView t = (TextView)findViewById(R.id.result);
		t.setText( "Counter value: "+Integer.toString( news ) );
    	
    }
    
    private void updateServiceStatus() {
	  String bindStatus = conn == null ? "unbound" : "bound";
	  String startStatus = started ? "started" : "not started";
	  String statusText = "Service status: "+
							bindStatus+
							","+
							startStatus;
	  TextView t = (TextView)findViewById( R.id.servicestatus );
	  t.setText( statusText );	  
	}

    class NewsServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, 
			IBinder boundService ) {
          counterService = INewsService.Stub.asInterface((IBinder)boundService);
		  Log.d( LOG_TAG,"onServiceConnected" );
        }

        public void onServiceDisconnected(ComponentName className) {
          counterService = null;
		  Log.d( LOG_TAG,"onServiceDisconnected" );
		  updateServiceStatus();
        }
    };

    class RunTask implements Runnable {
  	  public void run() {
  		  
  		Calendar cal = Calendar.getInstance();
  	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
  	    
  		invokeService();
  		if (news2 == 0) news2 = news;
  		else
  		if (news2 != news) {
  			news2 = news;
  			AlertD.show(ctx, "New News", 0, sdf.format(cal.getTime())+": There's a news on the website", "OK", false);
  		}
  		serviceHandler.postDelayed( this, 50000L );
  	  }
  	}

	
}
