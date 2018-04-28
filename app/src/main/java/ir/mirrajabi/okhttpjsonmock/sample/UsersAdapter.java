package ir.mirrajabi.okhttpjsonmock.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ir.mirrajabi.okhttpjsonmock.sample.models.UserModel;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    ArrayList<UserModel> users = new ArrayList<>();
    private Context context;
    UsersAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_users_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = users.get(position);
        holder.id.setText("Id : " + user.getId());
        holder.name.setText("Name : " + user.getName());
        holder.lastName.setText("LastName : " + user.getLastName());
        holder.age.setText("Age : " + user.getAge());
        String numbers = "";
        for (int i = 0; i < user.getPhoneNumbers().size(); i++)
            numbers += user.getPhoneNumbers().get(i) + (i < user.getPhoneNumbers().size() - 1 ? "\n" : "");
        holder.numbers.setText("PhoneNumbers : " + numbers);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addData(ArrayList<UserModel> userModels) {
        users.addAll(userModels);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView id;
        TextView name;
        TextView lastName;
        TextView age;
        TextView numbers;

        ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.user_id);
            name = itemView.findViewById(R.id.user_name);
            lastName = itemView.findViewById(R.id.user_last_name);
            age = itemView.findViewById(R.id.user_age);
            numbers = itemView.findViewById(R.id.user_numbers);
        }
    }
}
