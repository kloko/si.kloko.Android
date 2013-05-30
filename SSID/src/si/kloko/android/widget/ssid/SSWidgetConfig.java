package si.kloko.android.widget.ssid;

import de.devmil.common.ui.color.ColorSelectorDialog;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SSWidgetConfig extends Activity {

	private Context self = this;
	private int appWidgetId;
	private int barva = SSWidgetProvider.defaultColor;

	public void colorPicker() {
		ColorSelectorDialog dialog = new ColorSelectorDialog(this, new ColorSelectorDialog.OnColorChangedListener() {
			public void colorChanged(int color) {
				// color is the color selected by the user.
				barva = color;
				Button knopf = (Button) findViewById(R.id.colorCode);
				knopf.setBackgroundColor(barva);
				knopf.setTextColor(inverseColor(barva));
			}
		}, barva);
		dialog.setTitle("Select background color");
		dialog.show();
	}

	public static int inverseColor(int barva) {
		return Color.argb(Color.alpha(barva), 255 - Color.red(barva), 255 - Color.green(barva), 255 - Color.blue(barva));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the result for cancel first
		// if the user cancels, then the appWidget
		// should not appear
		Intent cancelResultValue = new Intent();
		cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_CANCELED, cancelResultValue);
		
		// get the appWidgetId of the appWidget being configured
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		if (extras != null) {
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

		// show the user interface of configuration
		setContentView(R.layout.config_layout);

		// colorPicker button
		Button color = (Button) findViewById(R.id.colorCode);
		color.setBackgroundColor(barva);
		color.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				colorPicker();
			}
		});

		// OK button
		Button ok = (Button) findViewById(R.id.main_ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				final Context context = SSWidgetConfig.this;
				 
				// store color value
				SSWidgetUtil.saveBackgroundColor(self, appWidgetId, barva);

				// perform initial update
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				SSWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId);
				
				// change the result to OK
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				setResult(RESULT_OK, resultValue);

				// finish closes activity
				// and sends the OK result
				// the widget will be be placed on the home screen
				finish();
			}
		});

		// Cancel button
		Button cancel = (Button) findViewById(R.id.main_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// finish sends the already configured cancel result
				// and closes activity
				finish();
			}
		});
	}
}
