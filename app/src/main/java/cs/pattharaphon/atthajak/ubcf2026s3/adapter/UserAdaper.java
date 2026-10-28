package cs.pattharaphon.atthajak.ubcf2026s3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cs.pattharaphon.atthajak.ubcf2026s3.R;
import cs.pattharaphon.atthajak.ubcf2026s3.model.User;

public class UserAdaper extends RecyclerView.Adapter<UserAdaper.UserViewHolder> {
    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private final Context context;
    private final List<User> users;
    private final OnUserClickListener listener;

    public UserAdaper(Context context, List<User> users, OnUserClickListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        holder.itemView.setOnClickListener(view -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView username;
        final TextView email;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_user_card_username);
            email = itemView.findViewById(R.id.tv_user_card_email);
        }
    }
}
