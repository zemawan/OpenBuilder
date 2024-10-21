package ru.slonchig.openbuilder.task;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import ru.slonchig.openbuilder.Config;

public class CleanTask {
    public void clean() {
        Config.setBuilding(false);

        new File(Config.path + File.separator + "AndroidManifest.xml").delete();
        new File(Config.path + File.separator + "base.apk").delete();
        new File(Config.path + File.separator + "aligned.apk").delete();
        new File(Config.path + File.separator + "testkey.pk8").delete();
        new File(Config.path + File.separator + "testkey.x509.pem").delete();

        try {
            FileUtils.deleteDirectory(new File(Config.path + File.separator + "assets" + File.separator));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileUtils.deleteDirectory(new File(Config.path + File.separator + "res" + File.separator));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}