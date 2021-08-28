package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;

// This class was inspired by the sample code: 08.Android/2.Draw
public class DrawingView extends View {

    private SharedViewModel model;

    private Path path;
    private Paint paint;
    private ArrayList<Point> points;

    private SharedViewModel.Mode mode;

    public DrawingView(Context context, SharedViewModel model, SharedViewModel.Mode mode) {
        super(context);

        this.model = model;
        this.mode = mode;

        points = new ArrayList<>();
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(30);
        paint.setStyle(Paint.Style.STROKE);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // This method uses the structure of the documentation here: https://developer.android.com/training/gestures/detector.html
                int action = event.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        points.clear();
                        points.add(new Point(event.getX(), event.getY()));

                        path.reset();
                        path.moveTo(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        points.add(new Point(event.getX(), event.getY()));
                        path.lineTo(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        points.add(new Point(event.getX(), event.getY()));
                        path.lineTo(event.getX(), event.getY());
                        break;
                    default:
                        return false;
                }

                invalidate();
                return true;
            }
        });
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    public Gesture getGesture() {
        if (path.isEmpty()) return null;

        Gesture gesture = new Gesture(path);
        gesture.origPoints = (ArrayList<Point>) points.clone();

        return gesture;
    }

    public void reset() {
        points.clear();
        path.reset();
        invalidate();
    }

}

