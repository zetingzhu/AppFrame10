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
import com.zzt.zt_groupfragment.frag.FullscreenFragment
import com.zzt.zt_groupfragment.frag.SettingsFragment
import com.zzt.zt_groupfragment.frag.LoginFragment
import com.zzt.zt_groupfragment.frag.ScrollingFragment
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

    val TAB_GROUP_A = "tabGroupA"
    val TAB_GROUP_B = "tabGroupB"
    val TAB_GROUP_C = "tabGroupC"


    // 使用栈来管理每个导航组的 Fragment
    private val groupAFragments = Stack<Fragment>()
    private val groupBFragments = Stack<Fragment>()
    private val groupCFragments = Stack<Fragment>()


    var CURRENT_FRAGMENT_TAG: String? = "current_fragment_tag" // 存储的 fragment
    var currentTabId: String? = TAB_GROUP_A // 默认选中分组
    var fragmentContainerId = R.id.fl_content // 添加容器 id
    var binding: ActivityActTabV2Binding? = null


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


        // 设置初始 Fragment
        if (savedInstanceState == null) {
            switchTab(TAB_GROUP_A)
        } else {
            currentTabId = savedInstanceState.getString(CURRENT_FRAGMENT_TAG)
            // 根据 currentFragmentTag 恢复 Fragment
            val restoredFragment = supportFragmentManager.findFragmentByTag(currentTabId)
            if (restoredFragment != null && !restoredFragment.isAdded) {
                supportFragmentManager.beginTransaction()
                    .add(fragmentContainerId, restoredFragment, currentTabId)
                    .commit()
            }
        }


        // 处理 Activity 重建时 Fragment 的恢复
        if (savedInstanceState != null) {
            restoreFragmentStacks(savedInstanceState)
            selectCurrentTab()
        }


        initView()
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
                        showTabA()
                    }

                    1 -> {
                        showTabB()
                    }

                    2 -> {
                        showTabC()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                when (position) {
                    0 -> {
                        showTabA()
                    }

                    1 -> {
                        showTabB()
                    }

                    2 -> {
                        showTabC()
                    }
                }
            }
        })
    }

    private fun showTabA() {
        if (TAB_GROUP_A == currentTabId) {
            popToFirstFragment(groupAFragments)
        } else {
            switchTab(TAB_GROUP_A)
        }
    }

    private fun showTabB() {
        if (TAB_GROUP_B == currentTabId) {
            popToFirstFragment(groupBFragments)
        } else {
            switchTab(TAB_GROUP_B)
        }
    }

    private fun showTabC() {
        if (TAB_GROUP_C == currentTabId) {
            popToFirstFragment(groupCFragments)
        } else {
            switchTab(TAB_GROUP_C)
        }
    }


    private fun switchTab(tabId: String) {

        if (tabId.isNullOrBlank()) return

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // 隐藏当前显示的 Fragment (如果存在)
        val topFragByTab = getTopFragmentByTabId(currentTabId)
        topFragByTab?.let {
            transaction.hide(it)
            Log.i(TAG, " hide:" + it)
        }

        currentTabId = tabId

        // 显示目标 Tab 对应的 Fragment
        var targetFragment: Fragment? = getTopFragmentByTabId(currentTabId)

        if (targetFragment != null) {
            transaction.show(targetFragment)

            Log.w(TAG, " show:" + targetFragment)
        } else {
            val newFragment = when (tabId) {
                TAB_GROUP_A -> {
                    val fragment = LoginFragment.newInstance()
                    groupAFragments.push(fragment)
                    fragment
                }

                TAB_GROUP_B -> {
                    val fragment = ScrollingFragment.newInstance()
                    groupBFragments.push(fragment)
                    fragment
                }

                TAB_GROUP_C -> {
                    val fragment = SettingsFragment.newInstance()
                    groupCFragments.push(fragment)
                    fragment
                }

                else -> throw IllegalArgumentException("Unknown tab ID")
            }
            transaction.add(fragmentContainerId, newFragment, tabId)
//            transaction.addToBackStack(currentTabId) // 可选：添加到回退栈

            Log.d(TAG, " add:" + newFragment)
        }
        transaction.commit()
    }


    /**
     *  根据当前Tab 获取顶部 显示的Fragment
     */
    fun getTopFragmentByTabId(currentTabId: String?): Fragment? {
        if (currentTabId?.isNotEmpty() == true) {
            when (currentTabId) {
                TAB_GROUP_A -> {
                    if (groupAFragments.size >= 1) {
                        return groupAFragments.peek()
                    }
                }

                TAB_GROUP_B -> {
                    if (groupBFragments.size >= 1) {
                        return groupBFragments.peek()
                    }
                }

                TAB_GROUP_C -> {
                    if (groupCFragments.size >= 1) {
                        return groupCFragments.peek()
                    }
                }
            }
        }
        return null
    }


    /**
     * 在当前选中的导航组中添加新的 Fragment
     *
     * @param fragment Fragment
     */
    fun addFragmentToCurrentGroup(addFragment: Fragment?) {
        addFragment?.let { fragment ->
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            // 隐藏老的 Fragment
            val topFragByTab = getTopFragmentByTabId(currentTabId)
            topFragByTab?.let {
                transaction.hide(it)
                Log.i(TAG, " hide:" + it)
            }

            when (currentTabId) {
                TAB_GROUP_A -> {
                    groupAFragments.push(fragment)
                }

                TAB_GROUP_B -> {
                    groupBFragments.push(fragment)
                }

                TAB_GROUP_C -> {
                    groupCFragments.push(fragment)
                }
            }

            // 添加新的 Fragment
            val addTag = fragment.javaClass.name
            transaction.add(fragmentContainerId, fragment, addTag)
//            transaction.addToBackStack(currentTabId) // 添加到回退栈
            transaction.commit()

            Log.d(TAG, " add:" + fragment)
        }
    }

    /**
     * 返回到指定 Fragment 栈的第一个 Fragment
     *
     * @param fragmentStack Stack<Fragment>
     */
    private fun popToFirstFragment(fragmentStack: Stack<Fragment>?) {
        fragmentStack?.let { fragmentStack ->
            if (fragmentStack.size <= 1) return // 已经是第一个或者没有 Fragment

            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            // 隐藏当前显示的 Fragment
            val topFragByTab = getTopFragmentByTabId(currentTabId)
            topFragByTab?.let {
                transaction.hide(it)
                Log.i(TAG, " hide:" + it)
            }

            // 移除栈顶的 Fragment 直到只剩第一个
            while (fragmentStack.size > 1) {
                val fragmentToRemove: Fragment = fragmentStack.pop()
                val topTag = fragmentToRemove.javaClass.name
                fragmentManager.findFragmentByTag(topTag)?.let {
                    transaction.remove(it)
                }
            }

            // 显示栈底的第一个 Fragment
            if (fragmentStack.isNotEmpty()) {
                val peekFrag = fragmentStack.peek()
                transaction.show(peekFrag)
                Log.w(TAG, " show:" + peekFrag)
            }
            transaction.commit()
        }
    }

    // 处理 Activity 重建时恢复 Fragment 栈
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveFragmentStacks(outState)
        outState.putString(CURRENT_FRAGMENT_TAG, currentTabId)
    }

    private fun saveFragmentStacks(outState: Bundle) {
        outState.putSerializable("groupA", ArrayList(groupAFragments))
        outState.putSerializable("groupB", ArrayList(groupBFragments))
        outState.putSerializable("groupC", ArrayList(groupCFragments))
    }

    private fun restoreFragmentStacks(savedInstanceState: Bundle?) {
        (savedInstanceState?.getSerializable("groupA") as? ArrayList<Fragment>)?.let {
            groupAFragments.addAll(
                it
            )
        }
        (savedInstanceState?.getSerializable("groupB") as? ArrayList<Fragment>)?.let {
            groupBFragments.addAll(
                it
            )
        }
        (savedInstanceState?.getSerializable("groupC") as? ArrayList<Fragment>)?.let {
            groupCFragments.addAll(
                it
            )
        }
    }

    private fun selectCurrentTab() {

    }

    override fun addFragment(fragment: Fragment?) {
        addFragmentToCurrentGroup(fragment)
    }

}