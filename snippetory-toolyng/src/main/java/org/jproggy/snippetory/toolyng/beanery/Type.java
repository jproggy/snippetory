package org.jproggy.snippetory.toolyng.beanery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class Type {
    private final String name;
    private final String packageName;

    public Type(String qualifiedClassName) {
        String[] parts = qualifiedClassName.split("\\.");
        StringBuilder packageHelper = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) packageHelper.append('.');
            packageHelper.append(parts[i]);
        }
        packageName = packageHelper.toString();
        name = parts[parts.length - 1];
    }

    public String getName() {
        return name;
    }

    public String getPackage() {
        return packageName;
    }

    @Override
    public String toString() {
        return packageName + '.' + name;
    }

    public Writer getTarget(String targetDir) throws IOException {
        File targetPath = new File(targetDir, toString().replace(".", "/") + ".java");
        targetPath.getParentFile().mkdirs();
        return new OutputStreamWriter(new FileOutputStream(targetPath), StandardCharsets.UTF_8);
    }
}
