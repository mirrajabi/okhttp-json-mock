package ir.mirrajabi.okhttpjsonmock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.Random;

import ir.mirrajabi.okhttpjsonmock.helpers.ResourcesHelper;
import ir.mirrajabi.okhttpjsonmock.models.MockedResponse;
import ir.mirrajabi.okhttpjsonmock.providers.DefaultInputStreamProvider;
import ir.mirrajabi.okhttpjsonmock.providers.InputStreamProvider;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpMockInterceptor implements Interceptor {
    private final static String DEFAULT_BASE_PATH = "";
    private final static int DELAY_DEFAULT_MIN = 500;
    private final static int DELAY_DEFAULT_MAX = 1500;

    private int failurePercentage;
    private String basePath;
    private InputStreamProvider inputStreamProvider;
    private int minDelayMilliseconds;
    private int maxDelayMilliseconds;

    public OkHttpMockInterceptor(int failurePercentage) {
        this(new DefaultInputStreamProvider(), failurePercentage, DEFAULT_BASE_PATH,
                DELAY_DEFAULT_MIN, DELAY_DEFAULT_MAX);
    }

    public OkHttpMockInterceptor(InputStreamProvider inputStreamProvider, int failurePercentage) {
        this(inputStreamProvider, failurePercentage, DEFAULT_BASE_PATH,
                DELAY_DEFAULT_MIN, DELAY_DEFAULT_MAX);
    }

    public OkHttpMockInterceptor(
            InputStreamProvider inputStreamProvider,
            int failurePercentage,
            int minDelayMilliseconds,
            int maxDelayMilliseconds) {
        this(inputStreamProvider, failurePercentage, DEFAULT_BASE_PATH,
                minDelayMilliseconds, maxDelayMilliseconds);
    }

    public OkHttpMockInterceptor(
            InputStreamProvider inputStreamProvider,
            int failurePercentage,
            String basePath,
            int minDelayMilliseconds,
            int maxDelayMilliseconds) {
        this.inputStreamProvider = inputStreamProvider;
        this.failurePercentage = failurePercentage;
        this.basePath = basePath;
        this.minDelayMilliseconds = minDelayMilliseconds;
        this.maxDelayMilliseconds = maxDelayMilliseconds;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Gson gson = new GsonBuilder().setLenient().create();
        HttpUrl url = chain.request().url();
        String sym = "";
        String query = url.encodedQuery() == null ? "" : url.encodedQuery();
        if (!query.equals(""))
            sym = "/";
        String path = url.encodedPath() + sym + query;
        String responseString = ResourcesHelper.loadFileAsString(inputStreamProvider,
                basePath + path.substring(1) + ".json");
        if (responseString == null)
            responseString = ResourcesHelper.loadFileAsString(inputStreamProvider,
                    basePath + url.encodedPath().substring(1) + ".json");
        MockedResponse mockedResponse = new MockedResponse()
                .setResponse(new LinkedTreeMap())
                .setStatusCode(404);
        if (responseString != null) {
            try {
                mockedResponse = gson.fromJson(responseString, MockedResponse.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }
        JsonObject jsonObject = gson.toJsonTree(mockedResponse.getResponse()).getAsJsonObject();
        String result = jsonObject.toString();
        JsonArray items = jsonObject.getAsJsonArray("items");
        if (items != null)
            result = gson.toJson(items);
        try {
            Thread.sleep(Math.abs(new Random()
                    .nextInt() % (maxDelayMilliseconds - minDelayMilliseconds))
                    + minDelayMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean failure = Math.abs(new Random().nextInt() % 100) < failurePercentage;
        int statusCode = failure ? 504 : mockedResponse.getStatusCode(); /*504 or 408*/
        if (failure)
            System.out.print("JsonMockServer: Returning result from " +
                    path + "\t\tStatusCode : " + statusCode);
        else
            System.out.print("JsonMockServer: Returning result from " +
                    path + "\t\tStatusCode : " + statusCode);

        return new Response.Builder()
                .code(statusCode)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), result))
                .addHeader("content-type", "application/json")
                .build();
    }

    public int getFailurePercentage() {
        return failurePercentage;
    }

    public OkHttpMockInterceptor setFailurePercentage(int failurePercentage) {
        this.failurePercentage = failurePercentage;
        return this;
    }

    public String getBasePath() {
        return basePath;
    }

    public OkHttpMockInterceptor setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public int getMinDelayMilliseconds() {
        return minDelayMilliseconds;
    }

    public OkHttpMockInterceptor setMinDelayMilliseconds(int minDelayMilliseconds) {
        this.minDelayMilliseconds = minDelayMilliseconds;
        return this;
    }

    public int getMaxDelayMilliseconds() {
        return maxDelayMilliseconds;
    }

    public OkHttpMockInterceptor setMaxDelayMilliseconds(int maxDelayMilliseconds) {
        this.maxDelayMilliseconds = maxDelayMilliseconds;
        return this;
    }
}