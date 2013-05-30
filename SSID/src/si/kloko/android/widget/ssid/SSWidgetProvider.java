package si.kloko.android.widget.ssid;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import org.apache.http.conn.util.InetAddressUtils;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

public class SSWidgetProvider extends AppWidgetProvider {

	final static int defaultColor = Color.argb(0x7F, Color.red(0x76AE76), Color.green(0x76AE76), Color.blue(0x76AE76));

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int widgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, widgetId);
		}
	}

	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

		// Inflate layout
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

		// Update UI
		int barva = SSWidgetUtil.readBackgroundColor(context, appWidgetId);
		remoteViews.setInt(R.id.update, "setBackgroundColor", Color.argb(Color.alpha(barva), Color.red(barva), Color.green(barva), Color.blue(barva)));
		remoteViews.setTextViewText(R.id.update, getLocalNetAddress(context));

		// When user click on the label, update ALL the instances of the widget.
		Intent labelIntent = get_ACTION_APPWIDGET_UPDATE_Intent(context);
		PendingIntent labelPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, labelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.update, labelPendingIntent);

		// Call the Manager to ensure the changes take effect
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {

		for (int widgetId : appWidgetIds) {
			SSWidgetUtil.delete(context, widgetId);
		}
	}

	/**
	 * Utility method to ensure that when we want an Intent that fire
	 * ACTION_APPWIDGET_UPDATE, the extras are correct. The default
	 * implementation of onReceive() will discard it if we don't add the ids of
	 * all the instances.
	 * 
	 * @param context
	 * @return
	 */
	protected static Intent get_ACTION_APPWIDGET_UPDATE_Intent(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), SSWidgetProvider.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
		Intent intent = new Intent(context, SSWidgetProvider.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		return intent;
	}

	private static String getLocalNetAddress(Context context) {
		String errStatus = "No info available!";
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null) {
					if (info.isConnected()) {
						String ipAddr = "";
						String extra = "";

						switch (info.getType()) {
						case ConnectivityManager.TYPE_WIFI:
							WifiManager wifiManager = null;
							wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
							WifiInfo wifiInfo = wifiManager.getConnectionInfo();
							extra = wifiInfo.getSSID();
							break;

						case ConnectivityManager.TYPE_MOBILE:
							TelephonyManager teleManager = null;
							teleManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

							switch (teleManager.getNetworkType()) {
							case TelephonyManager.NETWORK_TYPE_1xRTT:
								extra = "1xRTT";
								break;
							case TelephonyManager.NETWORK_TYPE_CDMA:
								extra = "CDMA";
								break;
							case TelephonyManager.NETWORK_TYPE_EDGE:
								extra = "EDGE";
								break;
							case TelephonyManager.NETWORK_TYPE_EHRPD:
								extra = "EHRPD";
								break;
							case TelephonyManager.NETWORK_TYPE_EVDO_0:
								extra = "EVDO_0";
								break;
							case TelephonyManager.NETWORK_TYPE_EVDO_A:
								extra = "EVDO_A";
								break;
							case TelephonyManager.NETWORK_TYPE_EVDO_B:
								extra = "EVDO_B";
								break;
							case TelephonyManager.NETWORK_TYPE_GPRS:
								extra = "GPRS";
								break;
							case TelephonyManager.NETWORK_TYPE_HSDPA:
								extra = "HSDPA";
								break;
							case TelephonyManager.NETWORK_TYPE_HSPA:
								extra = "HSPA";
								break;
							case TelephonyManager.NETWORK_TYPE_HSPAP:
								extra = "HSPAP";
								break;
							case TelephonyManager.NETWORK_TYPE_HSUPA:
								extra = "HSUPA";
								break;
							case TelephonyManager.NETWORK_TYPE_IDEN:
								extra = "IDEN";
								break;
							case TelephonyManager.NETWORK_TYPE_LTE:
								extra = "LTE";
								break;
							case TelephonyManager.NETWORK_TYPE_UMTS:
								extra = "UMTS";
								break;
							default:
								extra = "Unknown";
								break;
							}
							break;
						default:
						}

						for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
							NetworkInterface intf = en.nextElement();
							for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
								InetAddress inetAddress = enumIpAddr.nextElement();
								if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
									ipAddr = inetAddress.getHostAddress().toString();
								}
							}
						}

						return info.getTypeName() + " " + extra + " " + ipAddr;

					} else
						errStatus = "No WIFI network connection!";
				} else
					errStatus = "No active network connection!";

			} else
				errStatus = "No conectivity on device!";

		} catch (Exception ex) {
			errStatus = ex.toString();
		}
		return errStatus;
	}

}