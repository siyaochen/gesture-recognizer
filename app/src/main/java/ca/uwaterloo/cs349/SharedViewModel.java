package ca.uwaterloo.cs349;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private ArrayList<Gesture> gestures;

    enum Mode {
        ADD,
        RECOGNIZE
    }

    public SharedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shared model");

        gestures = new ArrayList<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void addGesture(Gesture gesture) {
        gestures.add(gesture);
    }

    public ArrayList<Gesture> getGestures() {
        return gestures;
    }

    public ArrayList<Gesture> matchGestures(Gesture gesture) {
        ArrayList<Point> points = gesture.getPoints();

        ArrayList<Point> comparison;
        PriorityQueue<Gesture> best = new PriorityQueue<>(new Comparator<Gesture>() {
            @Override
            public int compare(Gesture a, Gesture b) {
                return a.score > b.score ? 1 : -1;
            }
        });

        for (Gesture g : gestures) {
            comparison = g.getPoints();

            double score = 0d;
            for (int i = 0; i < 128; i++) {
                double dx = Math.pow(points.get(i).x - comparison.get(i).x, 2);
                double dy = Math.pow(points.get(i).y - comparison.get(i).y, 2);
                score += Math.sqrt(dx + dy);
            }
            score /= 128;
            g.score = score;

            best.add(g);
        }

        ArrayList<Gesture> result = new ArrayList<>();
        if (best.size() > 0) result.add(best.poll());
        if (best.size() > 0) result.add(best.poll());
        if (best.size() > 0) result.add(best.poll());

        return result;
    }

}