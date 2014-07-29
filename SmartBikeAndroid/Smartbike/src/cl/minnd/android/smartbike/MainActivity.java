package cl.minnd.android.smartbike;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import cl.minnd.android.smartbike.fragments.PlaceholderFragment;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
