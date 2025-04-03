package com.zzt.zt_downtosecondview.act;

/**
 * @author: zeting
 * @date: 2025/4/3
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.zzt.zt_downtosecondview.R;
import com.zzt.zt_downtosecondview.widget.PullToSecondFloorLayout;

import java.util.ArrayList;
import java.util.List;

public class PullMainActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, PullMainActivity.class);
        context.startActivity(starter);
    }

    private PullToSecondFloorLayout pullToSecondFloorLayout;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pullToSecondFloorLayout = findViewById(R.id.pullToSecondFloorLayout);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = generateData();
        adapter = new MyAdapter(dataList);
        recyclerView.setAdapter(adapter);

        pullToSecondFloorLayout.setOnSecondFloorActionListener(new PullToSecondFloorLayout.OnSecondFloorActionListener() {
            @Override
            public void onEnterSecondFloor() {
                Toast.makeText(PullMainActivity.this, "进入二楼", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLeaveSecondFloor() {
                Toast.makeText(PullMainActivity.this, "离开二楼", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> generateData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            data.add("Item " + i);
        }
        return data;
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> dataList;

        public MyAdapter(List<String> dataList) {
            this.dataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView textView;

            public ViewHolder(android.view.View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}