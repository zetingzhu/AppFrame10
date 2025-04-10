package com.zzt.zt_groupfragment.vp2;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.zzt.utilcode.util.StringUtils;

import java.util.Collection;
import java.util.List;


/**
 * @author: zeting
 * @date: 2023/12/25
 * viewpager2 适配器的通用处理
 */
public class Vp2BaseFragmentStateAdapter extends FragmentStateAdapter {
    private static final String TAG = Vp2BaseFragmentStateAdapter.class.getSimpleName();
    List<Vp2TabItemObj> fragmentList;

    public Vp2BaseFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public Vp2BaseFragmentStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public Vp2BaseFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    public void setFragmentList(List<Vp2TabItemObj> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public List<Vp2TabItemObj> getFragmentList() {
        return fragmentList;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (isNotListEmpty(fragmentList)) {
            return fragmentList.get(position).getFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return isNotListEmpty(fragmentList) ? fragmentList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        long itemId = super.getItemId(position);
        if (isNotListEmpty(fragmentList)) {
            itemId = fragmentList.get(position).getPage();
        }
        return itemId;
    }

    @Override
    public boolean containsItem(long itemId) {
        if (isNotListEmpty(fragmentList)) {
            for (int i = 0; i < fragmentList.size(); i++) {
                if (getItemId(i) == itemId) {
                    return true;
                }
            }
            return false;
        }
        return super.containsItem(itemId);
    }


    /**
     * 根据tag 查找fragment
     *
     * @param typeTag
     * @return
     */
    public Fragment findFragByTag(String typeTag) {
        if (isNotListEmpty(fragmentList) && !TextUtils.isEmpty(typeTag)) {
            for (int i = 0; i < fragmentList.size(); i++) {
                Vp2TabItemObj item = fragmentList.get(i);
                if (typeTag.equals(item.getTypeTAG())) {
                    return item.getFragment();
                }
            }
        }
        return null;
    }

    /**
     * 根据 tag 得到添加的fragment
     *
     * @param fragmentManager
     * @param tag
     * @return
     */
    public Fragment getFragmentByTag(@NonNull FragmentManager fragmentManager, String tag) {
        if (!StringUtils.isEmpty(tag) && isNotListEmpty(fragmentList)) {
            for (Vp2TabItemObj productTabItem : fragmentList) {
                if (tag.equals(productTabItem.getTypeTAG())) {
                    return fragmentManager.findFragmentByTag("f" + productTabItem.getPage());
                }
            }
        }
        return null;
    }

    /**
     * 根据当前选中下标，找到fragment
     *
     * @param fragmentManager
     * @return
     */
    public Fragment getFragmentByCurrentItem(@NonNull FragmentManager fragmentManager, @Nullable Integer currentItem) {
        if (currentItem != null && isNotEmptyDataForList(fragmentList, currentItem)) {
            Vp2TabItemObj productTabItem = fragmentList.get(currentItem);
            if (productTabItem != null) {
                return fragmentManager.findFragmentByTag("f" + productTabItem.getPage());
            }
        }
        return null;
    }

    /**
     * 判断list是否为空
     */
    public static boolean isListEmpty(Collection<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断list不为空
     */
    public static boolean isNotListEmpty(Collection<?> coll) {
        return !isListEmpty(coll);
    }

    /**
     * 列表某一条数据不为空
     */
    public static boolean isNotEmptyDataForList(Collection<?> coll, int index) {
        if (isListEmpty(coll)) {
            return false;
        } else {
            int size = coll.size();
            if (index < 0 || index >= size) {
                return false;
            }
        }
        return true;
    }
}
