# okhttp-json-mock
[![](https://jitpack.io/v/mirrajabi/okhttp-json-mock.svg?style=flat-square)](https://jitpack.io/#mirrajabi/okhttp-json-mock)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Okhttp%20Json%20Mock-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5239)

This simple library helps you mock your data for using with okhttp+retrofit in json format in just a few moves.
it forwards the requests to local json files and returns the data stored in them.

## Version 3.0 Notes:
`3.0` introduces breaking changes, since it removes the wrapper for mocked responses ([MockedResponse.java](https://github.com/mirrajabi/okhttp-json-mock/blob/2.0/okhttpjsonmock/src/main/java/ir/mirrajabi/okhttpjsonmock/models/MockedResponse.java)) and therefor does not alter the api anymore.
Data transfer objects are now accessed directly without embedding them into an additional json object. See the [Version 2.0 Documentation](https://github.com/mirrajabi/okhttp-json-mock/blob/2.0/README.md) for the old api.

## Version 2.0 Notes:
Since version `2.0` the dependency to android platform is removed so it will be useful for all your jvm-based projects, not just android. You can still use version `1.1.1` if you don't care.

### Usage
First add jitpack to your projects build.gradle file
```groovy
allprojects {
   	repositories {
   		...
   		maven { url "https://jitpack.io" }
   	}
}
```
Then add the dependency in modules build.gradle file
```groovy
dependencies {
    compile 'com.github.mirrajabi:okhttp-json-mock:3.0'
 }
```
**Since version 2.0**:
1. Construct your custom [InputStreamProvider](https://github.com/mirrajabi/okhttp-json-mock/blob/master/okhttpjsonmock/src/main/java/ir/mirrajabi/okhttpjsonmock/providers/InputStreamProvider.java):

```java
InputStreamProvider inputStreamProvider = new InputStreamProvider() {
    @Override
    public InputStream provide(String path) {
        try {
            return getAssets().open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
};
```

2. Use the `InputStreamProvider` to construct the `OkHttpMockInterceptor` and client:
```java
OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(new OkHttpMockInterceptor(getAndroidProvider(), 5))
    .build();
```

**For version 1.+**
#### 1. Add OkhttpMockInterceptor to your OkhttpClient instance and attach it to your retrofit instance
```java
OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
    .addInterceptor(new OkHttpMockInterceptor(this, 5))
    .build();
    
mRetrofit = new Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .baseUrl("http://example.com")
    .client(mOkHttpClient)
    .build();
```

#### 2. Prepare your api service interfaces for retrofit []()
```java
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
```

#### 3. Put your json models in assets folder like the [examples](https://github.com/mirrajabi/okhttp-json-mock/tree/master/app/src/main/assets)
```
\---api
    \---v1
        \---users
            |   1.json
            |   2.json
            |   3.json
            |   page=1.json
            |
            +---1
            |       phoneNumbers.json
            |
            +---2
            |       phoneNumbers.json
            |
            \---3
                    phoneNumbers.json
```

### Retrofit's annotations
Currently [@Query](https://square.github.io/retrofit/2.x/retrofit/retrofit2/http/Query.html) and [@Path](https://square.github.io/retrofit/2.x/retrofit/retrofit2/http/Path.html) can be achieved simply with correct folder and file namings (like website routes)
for example if you have a request like
```java
@GET("api/v1/posts/{userId}")
Observable<ArrayList<Post>> getUserPosts(@Path("userId"),
                                         @Query("page") int page,
                                         @Query("categoryId") int categoryId);
```
you can have json models in `api/v1/posts/{userId}` where `{userId}` could be an integer like `api/v1/posts/3`
and in that folder the json files should have names like `page=1&categoryId=5.json`
so multiple queries are achievable by seperating them using **Ampersand(&)** character 

You can take a look at [Sample app](https://github.com/mirrajabi/okhttp-json-mock/tree/master/app) for a working example

### Contributions

Any contributions are welcome. 
just fork it and submit your changes to your fork and then create a pull request

### Changelog

3.0 - `Removed wrapper for mocked responses`

2.0 - `The library no longer depends on android classes`

1.1.1 - `Fixes file name lowercase issue`

1.1 - `Adds delay customization option.`
