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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;



public class LibraryFragment extends Fragment {

    // Inspired by https://stackoverflow.com/questions/40862154/how-to-create-listview-items-button-in-each-row
    class CustomAdapter extends BaseAdapter implements ListAdapter {

        ArrayList<Gesture> gestures;
        Fragment fragment;

        public CustomAdapter(ArrayList<Gesture> gestures, Fragment fragment) {
            this.gestures = gestures;
            this.fragment = fragment;
        }

        @Override
        public int getCount() {
            return gestures.size();
        }

        @Override
        public Object getItem(int position) {
            return gestures.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup viewGroup = (ViewGroup) convertView;
            if (viewGroup == null) {
                viewGroup = (ViewGroup) inflater.inflate(R.layout.custom_layout, null);

                ThumbnailView thumbnail = new ThumbnailView(getContext(), mViewModel, gestures.get(position).getPathThumbnail());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 150);
                params.setMargins(30, 30, 10, 10);
                viewGroup.addView(thumbnail, params);

                String name = gestures.get(position).getName();
                TextView nameDisplay = viewGroup.findViewById(R.id.name);
                nameDisplay.setText(name);

                Button deleteButton = viewGroup.findViewById(R.id.deleteButton);
                final int pos = position;
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gestures.remove(pos);
                        getParentFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                        notifyDataSetChanged();
                    }
                });

                Button editButton = viewGroup.findViewById(R.id.editButton);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Gesture gesture = gestures.get(pos);

                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                        alertBuilder.setTitle("Edit Gesture");

                        final DrawingView drawing = new DrawingView(getContext(), mViewModel, SharedViewModel.Mode.RECOGNIZE);
                        alertBuilder.setView(drawing);

                        alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Gesture g = drawing.getGesture();
                                if (g != null) {
                                    g.setName(gesture.getName());
                                    gestures.set(pos, g);
                                    getParentFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                                    notifyDataSetChanged();
                                }
                            }
                        });
                        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog dialog = alertBuilder.create();
                        dialog.show();
                    }
                });
            }

            return viewGroup;
        }
    }

    private SharedViewModel mViewModel;
    private ListView listView;
    private LayoutInflater inflater;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_library, container, false);
        listView = (ListView) root.findViewById(R.id.gestureList);
        listView.setAdapter(new CustomAdapter(mViewModel.getGestures(), this));

        return root;
    }
}