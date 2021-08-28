package ca.uwaterloo.cs349;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import java.util.ArrayList;

class Point {

    float x;
    float y;

    Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @NonNull
    @Override
    public String toString() {
        return x + "," + y;
    }
}

public class Gesture {

    private Path path;
    private Path normalizedPath;
    private ArrayList<Point> points;
    float angle;
    String name;
    double score = 0;
    ArrayList<Point> origPoints;

    public Gesture(Path path) {
        this.path = path;

        RectF bounds = new RectF();
        path.computeBounds(bounds, false);
        Point centroid = new Point(bounds.centerX(), bounds.centerY());

        Matrix matrix = new Matrix();

        PathMeasure pm = new PathMeasure(path, false);
        float[] coords = new float[2];
        pm.getPosTan(0, coords, null);

        // Rotation
        float x1 = coords[0] - centroid.x;
        float y1 = coords[1] - centroid.y;
        float x2 = (float) Math.sqrt(x1*x1 + y1*y1);
        float y2 = 0f;
        angle = (float) ((Math.atan2(y2,x2) - Math.atan2(y1, x1)) * 180 / Math.PI);
        if (angle < 0) angle = 360 + angle;
        matrix.preRotate((angle));

        // Scale
        float width = bounds.right - bounds.left;
        float height = bounds.bottom - bounds.top;
        float scalingDimension = Math.max(width, height);
        matrix.preScale(100 / scalingDimension, 100 / scalingDimension);

        // Translation
        matrix.preTranslate(-centroid.x, -centroid.y);

        normalizedPath = new Path();
        path.transform(matrix, normalizedPath);

        // Sample points, used similar idea as: https://stackoverflow.com/questions/7972780/how-do-i-find-all-the-points-in-a-path-in-android
        points = new ArrayList<>();
        PathMeasure normalizedPm = new PathMeasure(normalizedPath, false);
        float interval = normalizedPm.getLength() / 128;

        for (int i = 0; i < 128; i++) {
            normalizedPm.getPosTan(i * interval, coords, null);
            points.add(new Point(coords[0], coords[1]));
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean exists() {
        return points.size() > 0;
    }

    public Path getPathThumbnail() {
        Path newPath = new Path();

        Matrix matrix = new Matrix();
        matrix.preTranslate(75, 75);
        matrix.preRotate(360 - angle);

        normalizedPath.transform(matrix, newPath);

        return newPath;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    @NonNull
    @Override
    public String toString() {
        String text = "";
        for (Point p : origPoints) {
            text += p + " ";
        }
        text += "end ";
        text += name;

        return text;
    }
}
