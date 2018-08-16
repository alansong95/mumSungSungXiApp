package com.wootae.mumsungsungxi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by Alan on 8/8/2018.
 */


public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    public interface EditStudentRequestListener {
        void editStudentRequest(Student student);
        void openMessageDialog(Student student);
    }

    private EditStudentRequestListener mListener;

    private Context mContext;
    private List<Student> students;

    public StudentAdapter(Context context, List<Student> students) {
        this.mContext = context;
        this.students = students;

        try {
            this.mListener = (EditStudentRequestListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditStudentListener");
        }
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

//        Glide.with(mContext).load(R.drawable.default_profile).into(holder.thumbnail);
//        boolean temp = student == null ? true : false;
//        String temp2 = student.getPictureUri();
//        Log.d(TAG, "student is null: " + temp);
//        Log.d(TAG, "student's picture uri: " + temp2);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (student.getUpdatedDate().equals(date)) {
            if (student.getStatus().equals(StudentStatus.ARRIVED)) {
                holder.mCardView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
            } else if (student.getStatus().equals(StudentStatus.DEPARTED)) {
                holder.mCardView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.departed));
            } else if (student.getStatus().equals(StudentStatus.ABSENT)) {
                holder.mCardView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
            }
            holder.name.setTextColor(Color.parseColor("#FFFFFF"));
        } else {

        }

        if (student.getPictureUri().equals("")) {
            Glide.with(mContext).load(R.drawable.default_profile).into(holder.thumbnail);
        } else {
//            Picasso.with(mContext).load(student.getPictureUri()).placeholder(R.drawable.default_profile)
//                    .fit().centerInside().into(holder.thumbnail);
//            Picasso.with(mContext).load(student.getPictureUri()).placeholder(R.drawable.default_profile)
//                    .into(holder.thumbnail);;

//            Glide.with(mContext).load(student.getPictureUri()).fitCenter().into(holder.thumbnail);
            Glide.with(mContext).load(student.getPictureUri()).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).fitCenter().into(holder.thumbnail);
        }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.openMessageDialog((Student) holder.mCardView.getTag());
            }
        });

        holder.thumbnail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopupMenu(holder.thumbnail, (Student) holder.mCardView.getTag());
                return true;
            }
        });

//        holder.overflow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPopupMenu(holder.overflow, (Student) holder.mCardView.getTag());
//            }
//        });

//                holder.bind(items[position]); // commented b4
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView thumbnail;
//        public ImageView overflow;

        public CardView mCardView;

        public StudentViewHolder(View view) {
            super(view);

            name  = view.findViewById(R.id.tv_name);
//            overflow = view.findViewById(R.id.overflow);
            thumbnail = view.findViewById(R.id.thumbnail);

            mCardView = view.findViewById(R.id.card_view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.openMessageDialog((Student) mCardView.getTag());
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPopupMenu(view, (Student) mCardView.getTag());
                    return true;
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
                    mListener.editStudentRequest(student);
                    return true;
                case R.id.menu_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("삭제 알림").setMessage("정말 삭제 하시겠습니까?")
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MainActivity.deleteStudentFromDatabase(student);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // do nothing;
                                }
                            }).show();
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
}
