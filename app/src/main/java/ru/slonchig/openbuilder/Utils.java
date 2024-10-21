package ru.slonchig.openbuilder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public boolean hasPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public void requestPermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", activity.getPackageName())));
                activity.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, requestCode);
            }
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    requestCode);
        }
    }

    public void copyFile(Context context, String inputFile, String outputFile) throws IOException {
        File file = new File(outputFile);

        InputStream inputStream = context.getAssets().open(inputFile);
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        int len;
        byte[] buffer = new byte[8192];

        while((len = inputStream.read(buffer)) >= 0) {
            fileOutputStream.write(buffer, 0, len);
        }

        inputStream.close();
        fileOutputStream.close();
    }

    public void resize(String path, String outputPath, int width, int height) {
        Bitmap originalBitmap = BitmapFactory.decodeFile(path);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
        saveImage(resizedBitmap, outputPath);
    }

    private void saveImage(Bitmap bitmap, String outputPath) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void newToast(Context context, String log) {
        Toast.makeText(context, log, Toast.LENGTH_SHORT).show();
    }

    public void showAlert(Context context, String title, String text) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(text)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .show();
    }

    public int getNumCores() {
        return Runtime.getRuntime().availableProcessors();
    }
}
