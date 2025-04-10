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
import com.google.android.material.tabs.TabLayout
import com.zzt.zt_groupfragment.FragmentCallback
import com.zzt.zt_groupfragment.R
import com.zzt.zt_groupfragment.databinding.ActivityActTabV2Binding
import java.util.Stack


/**
 * Stack
 * empty() 测试这个堆栈是否为空。
 * peek() 查看堆栈顶部的对象，而不将其从堆栈中移除。
 * pop() 删除堆栈顶部的对象，并返回该对象作为此函数的值。
 * push(E item) 将物品推到此堆栈的顶部。
 * search(Object o) 返回对象在此堆栈上的基于1的位置。
 */
class ActTabV2 : AppCompatActivity(), FragmentCallback {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ActTabV2::class.java)
            context.startActivity(starter)
        }
    }

    val TAG = "FragmentUtil"

//    val TAB_GROUP_A = "tabGroupA"
//    val TAB_GROUP_B = "tabGroupB"
//    val TAB_GROUP_C = "tabGroupC"


    // 使用栈来管理每个导航组的 Fragment
    private val groupAFragments = Stack<Fragment>()
    private val groupBFragments = Stack<Fragment>()
    private val groupCFragments = Stack<Fragment>()


    var CURRENT_FRAGMENT_TAG: String? = "current_fragment_tag" // 存储的 fragment
    var currentTabId: String? = FragmentTabGroupManager.TAB_GROUP_A // 默认选中分组
    var binding: ActivityActTabV2Binding? = null
    var tabGroupManager: FragmentTabGroupManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityActTabV2Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getFragByManager(Log.DEBUG)

        initView()

        tabGroupManager = FragmentTabGroupManager(
            supportFragmentManager,
            R.id.fl_content,
            object : FragmentTabGroupManager.TabGroupCallback {
                override fun checkTabCall(tabId: String?) {
                    currentTabId = tabId
                }
            })

        // 设置初始 Fragment
        if (savedInstanceState == null) {
            tabGroupManager?.switchTab(FragmentTabGroupManager.TAB_GROUP_A)
        } else {
            tabGroupManager?.restoreState(savedInstanceState)

            currentTabId = savedInstanceState.getString(CURRENT_FRAGMENT_TAG)
            currentTabId?.let {
                showTabCheckId(it)
                tabGroupManager?.showTabId(it)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        getFragByManager(Log.WARN)
    }

    private fun initView() {
        binding?.tabLayout?.apply {
            addTab(this.newTab().setText("AAA"))
            addTab(this.newTab().setText("BBB"))
            addTab(this.newTab().setText("CCC"))
        }

        binding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                when (position) {
                    0 -> {
                        tabGroupManager?.showTabId(FragmentTabGroupManager.TAB_GROUP_A)
                    }

                    1 -> {
                        tabGroupManager?.showTabId(FragmentTabGroupManager.TAB_GROUP_B)
                    }

                    2 -> {
                        tabGroupManager?.showTabId(FragmentTabGroupManager.TAB_GROUP_C)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                when (position) {
                    0 -> {
                        tabGroupManager?.showTabId(FragmentTabGroupManager.TAB_GROUP_A)
                    }

                    1 -> {
                        tabGroupManager?.showTabId(FragmentTabGroupManager.TAB_GROUP_B)
                    }

                    2 -> {
                        tabGroupManager?.showTabId(FragmentTabGroupManager.TAB_GROUP_C)
                    }
                }
            }
        })
    }

    private fun showTabCheckId(tabId: String?) {
        tabId?.let {
            when (tabId) {
                FragmentTabGroupManager.TAB_GROUP_A -> {
                    binding?.tabLayout?.getTabAt(0)?.select()
                }

                FragmentTabGroupManager.TAB_GROUP_B -> {
                    binding?.tabLayout?.getTabAt(1)?.select()
                }

                FragmentTabGroupManager.TAB_GROUP_C -> {
                    binding?.tabLayout?.getTabAt(2)?.select()
                }

                else -> {
                    tabGroupManager?.switchTab(FragmentTabGroupManager.TAB_GROUP_A)
                }
            }
        }
    }


    // 处理 Activity 重建时恢复 Fragment 栈
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        tabGroupManager?.saveState(outState)

        outState.putString(CURRENT_FRAGMENT_TAG, currentTabId)
    }


    override fun addFragment(fragment: Fragment?) {
        tabGroupManager?.addFragmentToCurrentGroup(fragment)
    }

    override fun backFragment() {
        tabGroupManager?.backFragmentByCurrentGroup()
    }

    fun getFragByManager(level: Int) {
        supportFragmentManager.fragments?.forEachIndexed { index, fragment ->
            val msgStr =
                "当前内容中的 frag index:" + index + " frag:" + fragment + " isAdd:" + fragment.isAdded + " isDetached: " + fragment.isDetached
            if (Log.DEBUG == level) {
                Log.d(
                    TAG, msgStr
                )
            } else if (Log.WARN == level) {
                Log.w(
                    TAG, msgStr
                )
            }
        }
    }

}