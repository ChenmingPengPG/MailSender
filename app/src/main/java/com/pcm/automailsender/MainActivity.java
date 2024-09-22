package com.pcm.automailsender;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.pcm.automailsender.model.AnalyseCallback;
import com.pcm.automailsender.model.MainReaderModel;
import com.pcm.automailsender.model.email.EmailSendModel;
import com.pcm.automailsender.model.ScoreInfoAnalyzeModel;
import com.pcm.automailsender.model.ScoreInfoAnalyzeModelV2;
import com.pcm.automailsender.common.ui.UiUtil;
import com.pcm.automailsender.ui.HighlightView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private AppBarConfiguration mAppBarConfiguration;

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Intent intent = new Intent(MainActivity.this, HighlightActivity.class);
//                startActivity(intent);
                View decorView = MainActivity.this.getWindow().getDecorView();
                ((ViewGroup) decorView).addView(new HighlightView(MainActivity.this));
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        initData();
    }

    private void initData() {
        Intent intent= getIntent();
        if (intent == null ) {
            return;
        }
        Uri uriFileShare;
        uriFileShare = intent.getData();
        if (uriFileShare != null){
            filePath =  uriFileShare.getPath();
            Log.d(TAG, "get file:" + filePath);
        }
        if (TextUtils.isEmpty(filePath)){
            Log.d(TAG, "get file is null");
            return;
        }
        if (!filePath.endsWith(".xlsx")) {
            Log.d(TAG, "get file:" + filePath);
            UiUtil.show("文件格式不支持。当前仅适配了xlsx文件读取");
            return;
        }
        MainReaderModel.getInstance().fileUri = uriFileShare;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}