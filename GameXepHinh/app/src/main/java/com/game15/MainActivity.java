package com.game15;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Context;
import android.os.SystemClock;

public class MainActivity extends Activity 
{
	private MyView myView;
	private Context context;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		myView=new MyView(this);
        setContentView(myView);
		context=this;
    }

	@Override
	protected void onResume()
	{
		super.onResume();
		new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					while (!myView.win) {}
					Toast.makeText(context, "BAN THANG ROI !", Toast.LENGTH_LONG).show();
					SystemClock.sleep(3000);
					finish();
				}
			}).start();
	}
}


