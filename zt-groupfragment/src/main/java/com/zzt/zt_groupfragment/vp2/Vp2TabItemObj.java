package com.zzt.zt_groupfragment.vp2;


import androidx.fragment.app.Fragment;

import java.io.Serializable;

/**
 * @author: zeting
 * @date: 2023/12/25
 * viewpager2 适配器的通用数据对象
 */
public class Vp2TabItemObj implements Serializable {

    private String title;
    private Fragment fragment;
    private String typeTAG;
    private Long page;

    public Vp2TabItemObj(String title, Fragment fragment, String typeTAG, Long page) {
        this.title = title;
        this.fragment = fragment;
        this.typeTAG = typeTAG;
        this.page = page;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getTypeTAG() {
        return typeTAG;
    }

    public void setTypeTAG(String typeTAG) {
        this.typeTAG = typeTAG;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "Vp2TabItemObj{" +
                "title='" + title + '\'' +
                ", typeTAG='" + typeTAG + '\'' +
                '}';
    }
}
