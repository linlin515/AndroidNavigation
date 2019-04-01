package com.navigation.toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.navigation.BaseFragment;
import com.navigation.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.listenzz.navigation.FragmentHelper;

/**
 * Created by Listen on 2018/2/1.
 */

public class PageFragment extends BaseFragment {

    private static final String ARG_TITLE = "title";

    public static PageFragment newInstance(String title) {
        PageFragment fragment = new PageFragment();
        Bundle args = FragmentHelper.getArguments(fragment);
        args.putString(ARG_TITLE, title);
        return fragment;
    }

    private PageAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        RecyclerView recyclerView =  view.findViewById(R.id.list);
        adapter = new PageAdapter(getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = FragmentHelper.getArguments(this);
        String title = args.getString(ARG_TITLE);
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            items.add(title);
        }
        adapter.setData(items);
    }

    static class PageAdapter extends RecyclerView.Adapter<PageAdapter.MyViewHolder> {

        private List<String> items = new ArrayList<>();
        private LayoutInflater inflater;

        private OnItemClickListener itemClickListener;

        public PageAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
        }

        public void setData(List<String> items) {
            this.items.clear();
            this.items.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_page, parent, false);
            final MyViewHolder holder = new MyViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(position, v);
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String item = items.get(position);
            holder.titleTextView.setText(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setOnItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        static class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView titleTextView;

            MyViewHolder(View itemView) {
                super(itemView);
                titleTextView =  itemView.findViewById(R.id.title);
            }
        }
    }

}
