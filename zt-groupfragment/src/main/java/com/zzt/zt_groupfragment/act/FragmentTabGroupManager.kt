package com.zzt.zt_groupfragment.act

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.zzt.zt_groupfragment.frag.LoginFragment
import com.zzt.zt_groupfragment.frag.ScrollingFragment
import com.zzt.zt_groupfragment.frag.SettingsFragment
import java.util.Stack

/**
 * @author: zeting
 * @date: 2025/4/9
 *
 * 根据 TAB 给添加的 Fragment 进行分组
 */
class FragmentTabGroupManager {
    companion object {
        const val TAG = "FragmentTabGroupManager"
        const val TAB_GROUP_A = "tabGroupA"
        const val TAB_GROUP_B = "tabGroupB"
        const val TAB_GROUP_C = "tabGroupC"
    }


    var fragmentManager: FragmentManager? = null//  fragment 管理工具
    var fragContainerViewId: Int? = 0 // fragment 容器id
    var callback: TabGroupCallback? = null // 和页面交互

    var currentTabId: String? = TAB_GROUP_A // 默认选中分组

    // 使用栈来管理每个导航组的 Fragment
    private val groupAFragments = Stack<Fragment>()
    private val groupBFragments = Stack<Fragment>()
    private val groupCFragments = Stack<Fragment>()

    constructor(
        fragmentManager: FragmentManager?, containerViewId: Int?,
        callback: TabGroupCallback
    ) {
        this.fragmentManager = fragmentManager
        this.fragContainerViewId = containerViewId
        this.callback = callback
    }


    fun showTabId(tabId: String?) {
        tabId?.let {
            when (tabId) {
                TAB_GROUP_A -> {
                    if (TAB_GROUP_A == currentTabId) {
                        popToFirstFragment(groupAFragments)
                    } else {
                        switchTab(TAB_GROUP_A)
                    }
                }

                TAB_GROUP_B -> {
                    if (TAB_GROUP_B == currentTabId) {
                        popToFirstFragment(groupBFragments)
                    } else {
                        switchTab(TAB_GROUP_B)
                    }
                }

                TAB_GROUP_C -> {
                    if (TAB_GROUP_C == currentTabId) {
                        popToFirstFragment(groupCFragments)
                    } else {
                        switchTab(TAB_GROUP_C)
                    }
                }

                else -> {
                    switchTab(TAB_GROUP_A)
                }
            }
        }
    }

    /**
     * 根据分类来
     */
    fun switchTab(tabId: String) {

        if (tabId.isNullOrBlank()) return
        if (fragContainerViewId == null) {
            return
        }
        fragmentManager?.let { fm ->
            val transaction = fm.beginTransaction()

            // 隐藏当前显示的 Fragment (如果存在)
            val topFragByTab = getTopFragmentByTabId(currentTabId)
            topFragByTab?.let {
                transaction.hide(it)
                if (it.isAdded) {
                    transaction.setMaxLifecycle(it, Lifecycle.State.STARTED)
                }

                Log.i(TAG, " hide:" + it)
            }

            currentTabId = tabId
            callback?.checkTabCall(currentTabId)

            // 显示目标 Tab 对应的 Fragment
            var targetFragment: Fragment? = getTopFragmentByTabId(currentTabId)

            if (targetFragment != null) {
                transaction.show(targetFragment)
                if (targetFragment.isAdded) {
                    transaction.setMaxLifecycle(targetFragment, Lifecycle.State.RESUMED)
                }

                Log.w(TAG, " show:" + targetFragment)
            } else {
                var groupCount = 0;
                val newFragment = when (tabId) {
                    TAB_GROUP_A -> {
                        val fragment = LoginFragment.newInstance()
                        groupAFragments.push(fragment)

                        groupCount = groupAFragments.size
                        fragment
                    }

                    TAB_GROUP_B -> {
                        val fragment = ScrollingFragment.newInstance()
                        groupBFragments.push(fragment)

                        groupCount = groupBFragments.size
                        fragment
                    }

                    TAB_GROUP_C -> {
                        val fragment = SettingsFragment.newInstance()
                        groupCFragments.push(fragment)

                        groupCount = groupCFragments.size
                        fragment
                    }

                    else -> {
                        null
                    }
                }
                newFragment?.let {
                    val addTag = currentTabId + "_" + groupCount;
                    transaction.add(fragContainerViewId!!, newFragment, addTag)
                    transaction.setMaxLifecycle(newFragment, Lifecycle.State.RESUMED)
                    Log.d(TAG, " add:" + newFragment)
                }
            }
            transaction.commitNowAllowingStateLoss()
        }
    }

    /**
     * 返回到指定 Fragment 栈的第一个 Fragment
     *
     * @param fragmentStack Stack<Fragment>
     */
    private fun popToFirstFragment(fragmentStack: Stack<Fragment>?) {
        fragmentManager?.let { fm ->
            fragmentStack?.let { fragmentStack ->
                if (fragmentStack.size <= 1) return // 已经是第一个或者没有 Fragment
                val transaction = fm.beginTransaction()

                // 隐藏当前显示的 Fragment
                val topFragByTab = getTopFragmentByTabId(currentTabId)
                topFragByTab?.let {
                    transaction.hide(it)
                    if (it.isAdded) {
                        transaction.setMaxLifecycle(it, Lifecycle.State.STARTED)
                    }

                    Log.i(TAG, " hide:" + it)
                }

                // 移除栈顶的 Fragment 直到只剩第一个
                while (fragmentStack.size > 1) {
                    val fragmentToRemove: Fragment = fragmentStack.pop()
                    fragmentToRemove?.let {
                        transaction.remove(it)
                        if (it.isAdded) {
                            transaction.setMaxLifecycle(it, Lifecycle.State.CREATED)
                        }
                        Log.i(TAG, " remove:" + it)
                    }
                }

                // 显示栈底的第一个 Fragment
                if (fragmentStack.isNotEmpty()) {
                    val peekFrag = fragmentStack.peek()
                    transaction.show(peekFrag)
                    if (peekFrag.isAdded) {
                        transaction.setMaxLifecycle(peekFrag, Lifecycle.State.RESUMED)
                    }

                    Log.w(TAG, " show:" + peekFrag)
                }
                transaction.commitNowAllowingStateLoss()
            }
        }
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
        if (fragContainerViewId == null) {
            return
        }
        fragmentManager?.let { fm ->
            addFragment?.let { fragment ->
                val transaction = fm.beginTransaction()
                // 隐藏老的 Fragment
                val topFragByTab = getTopFragmentByTabId(currentTabId)
                topFragByTab?.let {
                    transaction.hide(it)
                    if (it.isAdded) {
                        transaction.setMaxLifecycle(it, Lifecycle.State.STARTED)
                    }

                    Log.i(TAG, " hide:" + it)
                }

                var groupCount = 0;
                when (currentTabId) {
                    TAB_GROUP_A -> {
                        groupAFragments.push(fragment)
                        groupCount = groupAFragments.size
                    }

                    TAB_GROUP_B -> {
                        groupBFragments.push(fragment)
                        groupCount = groupBFragments.size
                    }

                    TAB_GROUP_C -> {
                        groupCFragments.push(fragment)
                        groupCount = groupBFragments.size
                    }
                }

                // 添加新的 Fragment
                val addTag = currentTabId + "_" + groupCount;
                transaction.add(fragContainerViewId!!, fragment, addTag)
                transaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)

                transaction.commitNowAllowingStateLoss()

                Log.d(TAG, " add:" + fragment)
            }
        }
    }

    /**
     * 当前分组往上退出有个页面
     */
    fun backFragmentByCurrentGroup() {
        fragmentManager?.let { fm ->
            val transaction = fm.beginTransaction()
            // 移除最上面的 Fragment
            val topFragByTab = getTopFragmentByTabId(currentTabId)
            topFragByTab?.let {
                transaction.remove(it)
                Log.e(TAG, " remove:" + it)
                val group = getGroupByTabId(currentTabId)
                group?.pop()
            }

            // 显示当前分组最上面的 Fragment
            var targetFragment: Fragment? = getTopFragmentByTabId(currentTabId)

            if (targetFragment != null) {
                transaction.show(targetFragment)
                if (targetFragment.isAdded) {
                    transaction.setMaxLifecycle(targetFragment, Lifecycle.State.RESUMED)
                }
                Log.w(TAG, " show:" + targetFragment)
            }
            transaction.commitNowAllowingStateLoss()
        }
    }

    fun getGroupByTabId(currentTabId: String?): Stack<Fragment>? {
        if (currentTabId?.isNotEmpty() == true) {
            when (currentTabId) {
                TAB_GROUP_A -> {
                    return groupAFragments
                }

                TAB_GROUP_B -> {
                    return groupBFragments
                }

                TAB_GROUP_C -> {
                    return groupCFragments
                }
            }
        }
        return null
    }

    /**
     * 保存所有可用的 fragment 状态
     * @param savedState Bundle
     */
    fun saveState(savedState: Bundle) {
        fragmentManager?.let { fm ->
            fm.fragments?.let {
                for (fragment in it) {
                    if (fragment != null && fragment.isAdded) {
                        val key = fragment.tag
                        key?.let {
                            if (isValidKey(key, TAB_GROUP_A)
                                || isValidKey(key, TAB_GROUP_B)
                                || isValidKey(key, TAB_GROUP_C)
                            ) {
                                fm.putFragment(savedState, key, fragment)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 恢复所有可用 fragment 状态
     * @param savedInstanceState Bundle?
     */
    fun restoreState(savedInstanceState: Bundle?) {
        fragmentManager?.let { fm ->
            savedInstanceState?.let { bundle ->
                if (bundle.classLoader == null) {
                    bundle.classLoader = javaClass.classLoader
                }
                for (key in bundle.keySet()) {
                    if (isValidKey(key, TAB_GROUP_A)) {
                        val fragment: Fragment? = fm.getFragment(bundle, key)
                        groupAFragments.push(fragment)
                    } else if (isValidKey(key, TAB_GROUP_B)) {
                        val fragment: Fragment? = fm.getFragment(bundle, key)
                        groupBFragments.push(fragment)
                    } else if (isValidKey(key, TAB_GROUP_C)) {
                        val fragment: Fragment? = fm.getFragment(bundle, key)
                        groupCFragments.push(fragment)
                    }
                }

                Log.e(
                    TAG, "看看还原的列表有没有问题 groupAFragments：" + groupAFragments
                            + "\ngroupBFragments:" + groupBFragments
                            + "\ngroupCFragments:" + groupCFragments
                )
            }
        }
    }

    fun isValidKey(key: String, prefix: String): Boolean {
        return key.startsWith(prefix) && key.length > prefix.length
    }


    public interface TabGroupCallback {
        fun checkTabCall(tabId: String?)
    }

}