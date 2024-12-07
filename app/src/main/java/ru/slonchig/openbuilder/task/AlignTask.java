package ru.slonchig.openbuilder.task;

import ru.maximoff.zipalign.ZipAligner;

public class AlignTask {
    String path;
    String output;

    public AlignTask(String path, String output) {
        this.path = path;
        this.output = output;
    }

    public boolean align() {
        return ZipAligner.align(path, output, ZipAligner.DEFAULT_LEVEL, true);
    }
}
