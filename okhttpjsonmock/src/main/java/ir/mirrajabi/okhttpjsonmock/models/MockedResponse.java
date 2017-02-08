package ir.mirrajabi.okhttpjsonmock.models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

public class MockedResponse {
    @SerializedName("status")
    int mStatusCode;
    @SerializedName("response")
    LinkedTreeMap mResponse;

    public LinkedTreeMap getResponse() {
        return mResponse;
    }

    public MockedResponse setResponse(LinkedTreeMap response) {
        mResponse = response;
        return this;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public MockedResponse setStatusCode(int statusCode) {
        mStatusCode = statusCode;
        return this;
    }
}
