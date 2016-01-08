package com.game15;

import android.view.View;
import android.content.Context;
import android.util.DisplayMetrics;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import android.view.MotionEvent;
import android.widget.*;

class MyView extends View
{
	private Bitmap grid;										// create 1 time only
	private Bitmap screen;										// create every starts
	private final Bitmap numbers[]=new Bitmap[16];				// create 1 time only
	private final byte array[][]=new byte[4][4];				// create every starts
	private int cord[]=new int[3];							// coordinate {x0, y0, STEP}
	private int arrayX, arrayY;									// coordinate of array, used by onTouchEvent()
	private boolean canMove;											// boolean, used by onTouchEvent()
	Context context;
	private float xDown, yDown;
	protected boolean win=false;
	
	public MyView(Context c)
	{
		super(c);
		context=c;
		grid=init(c, numbers, cord);				// call 1 time only
		screen=initArray(array, cord, grid, numbers);			// call every starts
	}

	private final Bitmap init(Context context, Bitmap[] numbers, int[] cord)
	{
		DisplayMetrics metric=context.getResources().getDisplayMetrics();
		final int WIDTH=metric.widthPixels, HEIGTH=metric.heightPixels;
		final int STEP=10+(WIDTH-50)/4, X_MAX=4*STEP;
		final int Y_MIN=(HEIGTH-WIDTH)/2, Y_MAX=Y_MIN+4*STEP+9;
		Paint paint=new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		grid=Bitmap.createBitmap(WIDTH, HEIGTH, Bitmap.Config.ARGB_8888);
		Canvas canvas=new Canvas(grid);
		
		// draw screen gridline
		for (int i=0; i<=X_MAX; i+=STEP)
		{
			canvas.drawRect(i, Y_MIN, i+10, Y_MAX+1, paint);			// vertical
			canvas.drawRect(0, i+Y_MIN, X_MAX+10, i+Y_MIN+10, paint);	//horizontal
		}
		
		// draw numbers		
		for (byte i=1; i<=15; ++i)
		{
			int id=context.getResources().getIdentifier("f"+i, "drawable", context.getPackageName());
			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), id);
			numbers[i]=Bitmap.createScaledBitmap(bitmap, STEP-10, STEP-10, false);
		}
		numbers[0]=Bitmap.createBitmap(STEP-10, STEP-10, Bitmap.Config.ARGB_8888);
		numbers[0].eraseColor(Color.GRAY);
		
		cord[0]=10; cord[1]=Y_MIN+10; cord[2]=STEP; 
		return grid;
	}

	private final Bitmap initArray(byte[][] array, int[] cord, Bitmap grid, Bitmap[] numbers)
	{
		// init array
		List<Byte> digits=new ArrayList<Byte>();
		for (byte i=0; i<=15; ++i) digits.add(i);
		Random random=new Random(System.currentTimeMillis());
		for (byte[] i:array)
			for (byte j=0; j<4; ++j)
				i[j]=digits.remove(random.nextInt(digits.size()));
		
		// init screen
		Bitmap screen=Bitmap.createBitmap(grid);
		Canvas canvas=new Canvas(screen);
		final int X0=cord[0], Y0=cord[1], STEP=cord[2];
		for (int i=0, x=X0; i<4; ++i, x+=STEP)
			for (int j=0, y=Y0; j<4; ++j, y+=STEP)
				canvas.drawBitmap(numbers[array[i][j]], x, y, null);
		
		return screen;
	}
	
	private final boolean isWinned()
	{
		byte temp=array[3][3]; array[3][3]=16;
		byte number=0;
		for (byte i=0; i<4; ++i)
			for (byte j=0; j<4; ++j)
				if (array[j][i]!=(++number)) { array[3][3]=temp; return false; }

		return true;
	}

	private final int[] findRectNArray(int x, int y, final int Y0, final int STEP)
	{
		x=(x-10)/STEP; y=(y-Y0+10)/STEP;
		if ( (0<=x && x<=3) && (0<=y && y<=3) ) return new int[] { x,y };
		return new int[] { -1, -1 };
	}

	private final void move(final int I, final int J, final int VECTO_X, final int VECTO_Y)
	{
		Canvas canvas=new Canvas(screen);
		final int I2=I+VECTO_X, J2=J+VECTO_Y;
		if (I2<0 || I2>3 || J2<0 || J2>3 || array[I2][J2]!=0) return;
		byte temp=array[I][J];
		array[I][J]=0; array[I2][J2]=temp;
		canvas.drawBitmap(numbers[0], cord[0]+I*cord[2], cord[1]+J*cord[2], null);
		canvas.drawBitmap(numbers[temp], cord[0]+I2*cord[2], cord[1]+J2*cord[2], null);
		invalidate();
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction()==MotionEvent.ACTION_DOWN)
		{
			xDown=event.getX(); yDown=event.getY();
			int temp[]=findRectNArray((int)xDown, (int)yDown, cord[1], cord[2]);
			if ((temp[0]!=-1) && (array[arrayX=temp[0]][arrayY=temp[1]]!=0)) canMove=true;
			else canMove=false;
		}
		else if (canMove && (event.getAction()==MotionEvent.ACTION_UP))
		{
			final float X=(event.getX()-xDown), Y=(event.getY()-yDown);
			int vectoX, vectoY;
			if (Math.abs(X)>Math.abs(Y))
				if (X>0) { vectoX=1; vectoY=0; }
				else { vectoX=-1; vectoY=0; }
			else
				if (Y>0) { vectoX=0; vectoY=1; }
				else { vectoX=0; vectoY=-1; }

			move(arrayX, arrayY, vectoX, vectoY);
			if (isWinned()) win=true;
		}
		
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawBitmap(screen, 0, 0, null);
	}
}
