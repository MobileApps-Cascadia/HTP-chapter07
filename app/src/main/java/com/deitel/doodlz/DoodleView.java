// DoodleView.java
// Main View for the Doodlz app.
package com.deitel.doodlz;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

// the main screen that is painted
public class DoodleView extends View 
{
   private static final String TAG = "DoodleView";

   // used to determine whether user moved a finger enough to draw again   
   private static final float TOUCH_TOLERANCE = 10;

   private Bitmap bitmap; // drawing area for display or saving
   private Canvas bitmapCanvas; // used to draw on bitmap
   private final Paint paintScreen; // used to draw bitmap onto screen
   private final Paint paintLine; // used to draw lines onto bitmap
   
   // Maps of current Paths being drawn and Points in those Paths
   private final Map<Integer, Path> pathMap = new HashMap<Integer, Path>(); 
   private final Map<Integer, Point> previousPointMap = 
      new HashMap<Integer, Point>();
   
   // used to hide/show system bars 
   private GestureDetector singleTapDetector; 

   private Bitmap backgroundBitmap;
   private Paint backgroundBitmapPaint;   // paint for image background
   private Paint backgroundColorPaint;    // paint for colored background
   private boolean bCircle = false;
   private boolean bRectangle = false;
   private Point beginPoint;
   private Point endPoint;
   private boolean bFilled = false;
   private int backgroundRadioButtonID;

   // DoodleView constructor initializes the DoodleView
   public DoodleView(Context context, AttributeSet attrs)  
   {
      super(context, attrs); // pass context to View's constructor 
      paintScreen = new Paint(); // used to display bitmap onto screen

      backgroundBitmapPaint = new Paint();
      backgroundBitmapPaint.setAlpha(90);

      backgroundColorPaint = new Paint();
      backgroundColorPaint.setColor(getResources().getColor(android.R.color.holo_green_light));
      //backgroundColorPaint.setColor(Color.CYAN);

      // set the initial display settings for the painted line
      paintLine = new Paint();
      paintLine.setAntiAlias(true); // smooth edges of drawn line
      paintLine.setColor(Color.BLACK); // default color is black
      paintLine.setStyle(Paint.Style.STROKE); // solid line
      paintLine.setStrokeWidth(5); // set the default line width
      paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends
      
      // GestureDetector for single taps
      singleTapDetector = 
         new GestureDetector(getContext(), singleTapListener);

      // set backgroundRadioButtonID
      backgroundRadioButtonID = R.id.noBackgroundRadioButton;
   } 

   // Method onSizeChanged creates Bitmap and Canvas after app displays
   @Override 
   public void onSizeChanged(int w, int h, int oldW, int oldH)
   {
      bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
              Bitmap.Config.ARGB_8888);

      Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.smiley_face);

      backgroundBitmap = Bitmap.createScaledBitmap(tempBitmap, getWidth(), getHeight(), true);

      bitmapCanvas = new Canvas(bitmap);
      bitmap.eraseColor(Color.WHITE); // erase the Bitmap with white
   } 
   
   // clear the painting
   public void clear()
   {
      pathMap.clear(); // remove all paths
      previousPointMap.clear(); // remove all previous points
      bitmap.eraseColor(Color.WHITE); // clear the bitmap 
      invalidate(); // refresh the screen
   }
   
   // set the painted line's color
   public void setDrawingColor(int color) 
   {
      paintLine.setColor(color);
   } 

   // return the painted line's color
   public int getDrawingColor() 
   {
      return paintLine.getColor();
   }

   // set the painted line's width
   public void setLineWidth(int width) 
   {
      paintLine.setStrokeWidth(width);
   } 

   // return the painted line's width
   public int getLineWidth() 
   {
      return (int) paintLine.getStrokeWidth();
   } 

   // called each time this View is drawn
   @Override
   protected void onDraw(Canvas canvas) 
   {
      // draw the background screen
      canvas.drawBitmap(bitmap, 0, 0, paintScreen);

      if (backgroundRadioButtonID == R.id.yellowNotepadRadioButton ||
              backgroundRadioButtonID == R.id.smileyRadioButton ||
              backgroundRadioButtonID == R.id.beeRadioButton) {
         canvas.drawBitmap(backgroundBitmap, 0, 0, backgroundBitmapPaint);
      } else if (backgroundRadioButtonID == R.id.coloredBackgroundRadioButton) {
         backgroundColorPaint.setAlpha(90);
         canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundColorPaint);
      }

      // for each path currently being drawn
      for (Integer key : pathMap.keySet())
         canvas.drawPath(pathMap.get(key), paintLine); // draw line

   }

   // hide system bars and action bar
   public void hideSystemBars()
   {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
         setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | 
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE);
   }

   // show system bars and action bar
   public void showSystemBars()
   {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
         setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
   }

   // create SimpleOnGestureListener for single tap events
   private SimpleOnGestureListener singleTapListener =  
      new SimpleOnGestureListener()
      {
         @Override
         public boolean onSingleTapUp(MotionEvent e)
         {
            if ((getSystemUiVisibility() & 
               View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0)
               hideSystemBars();
            else
               showSystemBars();
            return true;
         }            
      };

   // handle touch event
   @Override
   public boolean onTouchEvent(MotionEvent event) 
   {
      // if a single tap event occurred on KitKat or higher device      
      if (singleTapDetector.onTouchEvent(event))
         return true;
      
      // get the event type and the ID of the pointer that caused the event
      int action = event.getActionMasked(); // event type 
      int actionIndex = event.getActionIndex(); // pointer (i.e., finger)
      
      // determine whether touch started, ended or is moving
      if (action == MotionEvent.ACTION_DOWN ||
         action == MotionEvent.ACTION_POINTER_DOWN) 
      {
         touchStarted(event.getX(actionIndex), event.getY(actionIndex), 
            event.getPointerId(actionIndex));
      } 
      else if (action == MotionEvent.ACTION_UP ||
         action == MotionEvent.ACTION_POINTER_UP) 
      {
         if (bRectangle || bCircle) {
            // collect the end point of the path
            endPoint = new Point();
            endPoint.set((int) event.getX(), (int) event.getY());
         }
         touchEnded(event.getPointerId(actionIndex));
      } 
      else 
      {
         touchMoved(event); 
      }
      
      invalidate(); // redraw
      return true;
   } // end method onTouchEvent

   // called when the user touches the screen
   private void touchStarted(float x, float y, int lineID) 
   {
      Path path; // used to store the path for the given touch id
      Point point; // used to store the last point in path

      if (bRectangle || bCircle) {
         beginPoint = new Point();
         beginPoint.set((int)x, (int)y);
      }

      // if there is already a path for lineID
      if (pathMap.containsKey(lineID)) 
      {
         path = pathMap.get(lineID); // get the Path
         path.reset(); // reset the Path because a new touch has started
         point = previousPointMap.get(lineID); // get Path's last point
      }
      else 
      {
         path = new Path(); 
         pathMap.put(lineID, path); // add the Path to Map
         point = new Point(); // create a new Point
         previousPointMap.put(lineID, point); // add the Point to the Map

      }

      // move to the coordinates of the touch
      path.moveTo(x, y);
      point.x = (int) x;
      point.y = (int) y;


   } // end method touchStarted

   // called when the user drags along the screen
   private void touchMoved(MotionEvent event) 
   {
      // for each of the pointers in the given MotionEvent
      for (int i = 0; i < event.getPointerCount(); i++) 
      {
         // get the pointer ID and pointer index
         int pointerID = event.getPointerId(i);
         int pointerIndex = event.findPointerIndex(pointerID);
            
         // if there is a path associated with the pointer
         if (pathMap.containsKey(pointerID)) 
         {
            // get the new coordinates for the pointer
            float newX = event.getX(pointerIndex);
            float newY = event.getY(pointerIndex);

            // get the Path and previous Point associated with
            // this pointer
            Path path = pathMap.get(pointerID);
            Point point = previousPointMap.get(pointerID);
            
            // calculate how far the user moved from the last update
            float deltaX = Math.abs(newX - point.x);
            float deltaY = Math.abs(newY - point.y);

            // if the distance is significant enough to matter
            if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) 
            {
               // move the path to the new location
               path.quadTo(point.x, point.y, (newX + point.x) / 2,
                  (newY + point.y) / 2);

               // store the new coordinates
               point.x = (int) newX;
               point.y = (int) newY;
            } 
         } 
      }
   } // end method touchMoved

   // called when the user finishes a touch
   private void touchEnded(int lineID) 
   {
      Log.d(TAG, "in touchEnded. lineID:" + lineID);

      Path path = pathMap.get(lineID); // get the corresponding Path
      if (bRectangle) {
         //draw rectangle based on the start and end touch
         Log.d(TAG, "*** Drawing a rectangle ***");
         Log.d(TAG, "beginPoint " + beginPoint.x + "-" + beginPoint.y + " endPoint " + endPoint.x + "-" +endPoint.y);

         drawRectangle();
      } else if (bCircle) {
         //draw circle based on the start and end touch
         Log.d(TAG, "*** Drawing a circle ***");
         Log.d(TAG, "beginPoint " + beginPoint.x + "-" + beginPoint.y + " endPoint " + endPoint.x + "-" +endPoint.y);

         drawCircle();
      }else {
            bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas

      }
      path.reset(); // reset the Path
   }

   private void drawRectangle() {
      int left = Math.min(beginPoint.x, endPoint.x);
      int top = Math.min(beginPoint.y, endPoint.y);
      int right = Math.max(beginPoint.x, endPoint.x);
      int bottom = Math.max(beginPoint.y, endPoint.y);

      if (bFilled) {
         paintLine.setStyle(Paint.Style.FILL);
      }

      bitmapCanvas.drawRect(left, top, right, bottom, paintLine);
      paintLine.setStyle(Paint.Style.STROKE);
   }

   private void drawCircle() {
      int xCenter = (beginPoint.x + endPoint.x) / 2;
      int yCenter = (beginPoint.y + endPoint.y) / 2;

      double radius = Math.sqrt(Math.pow(endPoint.x - beginPoint.x,2) + Math.pow(endPoint.y - beginPoint.y , 2))/2;
      Log.d(TAG, "Circle radius: " + radius);
      if (bFilled) {
         paintLine.setStyle(Paint.Style.FILL);
      }
      bitmapCanvas.drawCircle(xCenter, yCenter, (float) radius, paintLine);
      // reset paintLine to stroke
      paintLine.setStyle(Paint.Style.STROKE);
   }

   // save the current image to the Gallery
   public void saveImage()
   {
      // use "Doodlz" followed by current time as the image name
      String name = "Doodlz" + System.currentTimeMillis() + ".jpg";
      
      // insert the image in the device's gallery
      String location = MediaStore.Images.Media.insertImage(
              getContext().getContentResolver(), bitmap, name,
              "Doodlz Drawing");

      if (location != null) // image was saved
      {
         // display a message indicating that the image was saved
         Toast message = Toast.makeText(getContext(), 
            R.string.message_saved, Toast.LENGTH_SHORT);
         message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                 message.getYOffset() / 2);
         message.show();
      }
      else      
      {
         // display a message indicating that the image was saved
         Toast message = Toast.makeText(getContext(), 
            R.string.message_error_saving, Toast.LENGTH_SHORT);
         message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
            message.getYOffset() / 2);
         message.show(); 
      } 
   } // end method saveImage
   
   // print the current image
   public void printImage()
   {
      if (PrintHelper.systemSupportsPrint())
      {
         // use Android Support Library's PrintHelper to print image
         PrintHelper printHelper = new PrintHelper(getContext());
         
         // fit image in page bounds and print the image
         printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
         printHelper.printBitmap("Doodlz Image", bitmap); 
      }
      else
      {
         // display message indicating that system does not allow printing
         Toast message = Toast.makeText(getContext(), 
            R.string.message_error_printing, Toast.LENGTH_SHORT);
         message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
            message.getYOffset() / 2);
         message.show(); 
      }
   }

   public void setCircleMode(boolean bCircle) {
      this.bCircle = bCircle;
   }

   public void setRectangleMode(boolean bRectangle) {
      this.bRectangle = bRectangle;
   }

   public void setFilledMode(boolean bFilled) {
      this.bFilled = bFilled;
   }

   private Bitmap createScaledBitmap(int resID) {
      Bitmap tempBitmap = BitmapFactory.decodeResource(getResources(), resID);

      return Bitmap.createScaledBitmap(tempBitmap, getWidth(), getHeight(), true);
   }

   public void setBackground(int id) {
      backgroundRadioButtonID = id;

      if (id == R.id.yellowNotepadRadioButton) {
         backgroundBitmap = createScaledBitmap(R.drawable.yellow_notepad_drawable);
      } else if (id == R.id.smileyRadioButton) {
         backgroundBitmap = createScaledBitmap(R.drawable.smiley_face);
      } else if (id == R.id.beeRadioButton) {
         backgroundBitmap = createScaledBitmap(R.drawable.ic_launcher);
      }
   }

   public int getBackgroundRadioButtonID() {
      return backgroundRadioButtonID;
   }
} // end class DoodleView


/**************************************************************************
 * (C) Copyright 1992-2014 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 **************************************************************************/
