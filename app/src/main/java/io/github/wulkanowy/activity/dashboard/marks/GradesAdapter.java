package io.github.wulkanowy.activity.dashboard.marks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.github.wulkanowy.R;

public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> list;

    public GradesAdapter(Context context, ArrayList<String> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public GradesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subject_item,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GradesAdapter.ViewHolder viewHolder, int i) {

        viewHolder.tv_android.setText(list.get(i));
        Picasso.with(context)
                .load(R.drawable.sample_0)
                .resize(240, 120)
                .noFade()
                .into(viewHolder.img_android);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_android;
        private ImageView img_android;

        public ViewHolder(View view) {
            super(view);

            tv_android = (TextView) view.findViewById(R.id.tv_android);
            img_android = (ImageView) view.findViewById(R.id.img_android);
        }
    }

}
