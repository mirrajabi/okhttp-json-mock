package ir.mirrajabi.okhttpjsonmock.models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

public class MockedResponse {
    @SerializedName("status")
    int statusCode;
    @SerializedName("response")
    LinkedTreeMap response;

    public LinkedTreeMap getResponse() {
        return response;
    }

    public MockedResponse setResponse(LinkedTreeMap response) {
        this.response = response;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public MockedResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}
