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
public class ThumbnailView extends View {

    private SharedViewModel model;

    private Path path;
    private Paint paint;

    private SharedViewModel.Mode mode;

    public ThumbnailView(Context context, SharedViewModel model, Path p) {
        super(context);

        this.model = model;

        path = p;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(7);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    public void setPath(Path p) {
        this.path = p;
        invalidate();
    }

}

