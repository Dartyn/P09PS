package sg.edu.rp.webservices.p09ps;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    String folderLocation;
    Button btnStart,btnStop,btnCheck;
    TextView tv;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = (Button)findViewById(R.id.button);
        btnStop = (Button)findViewById(R.id.button2);
        btnCheck = (Button)findViewById(R.id.button3);
        tv = (TextView)findViewById(R.id.textView);

        createFolder();

/*

        */
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File targetFile = new File(folderLocation, "ldata.txt");
                try {
                    FileWriter writer = new FileWriter(targetFile, true);
                    writer.write("Hello world"+"\n");
                    writer.flush();
                    writer.close();

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Failed to write!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission() == true) {

                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {
                            try {
                                File targetFile = new File(folderLocation, "ldata.txt");
                                FileReader reader = new FileReader(targetFile);
                                BufferedReader br = new BufferedReader(reader);
                                String line = br.readLine();
                                String data = "";
                                while (line != null) {
                                    data += line + "\n";
                                    line = br.readLine();
                                    if (location != null) {
                                        String msg = "Lat : " + location.getLatitude() + " Lng : " + location.getLatitude();
                                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

                                    } else {
                                        String msg = "No last location";
                                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }

                                }
                                br.close();
                                reader.close();

                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to read!",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }

                        }
                    });
                }
                     else {
                        Toast.makeText(MainActivity.this, "Permission nOt granted", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                    }

                    client.getLastLocation();
                }

        });
    }

    private void createFolder() {
        int permissionCheck_Storage = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck_Storage != PermissionChecker.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            finish();
        }

        folderLocation = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/MyFolder";

        File folder = new File(folderLocation);
        if (folder.exists() == false){
            boolean result = folder.mkdir();
            if (result == true){
                Log.d("File Read/Write", "Folder created");
            } else {
                Log.e("File Read/Write", "Folder creation failed");
            }
        }
    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
