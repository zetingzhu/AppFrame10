package com.zzt.zt_groupfragment.act;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zzt.zt_groupfragment.R;
import com.zzt.zt_groupfragment.frag.Fragment1;
import com.zzt.zt_groupfragment.frag.Fragment2;
import com.zzt.zt_groupfragment.frag.ModeSwitchFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ActTabV4 extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ActTabV4.class);
        context.startActivity(starter);
    }

    private static final String TAG = ActTabV4.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] tabTitles = {"Fragment 1", "Fragment 2", "Mode Switch"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_tab_v4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ll_main), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });

        getFragByManager();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);

        fragmentList.add(new Fragment1());
        fragmentList.add(new Fragment2());
        fragmentList.add(new ModeSwitchFragment());

        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment createFragment(int position) {
                return fragmentList.get(position);
            }
        };

        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setText(tabTitles[position])).attach();

        getFragByManager();
    }


    public void getFragByManager() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                Log.d(TAG, "当前内容中的 frag index:" + i + " frag:" + fragment + " isAdd:" + fragment.isAdded() + " isDetached" + fragment.isDetached());
            }
        }
    }

}    