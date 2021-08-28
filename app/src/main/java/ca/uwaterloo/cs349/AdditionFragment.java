package ca.uwaterloo.cs349;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.security.Key;

public class AdditionFragment extends Fragment {

    private SharedViewModel mViewModel;
    DrawingView drawing;
    private Button addButton;
    private Button clearButton;

    private ViewGroup viewGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        View root = inflater.inflate(R.layout.fragment_addition, container, false);
        addButton = (Button) root.findViewById(R.id.addButton);
        clearButton = (Button) root.findViewById(R.id.clearButton);

        viewGroup = (ViewGroup) root;
        drawing = new DrawingView(this.getContext(), mViewModel, SharedViewModel.Mode.ADD);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1500, 2000);
        params.topMargin = 250;
        viewGroup.addView(drawing, params);

        initButtons();

        return viewGroup;
    }

    private void initButtons() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Gesture gesture = drawing.getGesture();
                // https://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
                if (gesture != null) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setTitle("Add Gesture");
                    final EditText gestureName = new EditText(getContext());
                    gestureName.setText("Gesture " + (mViewModel.getGestures().size() + 1));
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    alertBuilder.setView(gestureName);
                    alertBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = gestureName.getText().toString();
                            gesture.setName(name);
                            mViewModel.addGesture(gesture);

                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    });

                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }

                drawing.reset();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawing.reset();
            }
        });
    }


}