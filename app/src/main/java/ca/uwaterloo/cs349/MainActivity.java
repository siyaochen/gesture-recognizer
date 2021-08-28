package ca.uwaterloo.cs349;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    SharedViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // IMPORT
        mViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        try {
            FileInputStream fis = openFileInput("save.txt");
            Scanner in = new Scanner(fis);

            while (in.hasNextLine()) {
                String text = in.nextLine();
                String[] textArr = text.split(" ");

                Path path = new Path();
                ArrayList<Point> points = new ArrayList<>();
                String[] point = textArr[0].split(",");
                path.moveTo(Float.parseFloat(point[0]), Float.parseFloat(point[1]));
                points.add(new Point(Float.parseFloat(point[0]), Float.parseFloat(point[1])));
                int count = 1;
                while (!textArr[count].equals("end")) {
                    point = textArr[count].split(",");
                    path.lineTo(Float.parseFloat(point[0]), Float.parseFloat(point[1]));
                    points.add(new Point(Float.parseFloat(point[0]), Float.parseFloat(point[1])));
                    count++;
                }
                count++;

                Gesture g = new Gesture(path);
                g.origPoints = points;
                g.name = "";
                while (count < textArr.length) {
                    g.name += textArr[count] + " ";
                    count++;
                }
                g.name = g.name.substring(0, g.name.length() - 1);

                mViewModel.addGesture(g);
            }

            in.close();
        } catch (Exception e) {}

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_library, R.id.navigation_addition)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // SAVE
        String fileName = "save.txt";
        String contents = "";

        ArrayList<Gesture> gestures = mViewModel.getGestures();
        for (Gesture g : gestures) {
            contents += g + "\n";
        }

        // From source: https://www.androidauthority.com/how-to-store-data-locally-in-android-app-717190/#:~:text=Data%20can%20be%20cached%20in,your%20app's%20internal%20storage%20directory.
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(contents.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}