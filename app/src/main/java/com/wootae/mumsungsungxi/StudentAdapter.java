package com.wootae.mumsungsungxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Alan on 8/8/2018.
 */


public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private Context mContext;
    private List<Student> students;

    public StudentAdapter(Context context, List<Student> students) {
        this.mContext = context;
        this.students = students;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StudentViewHolder holder, int position) {
//        final Student student = students.get(position);
        Student student = students.get(position);
        holder.name.setText(student.getName());
        holder.mCardView.setTag(student);

        Glide.with(mContext).load(R.drawable.default_profile).into(holder.thumbnail);

//        if (student.getProfileImageUrl().equals("")) {
//            Glide.with(mContext).load(student.getThumbnail()).into(holder.thumbnail);
//        } else {
//            Picasso.with(mContext).load(student.getProfileImageUrl()).placeholder(R.drawable.profile)
//                    .fit().centerCrop().into(holder.thumbnail);
//        }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openSMSDialog(student);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showPopupMenu(holder.overflow, student);
                showPopupMenu(holder.overflow, (Student) holder.mCardView.getTag());
            }
        });

//                holder.bind(items[position]); // commented b4
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView thumbnail;
        public ImageView overflow;

        public CardView mCardView;

        public StudentViewHolder(View view) {
            super(view);

            name  = view.findViewById(R.id.tv_name);
            overflow = view.findViewById(R.id.overflow);
            thumbnail = view.findViewById(R.id.thumbnail);

            mCardView = view.findViewById(R.id.card_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Student student = (Student) mCardView.getTag();
//                    openSMSDialog(student);
                }
            });
        }
    }

    private void showPopupMenu(View view, Student student) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_student, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(student));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        Student student;
        public MyMenuItemClickListener(Student student) {
            this.student = student;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_edit:
//                    Intent intent = new Intent(mContext, EditStudentActivity.class);
//                    intent.putExtra("uid", student.getUid());
//                    ((Activity) mContext).startActivityForResult(intent, MainActivity.EDIT_STUDNET_REQUEST);
                    Toast.makeText(mContext, "EDIT: " + student.getName() , Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.menu_delete:
                    Toast.makeText(mContext, "DELETE", Toast.LENGTH_SHORT).show();
                    MainActivity.deleteStudentFromDatabase(student);
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

//    public void openSMSDialog(Student student) {
//        Toast.makeText(mContext, student.getName(),Toast.LENGTH_SHORT).show();
//
//        SMSDialog classNameDialog = new SMSDialog();
//        Bundle args = new Bundle();
//        args.putString("uid", student.getUid());
//        classNameDialog.setArguments(args);
//        classNameDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), "sms dialog");
//    }
}
