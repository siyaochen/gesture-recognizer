package ca.uwaterloo.cs349;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    // Inspired by https://stackoverflow.com/questions/40862154/how-to-create-listview-items-button-in-each-row
    class MatchesAdapter extends BaseAdapter implements ListAdapter {

        ArrayList<Gesture> matches;

        public MatchesAdapter(ArrayList<Gesture> matches) {
            this.matches = matches;
        }

        @Override
        public int getCount() {
            return matches.size();
        }

        @Override
        public Object getItem(int position) {
            return matches.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup viewGroup = (ViewGroup) convertView;
            if (viewGroup == null) {
                viewGroup = (ViewGroup) inflater.inflate(R.layout.matches_layout, null);

                ThumbnailView thumbnail = new ThumbnailView(getContext(), mViewModel, matches.get(position).getPathThumbnail());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
                params.setMargins(30, 30, 10, 10);
                viewGroup.addView(thumbnail, params);

                String name = matches.get(position).getName();
                TextView nameDisplay = viewGroup.findViewById(R.id.gestureName);
                nameDisplay.setText(name);
            }

            return viewGroup;
        }
    }

    private SharedViewModel mViewModel;
    DrawingView drawing;
    private Button checkButton;
    private LayoutInflater inflater;
    private ViewGroup viewGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        checkButton = (Button) root.findViewById(R.id.checkButton);

        viewGroup = (ViewGroup) root;
        drawing = new DrawingView(this.getContext(), mViewModel, SharedViewModel.Mode.RECOGNIZE);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1500, 2000);
        params.topMargin = 250;
        viewGroup.addView(drawing, params);

        initButtons();

        return viewGroup;
    }

    private void initButtons() {
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Gesture gesture = drawing.getGesture();

                if (gesture != null) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setTitle("Gestures Matched");

                    ArrayList<Gesture> matches = mViewModel.matchGestures(gesture);
                    ListView gesturesList = new ListView(getContext());
                    gesturesList.setAdapter(new MatchesAdapter(matches));

                    alertBuilder.setView(gesturesList);

                    alertBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });

                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }

                drawing.reset();
            }
        });
    }

}