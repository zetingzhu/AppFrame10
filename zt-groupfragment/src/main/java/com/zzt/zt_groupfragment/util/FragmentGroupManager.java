package com.zzt.zt_groupfragment.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zeting
 * @date: 2025/3/31
 */
public class FragmentGroupManager {

    private final FragmentManager fragmentManager;
    private final Map<String, List<Fragment>> fragmentGroups = new HashMap<>();

    public FragmentGroupManager(FragmentManager fm) {
        this.fragmentManager = fm;
    }


    public void addFragmentToGroup(String groupName, int containerId, Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(containerId, fragment, tag); // 添加到指定的容器
        transaction.commit();

        if (!fragmentGroups.containsKey(groupName)) {
            fragmentGroups.put(groupName, new ArrayList<>());
        }
        fragmentGroups.get(groupName).add(fragment);
    }

    public List<Fragment> getFragmentsInGroup(String groupName) {
        return fragmentGroups.getOrDefault(groupName, new ArrayList<>());
    }


}
