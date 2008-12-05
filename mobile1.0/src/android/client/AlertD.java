package android.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

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
	
	static void show(Context context, CharSequence title, int iconId, CharSequence message, CharSequence button1Text, OnClickListener button1Listener, CharSequence button2Text, OnClickListener button2Listener, boolean cancelable, OnCancelListener cancelListener) {
		
		new AlertDialog.Builder(context)
		.setMessage(message)
        .setTitle(title)
        .setCancelable(cancelable)
        .setIcon(iconId)
	     .setPositiveButton(button1Text, button1Listener)
	     .setNegativeButton(button2Text, button2Listener).create().show();
		
	}
	
}
