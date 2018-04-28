package ir.mirrajabi.okhttpjsonmock.providers;

import java.io.InputStream;

public interface InputStreamProvider {
    InputStream provide(String path);
}
