package ru.slonchig.openbuilder.task;

import android.content.Context;

import com.codyi.xml2axml.Encoder;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ManifestTask {
    public void createManifest(Context context, String versionCode, String version, String packageName, String title, String path)
    throws IOException, XmlPullParserException {
        InputStream stream = null;
        stream = context.getAssets().open("manifest.xml");

        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();
        String manifestContent = new String(buffer);

        manifestContent = manifestContent.replace("SLONOAPP_VERSIONCODE", versionCode);
        manifestContent = manifestContent.replace("SLONOAPP_VERSIONNAME", version);
        manifestContent = manifestContent.replace("SLONOAPP_PACKAGE", packageName);
        manifestContent = manifestContent.replace("SLONOAPP_NAME", title);

        stream.close();

        // Компиляция манифеста
        Encoder encoder = new Encoder();

        byte[] compileManifest = encoder.encodeString(context, manifestContent);
        File file = new File(path + File.separator + "AndroidManifest.xml");

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(compileManifest);
        fileOutputStream.close();
    }
}