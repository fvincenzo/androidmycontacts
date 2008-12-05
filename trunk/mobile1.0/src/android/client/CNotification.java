package android.client;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class CNotification {

	static Notification notifier(Context context, int icon, CharSequence tickerText, long when, CharSequence expandedTitle, CharSequence expandedText, Intent contentIntent, int appIcon, CharSequence appName, Intent appIntent) {
		
		Notification n = new Notification(icon, tickerText, when);
		
		contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		PendingIntent pi = PendingIntent.getActivity(context, 0, contentIntent, android.app.PendingIntent.FLAG_ONE_SHOT);
		
		n.setLatestEventInfo(context, expandedTitle, expandedText, pi);

		return n;
		
	}
	
}
