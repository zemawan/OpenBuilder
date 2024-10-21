package ru.slonchig.openbuilder.task;

import android.content.Context;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;

public class TaskManager {
    private final Context context;
    private final String appName;
    private final String packageName;
    private final String versionName;
    private final String versionCode;
    private final String path;

    public TaskManager(Context context, String appName, String packageName, String versionName, String versionCode, String path) {
        this.context = context;
        this.appName = appName;
        this.packageName = packageName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.path = path;
    }

    public int runManifestTask() {
        ManifestTask manifestTask = new ManifestTask();

        try {
            manifestTask.createManifest(context, versionCode, versionName, packageName, appName, path);
            return 0;
        } catch (IOException | XmlPullParserException e) {
            return 1;
        }
    }

    public int runBuildJarTask() {
        JarTask jarTask = new JarTask();

        try {
            jarTask.buildClasses(context, packageName, path);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    public int runBuildDexTask() {
        DexTask dexTask = new DexTask();

        try {
            dexTask.compileDex(path);
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    public int runSignTask(String path) {
        SignTask signTask = new SignTask(path, path + File.separator + "testkey.pk8");
        String result = signTask.sign();

        if (result == "Done") {
            return 0;
        }

        return 1;
    }
}
