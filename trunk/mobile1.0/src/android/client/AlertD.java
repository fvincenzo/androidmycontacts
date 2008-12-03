package android.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertD{

	static void show(Context context, CharSequence title, int iconId, CharSequence message, CharSequence buttonText, boolean cancelable){
		
		new AlertDialog.Builder(context)
        .setMessage(message)
        .setTitle(title)
        .setCancelable(cancelable)
        .setIcon(iconId)
        .setPositiveButton(buttonText, new DialogInterface.OnClickListener(){
             @Override
             public void onClick(DialogInterface d, int which) {
                  d.dismiss();
             }
        }
        ).create().show();
		
	}
	
}
