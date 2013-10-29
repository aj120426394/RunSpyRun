package uq.deco7381.runspyrun;

import uq.deco7381.runspyrun.activity.LoginActivity;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;
/**
 * This class will be called when user launch the app.
 * It will initialize the Parse, and install the push notification service to this device.
 * 
 * @author Jafo
 * @version 1.0
 * @since 15/10/2013
 *
 */
public class Application extends android.app.Application {

  public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

	// Initialize the Parse SDK.
    Parse.initialize(this, "2XLuNz2w0M4iTL5VwXY2w6ICc7aYPZfnr7xyB4EF", "6ZHEiV500losBP4oHmX4f1qVuct1VyRgOlByTVQB"); 

	// Specify a Activity to handle all pushes by default.
	PushService.setDefaultPushCallback(this, LoginActivity.class);
	
	// Save the current installation.
	ParseInstallation.getCurrentInstallation().saveInBackground();
  }
}