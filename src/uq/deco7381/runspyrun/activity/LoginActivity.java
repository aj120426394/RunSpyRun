package uq.deco7381.runspyrun.activity;

import uq.deco7381.runspyrun.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.RequestPasswordResetCallback;
/**
 * This is a launch Activity of this application
 * It provide two funcation:
 * 1. Login
 * 2. Reset password
 * 
 * @author Jafo
 *
 */
public class LoginActivity extends Activity {
	
	public static final String PREFS_NAME = "LoginInfo";  // Key of SharedPreferences
	private static final String PREF_USERNAME = "username"; // Key of getting username in SharedPreference
	private static final String PREF_ORG = "organization"; // Key of getting organization in SharedPreference
	
	private View mContentView;	// The view contain the whole content.
	private View mLoadingView;	// The view contain the process animation.
	private int mShortAnimationDuration;	// Animation time
	private String prevOrg; 	// Last known organization;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		//Parse.initialize(this, "2XLuNz2w0M4iTL5VwXY2w6ICc7aYPZfnr7xyB4EF", "6ZHEiV500losBP4oHmX4f1qVuct1VyRgOlByTVQB");
		ParseAnalytics.trackAppOpened(getIntent());
		
		/*
		 *  Get the username from device's cache
		 */
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String username = pref.getString(PREF_USERNAME, null);
		setUsername(username);
		prevOrg = pref.getString(PREF_ORG, null);

		/*
		 *  Set up the loading screen and content screen
		 */
		mContentView = findViewById(R.id.login_content);
		mLoadingView = findViewById(R.id.loading_spinner);
		mLoadingView.setVisibility(View.GONE);
		mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
		
		

	}


	/**
	 * Set the username on the login screen if user have login before.
	 * 
	 * @param username
	 * @see onCreate()
	 */
	private void setUsername(String username){
		EditText usernameEditText = (EditText) findViewById(R.id.username);
		usernameEditText.setText(username);
	}
	
	/**
	 *onClick method triggered by button "Login"
	 * 1. Get data that user input from TextView
	 * 2. Set up the dialog for alert user in some situation
	 * 3. Validate the username and password on Parse.
	 * 
	 * @param view
	 */
	public void login(View view){
			
		showLoading();
		final Intent intent = new Intent(this, DashboardActivity.class);
			
		/*
		 * Retrieve user input
		 */
		EditText usernameEditText = (EditText) findViewById(R.id.username);
		final String usernameString = usernameEditText.getText().toString();
		
		EditText passwordEditText = (EditText) findViewById(R.id.password);
		String passwordString = passwordEditText.getText().toString();
		
		/*
		 *  LoginActivity system by Parse
		 */
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		ParseUser.logInInBackground(usernameString, passwordString, new LogInCallback() {
				
			@Override
			public void done(ParseUser user, ParseException e) {
				// TODO Auto-generated method stub
				if(user != null){
					/*
					 * Check if the user is same as previous user.
					 */
					if(!user.getString("organization").equals(prevOrg)){
						PushService.unsubscribe(LoginActivity.this, prevOrg);
						PushService.subscribe(LoginActivity.this, user.getString("organization"), LoginActivity.class);
						getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
						.edit()
						.putString(PREF_ORG, user.getString("organization"))
						.commit();
					}
					startActivity(intent);
					showContent();
				}else if(e.getMessage().startsWith("i/o failure")){
					/*
					 * if there is a exception from parse, show in dialog box
					 */
					builder.setTitle("Alert");
					builder.setMessage("Connection fail");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
							showContent();
						}
					});
					//show the dialog box
					AlertDialog alert = builder.create();
					alert.show();
				}else{
					builder.setTitle("Alert");
					builder.setMessage("Invalid username or password");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
							showContent();
						}
					});
					//show the dialog box
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});
	}

	/**
	 * onClick method triggered by button "Signup"
	 * 
	 * 1. Direct user to "Signup" activity
	 * @param view
	 */
	public void signup(View view){
		Intent intent = new Intent(this, SignupActivity.class);
		startActivity(intent);
	}
	
	/**
	 * onClick method triggered by "Forget password"
	 * 1. Pop out a dialog let user input the email address(already registered)
	 * 2. Sending email address to Parse
	 * (Parse will send a mail to user's mail box to reset password)
	 * 
	 * @param view
	 */
	public void resetPassword(View view){
		/*
		 *  Set up a layout to input in Alert Dialog
		 */
		LayoutInflater inflater = LayoutInflater.from(this);
		final View v = inflater.inflate(R.layout.dialog_resetpassword, null);
		
		/*
		 *  Build a AlertDialog
		 */
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Reset the password");
		builder.setView(v);
		builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				/*
				 *  Get the email address from the AlertDialog and send a password reset email to user.
				 */
				EditText email = (EditText)(v.findViewById(R.id.rpEmail));
				ParseUser.requestPasswordResetInBackground(email.getText().toString(), new RequestPasswordResetCallback(){
					@Override
					public void done(ParseException e) {
						// TODO Auto-generated method stub
						/*
						 *  Avoid software keyboard remain on the screen after click "Send" button
						 */
						InputMethodManager imm = ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE));
						imm.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
						if(e == null){
							Toast.makeText(getApplicationContext(), "Password reset mail sent", Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
						}
					}
					
				});
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	/**
	 * Set up the screen in normal condition
	 * 1. Set content visible
	 * 2. set loading progress invisible
	 */
	private void showContent(){
		mContentView.setAlpha(0f);
		mContentView.setVisibility(View.VISIBLE);
		
		mContentView.animate()
					.alpha(1f)
					.setDuration(mShortAnimationDuration)
					.setListener(null);
		
		mLoadingView.animate()
					.alpha(0f)
					.setDuration(mShortAnimationDuration)
					.setListener(new AnimatorListenerAdapter() {
						@Override
	                    public void onAnimationEnd(Animator animation) {
	                        mLoadingView.setVisibility(View.GONE);
	                    }
					});
	}
	/**
	 * Set up the screen in loading condition
	 * 1. Set loading progress visible
	 * 2. Set content invisible
	 */
	private void showLoading(){
		mLoadingView.setAlpha(0f);
		mLoadingView.setVisibility(View.VISIBLE);
		
		mLoadingView.animate()
					.alpha(1f)
					.setDuration(mShortAnimationDuration)
					.setListener(null);
		
		mContentView.animate()
					.alpha(0.5f)
					.setDuration(mShortAnimationDuration)
					.setListener(new AnimatorListenerAdapter() {
						@Override
	                    public void onAnimationEnd(Animator animation) {
							mContentView.setVisibility(View.GONE);
	                    }
					});
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		/*
		 *  Store the username into the device.
		 */
		EditText usernameEditText = (EditText) findViewById(R.id.username);
		String usernameString = usernameEditText.getText().toString();
		
		getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
		.edit()
		.putString(PREF_USERNAME, usernameString)
		.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 *  Inflate the menu; this adds items to the action bar if it is present.
		 */
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
