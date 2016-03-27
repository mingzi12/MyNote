package com.mingzi.onenote.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.mingzi.onenote.R;
import com.mingzi.onenote.activity.MainActivity;
import com.mingzi.onenote.activity.NewNoteActivity;


public class AppWidget extends AppWidgetProvider {
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		for (int i = 0; i < appWidgetIds.length; i++) {
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);
			remoteViews.setOnClickPendingIntent(R.id.appname, pendingIntent);
			
			intent = new Intent(context, NewNoteActivity.class);
			pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			remoteViews.setOnClickPendingIntent(R.id.widgetAddButton, pendingIntent);
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
		}
	}
}
