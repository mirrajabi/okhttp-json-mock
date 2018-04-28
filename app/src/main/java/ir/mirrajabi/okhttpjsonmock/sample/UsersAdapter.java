package ir.mirrajabi.okhttpjsonmock.sample;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import ir.mirrajabi.okhttpjsonmock.sample.models.UserModel;

public class UsersAdapter extends BaseQuickAdapter<UserModel, BaseViewHolder> {
    UsersAdapter() {
        super(R.layout.layout_users_adapter, new ArrayList<>());
    }

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
}
