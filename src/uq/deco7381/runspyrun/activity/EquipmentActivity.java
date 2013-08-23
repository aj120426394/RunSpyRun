package uq.deco7381.runspyrun.activity;

import uq.deco7381.runspyrun.R;
import uq.deco7381.runspyrun.R.layout;
import uq.deco7381.runspyrun.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class EquipmentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equipment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.equipment, menu);
		return true;
	}
	
	public void setDatastream(){
		Intent intent = new Intent(this, DefenceActivity.class);
	}

}
