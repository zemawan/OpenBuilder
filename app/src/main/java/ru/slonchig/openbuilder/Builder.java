package ru.slonchig.openbuilder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.slonchig.openbuilder.task.AlignTask;
import ru.slonchig.openbuilder.task.CleanTask;
import ru.slonchig.openbuilder.task.TaskManager;

public class Builder {
    private final Context context;
    private android.os.Handler handler;
    private static final List<String> assets = new ArrayList<>();

    public Builder(Context context) {
        this.context = context;
    }

    private void newToast(String log) {
        Utils utils = new Utils();
        handler.post(() -> utils.newToast(context, log));
    }

    private void newAlert(String title, String log) {
        Utils utils = new Utils();
        handler.post(() -> utils.showAlert(context, title, log));
    }

    public static void addAssets(File[] files, String dir) {
        if (!dir.equals("")) {
            dir = dir + File.separator;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                addAssets(file.listFiles(), dir + file.getName());
            } else {
                if (!file.getName().equalsIgnoreCase("icon.png") && !file.getName().equalsIgnoreCase("main.lua")
                        && !file.getName().equalsIgnoreCase("game.apk") && !file.getName().equalsIgnoreCase("game.zip")) {

                    try {
                        FileUtils.copyFile(
                            new File(Config.path + File.separator + dir + file.getName()),
                            new File(Config.path + File.separator + "assets" + File.separator + dir + file.getName())
                        );

                        assets.add("assets" + File.separator + dir + file.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void start() {
        Utils utils = new Utils();
        CleanTask cleanTask = new CleanTask();
        StringBuilder stringBuilder = new StringBuilder();
        handler = new Handler(Looper.getMainLooper());
        TaskManager taskManager = new TaskManager(
                context, Config.title, Config.packageName, Config.versionName, Config.versionCode, Config.path
        );

        File path = new File(Config.path);
        if(!path.exists()) {
            path.mkdirs();
        }

        newToast("Building...");

        assets.clear();
        addAssets(new File(Config.path).listFiles(), "");

        stringBuilder.append("TASK: Creating android manifest...\n");
        if (taskManager.runManifestTask() != 0) {
            stringBuilder.append("ERROR: Compile AndroidManifest.xml failed!\n");
            newAlert("Ошибка сборки #1", stringBuilder.toString());
            cleanTask.clean();
            return;
        }

        try {
            stringBuilder.append("TASK: Using AAPT...\n");
            runAapt();
        } catch (Exception e) {
            e.printStackTrace();
            stringBuilder.append("ERROR: Failed to run AAPT!\n");
            newAlert("Ошибка сборки #6", stringBuilder.toString());
            cleanTask.clean();
            return;
        }

        AlignTask alignTask = new AlignTask(Config.path + File.separator + "base.apk",
                Config.path + File.separator + "aligned.apk");

        stringBuilder.append("TASK: ZipAlign...\n");
        if (!alignTask.align()) {
            stringBuilder.append("ERROR: Failed to run ZipAlign!\n");
            newAlert("Ошибка сборки #7", stringBuilder.toString());
            cleanTask.clean();
            return;
        }

        try {
            utils.copyFile(context, "testkey.pk8", Config.path + File.separator + "testkey.pk8");
            utils.copyFile(context, "testkey.x509.pem", Config.path + File.separator + "testkey.x509.pem");
            stringBuilder.append("TASK: Signed apk...\n");

            if (taskManager.runSignTask(Config.path) != 0) {
                stringBuilder.append("ERROR: Failed to sign apk!\n");
                newAlert("Ошибка сборки #9", stringBuilder.toString());
                cleanTask.clean();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            stringBuilder.append("ERROR: Failed to sign apk!\n");
            newAlert("Ошибка сборки #9", stringBuilder.toString());
            cleanTask.clean();
            return;
        }

        stringBuilder.append("TASK: Clean...");
        cleanTask.clean();
        newToast("Done!");
        newAlert("Сборка успешно завершена!", stringBuilder.toString());
    }

    private void runAapt() throws IOException {
        Utils utils = new Utils();
        File assetsPath = new File(Config.path + File.separator + "assets");

        if (!assetsPath.exists()) {
            assetsPath.mkdirs();
        }

        utils.copyFile(context, "slonoGame.apk", Config.path + File.separator + "base.apk");
        String aaptPath = context.getApplicationInfo().nativeLibraryDir + File.separator + "libaapt.so";

        BinaryUtils binaryUtils = new BinaryUtils();
        binaryUtils.setExecutable(new File(aaptPath));

        FileOutputStream fileOutputStream = new FileOutputStream(Config.path + File.separator + "assets" +
                File.separator + "program.gg");
        fileOutputStream.write(Config.luaCode.getBytes());
        fileOutputStream.close();

        for (String assetPath : assets) {
            binaryUtils.execute(
                    aaptPath + " add " + Config.path + File.separator + "base.apk " + assetPath, Config.path
            );
        }

        binaryUtils.execute(
                aaptPath + " remove " + Config.path + File.separator + "base.apk assets/program.gg", Config.path
        );
        binaryUtils.execute(
                aaptPath + " add " + Config.path + File.separator + "base.apk assets/program.gg", Config.path
        );
        binaryUtils.execute(
                aaptPath + " remove " + Config.path + File.separator + "base.apk AndroidManifest.xml", Config.path
        );
        binaryUtils.execute(
                aaptPath + " add " + Config.path + File.separator + "base.apk AndroidManifest.xml", Config.path
        );

        String resourcePath = Config.path + File.separator + "res" + File.separator;
        String[] icons = new String[]{"mipmap-hdpi-v4", "mipmap-ldpi-v4", "mipmap-mdpi-v4", "mipmap-xhdpi-v4",
                "mipmap-xxhdpi-v4", "mipmap-xxxhdpi-v4"};
        int[] iconsSize = new int[]{72, 36, 48, 96, 144, 192};

        try {
            if (!Config.iconPath.equals("")) {
                int index = 0;
                for (String folder : icons) {
                    String mipmapPath = resourcePath + folder + File.separator;
                    File file = new File(resourcePath + folder + File.separator);
                    if (file.exists()) {
                        file.mkdirs();
                    }

                    FileUtils.copyFile(
                            new File(Config.iconPath),
                            new File(mipmapPath + "ic_launcher_foreground.png"));
                    FileUtils.copyFile(
                            new File(Config.iconPath),
                            new File(mipmapPath + "ic_launcher.png"));

                    utils.resize(Config.iconPath, mipmapPath + "ic_launcher.png",
                            iconsSize[index], iconsSize[index]);
                    utils.resize(Config.iconPath, mipmapPath + "ic_launcher_foreground.png",
                            iconsSize[index], iconsSize[index]);

                    binaryUtils.execute(
                            aaptPath + " remove " + Config.path + File.separator + "base.apk " +
                                    "res" + File.separator + folder + File.separator + "ic_launcher_foreground.png",
                            Config.path
                    );
                    binaryUtils.execute(
                            aaptPath + " add " + Config.path + File.separator + "base.apk " +
                                    "res" + File.separator + folder + File.separator + "ic_launcher_foreground.png",
                            Config.path
                    );
                    binaryUtils.execute(
                            aaptPath + " remove " + Config.path + File.separator + "base.apk " +
                                    "res" + File.separator + folder + File.separator + "ic_launcher.png",
                            Config.path
                    );
                    binaryUtils.execute(
                            aaptPath + " add " + Config.path + File.separator + "base.apk " +
                                    "res" + File.separator + folder + File.separator + "ic_launcher.png",
                            Config.path
                    );

                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}