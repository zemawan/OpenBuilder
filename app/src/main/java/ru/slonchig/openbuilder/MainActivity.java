package ru.slonchig.openbuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = this;

        Utils utils = new Utils();
        Button button = (Button) findViewById(R.id.buildButton);

       button.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               if (utils.hasPermissions(context)) {
                   if (!Config.building) {
                       Builder builder = new Builder(context);

                       Config.setTitle("Test");
                       Config.setPackageName("com.openbuilder.test");
                       Config.setVersionCode("1");
                       Config.setVersionName("1.0");
                       Config.setPath("/storage/emulated/0/Solar2d/TestProject");
                       Config.setLuaCode("display.newText(\"Hello world\", 100, 100, nil, 64)");
                       Config.setIconPath("/storage/emulated/0/Solar2d/TestProject/icon.png");
                       
                       Config.setBuilding(true);
                       Runnable runnable = () -> {
                           builder.start();
                       };

                       Thread thread = new Thread(runnable);
                       thread.start();
                   }
               } else {
                   utils.requestPermissions(MainActivity.this, REQUEST_EXTERNAL_STORAGE);
               }
            }
        });
    }
}