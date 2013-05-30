package si.kloko.android.widget.ssid;

import android.content.Context;
import android.content.SharedPreferences;

public class SSWidgetUtil {

	public static int readBackgroundColor(Context context, int appWidgetId) {

		SharedPreferences widgetSettings = context.getSharedPreferences("NETI_PREF", Context.MODE_PRIVATE);
		if (widgetSettings == null) {
			return SSWidgetProvider.defaultColor;
		} else {
			return widgetSettings.getInt("NETI_BARVA" + String.valueOf(appWidgetId), 0xFF000000);
		}
	}

	public static void saveBackgroundColor(Context context, int appWidgetId, int color) {

		SharedPreferences prefs = context.getSharedPreferences("NETI_PREF", Context.MODE_PRIVATE);
		if (prefs != null) {
			SharedPreferences.Editor edit = prefs.edit();
			if (edit != null) {
				edit.putInt("NETI_BARVA" + String.valueOf(appWidgetId), color);
				edit.commit();
			}
		}
	}

	public static void delete(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences("NETI_PREF", Context.MODE_PRIVATE);
		if (prefs != null) {
			SharedPreferences.Editor edit = prefs.edit();
			if (edit != null) {
				edit.remove("NETI_BARVA" + String.valueOf(appWidgetId));
				edit.commit();
			}
		}
	}

}
