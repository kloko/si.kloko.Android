package si.kloko.app.kafra;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import si.kloko.app.kafra.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class PrikazActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	static final String KAFRA_URL = "http://www.harfa-restavracija.si/index.php?module=strani&stranid=24";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_prikaz);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final TextView contentView = (TextView) findViewById(R.id.fullscreen_content);

		if (hasConnection())
			new getKafraToday().execute(KAFRA_URL);
		else
			Toast.makeText(this, "No Internet connection found.", Toast.LENGTH_LONG).show();

		Button btnPon = (Button) findViewById(R.id.pon_button);
		btnPon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new getKafraToday().execute("http://www.harfa-restavracija.si/index.php?module=strani&stranid=24&dan=1");
			}
		});

		Button btnTor = (Button) findViewById(R.id.tor_button);
		btnTor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new getKafraToday().execute("http://www.harfa-restavracija.si/index.php?module=strani&stranid=24&dan=2");
			}
		});

		Button btnSre = (Button) findViewById(R.id.sre_button);
		btnSre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new getKafraToday().execute("http://www.harfa-restavracija.si/index.php?module=strani&stranid=24&dan=3");
			}
		});

		Button btnCet = (Button) findViewById(R.id.cet_button);
		btnCet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new getKafraToday().execute("http://www.harfa-restavracija.si/index.php?module=strani&stranid=24&dan=4");
			}
		});

		Button btnPet = (Button) findViewById(R.id.pet_button);
		btnPet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new getKafraToday().execute("http://www.harfa-restavracija.si/index.php?module=strani&stranid=24&dan=5");
			}
		});

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
			// Cached values.
			int mControlsHeight;
			int mShortAnimTime;

			@Override
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			public void onVisibilityChange(boolean visible) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					// If the ViewPropertyAnimator API is available
					// (Honeycomb MR2 and later), use it to animate the
					// in-layout UI controls at the bottom of the
					// screen.
					if (mControlsHeight == 0) {
						mControlsHeight = controlsView.getHeight();
					}
					if (mShortAnimTime == 0) {
						mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
					}
					controlsView.animate().translationY(visible ? 0 : mControlsHeight).setDuration(mShortAnimTime);
				} else {
					// If the ViewPropertyAnimator APIs aren't
					// available, simply show or hide the in-layout UI
					// controls.
					controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
				}

				if (visible && AUTO_HIDE) {
					// Schedule a hide().
					delayedHide(AUTO_HIDE_DELAY_MILLIS);
				}
			}
		});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.pon_button).setOnTouchListener(mDelayHideTouchListener);
		findViewById(R.id.tor_button).setOnTouchListener(mDelayHideTouchListener);
		findViewById(R.id.sre_button).setOnTouchListener(mDelayHideTouchListener);
		findViewById(R.id.cet_button).setOnTouchListener(mDelayHideTouchListener);
		findViewById(R.id.pet_button).setOnTouchListener(mDelayHideTouchListener);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	private class getKafraToday extends AsyncTask<String, Void, String> {
		ProgressDialog pd;

		protected void onPreExecute() {

			pd = new ProgressDialog(PrikazActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			if (hasConnection())
				pd.setMessage("Downloading kafra...");
			else {
				pd.setMessage("No internet connection found...");
				cancel(true);
			}
			pd.show();
		}

		protected String doInBackground(String... url) {
			String result = "";

			try {
				// get html document structure
				Document document = Jsoup.connect(url[0]).userAgent("Mozilla").get();

				// aktualni teden
				Elements kafraLines = document.select("div#kPadding > .kDatum");
				result = result + kafraLines.get(0).text();
				result = result + "\n";

				// izbrani dan
				kafraLines = document.select("div#kMenu > a[style*=none]");
				result = result + "\n" + kafraLines.get(0).text();
				result = result + "\n";

				// menu za izbrani dan
				kafraLines = document.select("div#kPonudba > .kponMark");
				// iterate results
				for (Element el : kafraLines) {
					result = result + "\n" + el.text();
				}

			} catch (Exception e) {
				result = e.toString();
			}
			return result;
		}

		protected void onPostExecute(String result) {
			pd.dismiss();
			TextView contentView = (TextView) findViewById(R.id.fullscreen_content);
			contentView.setMovementMethod(new ScrollingMovementMethod());
			contentView.setText(result);
		}

	}

	public boolean hasConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}

		return false;
	}
}
