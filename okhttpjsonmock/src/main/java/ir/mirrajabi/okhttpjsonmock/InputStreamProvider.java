package ir.mirrajabi.okhttpjsonmock;

import java.io.InputStream;

public interface InputStreamProvider {
    InputStream provide(String path);
}
