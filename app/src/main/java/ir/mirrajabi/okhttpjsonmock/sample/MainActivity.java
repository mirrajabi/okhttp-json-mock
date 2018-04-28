package ir.mirrajabi.okhttpjsonmock.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ir.mirrajabi.okhttpjsonmock.InputStreamProvider;
import ir.mirrajabi.okhttpjsonmock.interceptors.OkHttpMockInterceptor;
import ir.mirrajabi.okhttpjsonmock.sample.models.UserModel;
import ir.mirrajabi.okhttpjsonmock.sample.services.UsersService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private UsersService mUsersService;

    private BaseQuickAdapter<UserModel, BaseViewHolder> mUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initializeComponents();
    }

    private void initializeComponents() {
        InputStreamProvider androidInputStreamProvider = path -> {
            try {
                return getAssets().open(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new OkHttpMockInterceptor(androidInputStreamProvider, 5))
                .build();
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://example.com")
                .client(mOkHttpClient)
                .build();
        mUsersService = mRetrofit.create(UsersService.class);
        mUsersAdapter = new BaseQuickAdapter<UserModel, BaseViewHolder>
                (R.layout.layout_users_adapter, new ArrayList<>()) {
            @Override
            protected void convert(BaseViewHolder viewHolder, UserModel user) {
                TextView txtId = viewHolder.getView(R.id.user_id);
                TextView txtName = viewHolder.getView(R.id.user_name);
                TextView txtLastName = viewHolder.getView(R.id.user_last_name);
                TextView txtAge = viewHolder.getView(R.id.user_age);
                TextView txtNumbers = viewHolder.getView(R.id.user_numbers);
                txtId.setText("Id : " + user.getId());
                txtName.setText("Name : " + user.getName());
                txtLastName.setText("LastName : " + user.getLastName());
                txtAge.setText("Age : " + user.getAge());
                String numbers = "";
                for (int i = 0; i < user.getPhoneNumbers().size(); i++)
                    numbers += user.getPhoneNumbers().get(i) + (i < user.getPhoneNumbers().size() - 1 ? "\n" : "");
                txtNumbers.setText("PhoneNumbers : " + numbers);
            }
        };
        mUsersService.getUsers(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .retry(3)
                .subscribe(userModels -> mUsersAdapter.addData(userModels),
                        throwable -> throwable.printStackTrace());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mUsersAdapter);
    }
}
