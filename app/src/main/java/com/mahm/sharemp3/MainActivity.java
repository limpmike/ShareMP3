package com.mahm.sharemp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String AUTHORITIES_NAME = "com.mahm.sharemp3.fileprovider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.copyButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File mp3File = new File(getCacheDirectory(),"hola.mp3");
                copyFileToCache(R.raw.hola,mp3File);
                Uri uri = FileProvider.getUriForFile(MainActivity.this, AUTHORITIES_NAME, mp3File);
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("audio/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Compartir audio .mp3"));
            }
        });
    }

    private void copyFileToCache(int resourceId,File file) {
        try {
            InputStream in = getResources().openRawResource(resourceId);
            FileOutputStream out = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage()!=null?e.getMessage():e.toString());
        }
    }

    private File getCacheDirectory(){
        //get cache dir path
        File cacheDir = getCacheDir();
        //get custom folder path inside cache dir
        File customCacheFolder = new File(cacheDir, "/mp3_cache/");
        if (!customCacheFolder.exists()) {
            //create custom folder inside cache dir
            boolean wasSuccessful = customCacheFolder.mkdirs();
            if (!wasSuccessful) {
                Log.e(TAG, "An error ocurred while creating cache dir");
            }
        }
        //for clearing files in cache
        File[] files = cacheDir.listFiles();
        if(files!=null){
            for (File file:files) {
                //delete file from custom cache folder
                boolean wasSuccessful = file.delete();
                if (!wasSuccessful) {
                    Log.e(TAG, "An error ocurred while deleting file: "+file.getName()
                            +" from: "+customCacheFolder.getPath());
                }
            }
        }

        return customCacheFolder;
    }
}
