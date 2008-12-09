package android.client;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AndroidSES extends Activity {

	private ImageButton rc;
	private ImageButton rc1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		rc = (ImageButton) findViewById(R.id.widget30);

		rc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent("android.intent.action.VIEW", Uri
						.parse("http:// www.google.com"));
				startActivity(myIntent);
			}

		});
		
		rc1 = (ImageButton) findViewById(R.id.widget33);
		
		
		rc1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent i = new Intent(AndroidSES.this, MyContatsClient.class);
				startActivity(i);
			}

		});
		
	}
}