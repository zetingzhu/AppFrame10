package com.zzt.zt_groupfragment.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.zzt.zt_groupfragment.FragmentCallback
import com.zzt.zt_groupfragment.R
import com.zzt.zt_groupfragment.databinding.ActivityActTabV3Binding
import com.zzt.zt_groupfragment.frag.LoginFragment
import com.zzt.zt_groupfragment.frag.ScrollingFragment
import com.zzt.zt_groupfragment.frag.SettingsFragment
import com.zzt.zt_groupfragment.vp2.Vp2BaseFragmentStateAdapter
import com.zzt.zt_groupfragment.vp2.Vp2TabItemObj
import java.util.Stack


/**
 * Stack
 * empty() 测试这个堆栈是否为空。
 * peek() 查看堆栈顶部的对象，而不将其从堆栈中移除。
 * pop() 删除堆栈顶部的对象，并返回该对象作为此函数的值。
 * push(E item) 将物品推到此堆栈的顶部。
 * search(Object o) 返回对象在此堆栈上的基于1的位置。
 */
class ActTabV3 : AppCompatActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ActTabV3::class.java)
            context.startActivity(starter)
        }
    }

    val TAG = "ActTabV3"
    var CURRENT_FRAGMENT_TAG: String? = "current_fragment_tag" // 存储的 fragment

    val TAB_GROUP_A = "tabGroupA"
    val TAB_GROUP_B = "tabGroupB"
    val TAB_GROUP_C = "tabGroupC"
    var currentTabId: String? = TAB_GROUP_A // 默认选中分组

    var binding: ActivityActTabV3Binding? = null
    var fragmentList: MutableList<Vp2TabItemObj>? = null
    var adapter2: Vp2BaseFragmentStateAdapter? = null
    var savedFragmentStates: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityActTabV3Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        getFragByManager()


        adapter2 = Vp2BaseFragmentStateAdapter(this@ActTabV3)
        fragmentList = initTab()
        adapter2?.fragmentList = fragmentList

        initView()
        initViewpager2()

        // 处理 Activity 重建时 Fragment 的恢复
        if (savedInstanceState != null) {
            currentTabId = savedInstanceState.getString(CURRENT_FRAGMENT_TAG)
//            savedFragmentStates = savedInstanceState.getBundle("fragment_states")
//            savedFragmentStates?.let {
//                adapter2?.restoreState(it)
//            }
        }


    }

    private fun initViewpager2() {
        binding?.vp2Content?.apply {
            setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentTabId = fragmentList?.get(position)?.typeTAG
                    Log.d(TAG, "Vp2 选择 page pos:" + position + "  typeTag::" + currentTabId)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })
            adapter = adapter2
        }


        binding?.let {
            it.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    Log.d(TAG, "tabLayout 选择 tab pos:" + tab?.position)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            val tabLayoutMediator = TabLayoutMediator(
                it.tabLayout, it.vp2Content
            ) { tab, position ->

                val title = fragmentList?.get(position)?.title
                tab.setText(title)

            }
            tabLayoutMediator.attach()
        }
    }


    fun initTab(): MutableList<Vp2TabItemObj> {
        var fragmentList: MutableList<Vp2TabItemObj> = mutableListOf()

        fragmentList.add(Vp2TabItemObj("AAA", LoginFragment.newInstance(), TAB_GROUP_A, 1L))
        fragmentList.add(Vp2TabItemObj("BBB", ScrollingFragment.newInstance(), TAB_GROUP_B, 2L))
        fragmentList.add(Vp2TabItemObj("CCC", SettingsFragment.newInstance(), TAB_GROUP_C, 3L))

        return fragmentList
    }

    private fun initView() {

    }


    /**
     * 返回到指定 Fragment 栈的第一个 Fragment
     *
     * @param fragmentStack Stack<Fragment>
     */

    // 处理 Activity 重建时恢复 Fragment 栈
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_FRAGMENT_TAG, currentTabId)
    }

    fun getFragByManager() {
        supportFragmentManager.fragments?.forEachIndexed { index, fragment ->
            Log.d(
                TAG,
                "当前内容中的 frag index:" + index + " frag:" + fragment + " isAdd:" + fragment.isAdded + " isDetached" + fragment.isDetached
            )
        }
    }


}