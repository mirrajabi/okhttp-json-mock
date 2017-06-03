package ir.mirrajabi.okhttpjsonmock.sample.services;

import java.util.ArrayList;

import io.reactivex.Observable;
import ir.mirrajabi.okhttpjsonmock.sample.models.UserModel;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsersService {
    String API_VERSION = "api/v1";

    //usage example /users/page=phoneNumbers.json
    @GET(API_VERSION + "/users")
    Observable<ArrayList<UserModel>> getUsers(@Query("page") int page);

    //usage example /users/page=1&secondParameter=phoneNumbers.json
    @GET(API_VERSION + "/users")
    Observable<ArrayList<UserModel>> getUsers(@Query("page") int page,
                                              @Query("name") String name);

    //usage example /users/1.json
    @GET(API_VERSION + "/users/{userId}")
    Observable<UserModel> getUser(@Path("userId") int userId);

    //usage example /users/1/phoneNumbers.json
    @GET(API_VERSION + "/users/{userId}/phoneNumbers")
    Observable<ArrayList<String>> getUserNumbers(@Path("userId") int userId);
}
