package ir.mirrajabi.okhttpjsonmock.interceptors;

import android.content.Context;
import android.util.Log;

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
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpMockInterceptor implements Interceptor {
    public final static String DEFAULT_BASE_PATH = "";
    public final static int DELAY_DEFAULT_MIN = 500;
    public final static int DELAY_DEFAULT_MAX = 1500;
    private Context mContext;
    private int mFailurePercentage;
    private String mBasePath;
    private int mMinDelayMilliseconds;
    private int mMaxDelayMilliseconds;

    public OkHttpMockInterceptor(Context context, int failurePercentage) {
        this(context, failurePercentage, DEFAULT_BASE_PATH,
                DELAY_DEFAULT_MIN, DELAY_DEFAULT_MAX);
    }

    public OkHttpMockInterceptor(Context context, int failurePercentage,
                                 int minDelayMilliseconds, int maxDelayMilliseconds) {
        this(context, failurePercentage, DEFAULT_BASE_PATH,
                minDelayMilliseconds, maxDelayMilliseconds);
    }

    public OkHttpMockInterceptor(Context context, int failurePercentage, String basePath,
                                 int minDelayMilliseconds, int maxDelayMilliseconds) {
        mContext = context;
        mFailurePercentage = failurePercentage;
        mBasePath = basePath;
        mMinDelayMilliseconds = minDelayMilliseconds;
        mMaxDelayMilliseconds = maxDelayMilliseconds;
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
        String responseString = ResourcesHelper.loadAssetTextAsString(mContext,
                mBasePath + path.substring(1).toLowerCase() + ".json");
        if (responseString == null)
            responseString = ResourcesHelper.loadAssetTextAsString(mContext,
                    mBasePath + url.encodedPath().substring(1).toLowerCase() + ".json");
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
                    .nextInt() % (mMaxDelayMilliseconds - mMinDelayMilliseconds))
                    + mMinDelayMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean failure = Math.abs(new Random().nextInt() % 100) < mFailurePercentage;
        int statusCode = failure ? 504 : mockedResponse.getStatusCode(); /*504 or 408*/
        if (failure)
            Log.e("JsonMockServer", "Returning result from " +
                    path + "\t\tStatusCode : " + statusCode);
        else
            Log.v("JsonMockServer", "Returning result from " +
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
        return mFailurePercentage;
    }

    public OkHttpMockInterceptor setFailurePercentage(int failurePercentage) {
        mFailurePercentage = failurePercentage;
        return this;
    }

    public String getBasePath() {
        return mBasePath;
    }

    public OkHttpMockInterceptor setBasePath(String basePath) {
        mBasePath = basePath;
        return this;
    }

    public int getMinDelayMilliseconds() {
        return mMinDelayMilliseconds;
    }

    public OkHttpMockInterceptor setMinDelayMilliseconds(int minDelayMilliseconds) {
        mMinDelayMilliseconds = minDelayMilliseconds;
        return this;
    }

    public int getMaxDelayMilliseconds() {
        return mMaxDelayMilliseconds;
    }

    public OkHttpMockInterceptor setMaxDelayMilliseconds(int maxDelayMilliseconds) {
        mMaxDelayMilliseconds = maxDelayMilliseconds;
        return this;
    }
}