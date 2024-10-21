package ru.slonchig.openbuilder;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class BinaryUtils {
    public String execute(String str, String directory) {
        String[] args = str.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            processBuilder.directory(new File(directory));
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            Scanner scanner = new Scanner(process.getErrorStream());

            while (true) {
                int read = inputStream.read();

                if (read == -1) {
                    break;
                }
                stringBuilder.append((char) read);
            }

            StringBuilder output = new StringBuilder();
            output.append(scanner.hasNext() ? scanner.next() : "");

            return output.toString();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setExecutable(File file) {
        String command = "chmod 777 ";

        if (file.exists()) {
            try {
                file.setExecutable(true, true);
                Runtime runtime = Runtime.getRuntime();

                StringBuilder stringBuilder = new StringBuilder(command);
                stringBuilder.append(file.getAbsolutePath());
                runtime.exec(stringBuilder.toString());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
