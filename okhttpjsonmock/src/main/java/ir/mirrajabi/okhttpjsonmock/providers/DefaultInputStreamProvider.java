package ir.mirrajabi.okhttpjsonmock.providers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DefaultInputStreamProvider implements InputStreamProvider {
    @Override
    public InputStream provide(String path) {
        try {
            return new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
