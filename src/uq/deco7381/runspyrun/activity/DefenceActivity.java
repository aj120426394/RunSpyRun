package uq.deco7381.runspyrun.activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;

import uq.deco7381.runspyrun.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

public class DefenceActivity extends Activity implements AnimationListener {
	
	View equipment;
    View content;
    boolean menuOut = false;
    AnimParams animParams = new AnimParams();
    GoogleMap map;
    private LocationManager status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_defence);
		
		
		status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
		
		// Check is GPS available
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			// Check the map is exist or not
			if (map == null){
				map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
				if(map != null){
					setUpMap();
				}
			}
		}else{
			// If GPS is no available, direct user to Setting  
			Toast.makeText(this, "Please open the GPS", Toast.LENGTH_LONG).show();
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
		
		equipment = findViewById(R.id.defence_equipment);
        content = findViewById(R.id.defence_content);

        //ViewUtils.printView("equipment", equipment);
        //ViewUtils.printView("content", content);

	}

	private void setUpMap(){
		map.setMyLocationEnabled(true);
		UiSettings uiSettings = map.getUiSettings();
		uiSettings.setAllGesturesEnabled(false);
		uiSettings.setMyLocationButtonEnabled(false);
		uiSettings.setZoomControlsEnabled(false);
	}
	public void slideMenu(View v){
		DefenceActivity me = DefenceActivity.this;
        Context context = me;
        Animation anim;

        int w = content.getMeasuredWidth();
        int h = content.getMeasuredHeight();
        int left = (int) (content.getMeasuredWidth() * 0.8);

        if (!menuOut) {
            // anim = AnimationUtils.loadAnimation(context, R.anim.push_right_out_80);
            anim = new TranslateAnimation(0, left, 0, 0);
            equipment.setVisibility(View.VISIBLE);
            animParams.init(left, 0, left + w, h);
        } else {
            // anim = AnimationUtils.loadAnimation(context, R.anim.push_left_in_80);
            anim = new TranslateAnimation(0, -left, 0, 0);
            animParams.init(0, 0, w, h);
        }

        anim.setDuration(500);
        anim.setAnimationListener(me);
        //Tell the animation to stay as it ended (we are going to set the app.layout first than remove this property)
        anim.setFillAfter(true);


        // Only use fillEnabled and fillAfter if we don't call layout ourselves.
        // We need to do the layout ourselves and not use fillEnabled and fillAfter because when the anim is finished
        // although the View appears to have moved, it is actually just a drawing effect and the View hasn't moved.
        // Therefore clicking on the screen where the button appears does not work, but clicking where the View *was* does
        // work.
        // anim.setFillEnabled(true);
        // anim.setFillAfter(true);

        content.startAnimation(anim);
	}
	
	
	void layoutApp(boolean menuOut) {
        System.out.println("layout [" + animParams.left + "," + animParams.top + "," + animParams.right + ","
                + animParams.bottom + "]");
        content.layout(animParams.left, animParams.top, animParams.right, animParams.bottom);
        //Now that we've set the app.layout property we can clear the animation, flicker avoided :)
        content.clearAnimation();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.defence, menu);
		return true;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		//ViewUtils.printView("menu", equipment);
        //ViewUtils.printView("app", content);
        menuOut = !menuOut;
        if (!menuOut) {
            equipment.setVisibility(View.INVISIBLE);
        }
        layoutApp(menuOut);
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	static class AnimParams {
        int left, right, top, bottom;

        void init(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

}
