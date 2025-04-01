package com.zzt.zt_groupfragment.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.zzt.zt_groupfragment.R
import com.zzt.zt_groupfragment.databinding.ActivityActTabFragmentBinding
import com.zzt.zt_groupfragment.frag.FullscreenFragment
import com.zzt.zt_groupfragment.frag.SettingsFragment
import com.zzt.zt_groupfragment.frag.LoginFragment
import com.zzt.zt_groupfragment.util.FragmentGroupManager


class ActTabFragment : AppCompatActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ActTabFragment::class.java)
            context.startActivity(starter)
        }
    }

    var binding: ActivityActTabFragmentBinding? = null
    var groupManager: FragmentGroupManager? = null

    var fragmentA1: Fragment? = null
    var fragmentB1: Fragment? = null
    var fragmentC1: Fragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityActTabFragmentBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

            }
        })

        val fragmentManager: FragmentManager = supportFragmentManager
        groupManager = FragmentGroupManager(fragmentManager)

        fragmentA1 = LoginFragment.newInstance()
        fragmentB1 = FullscreenFragment.newInstance()
        fragmentC1 = SettingsFragment.newInstance()
    }


    fun showTabA() {
        binding?.flContentGroupA?.visibility = View.VISIBLE
        binding?.flContentGroupB?.visibility = View.GONE
        binding?.flContentGroupC?.visibility = View.GONE

        groupManager?.addFragmentToGroup(
            "group_a",
            R.id.fl_content_group_a,
            fragmentA1,
            "tag_a1"
        )
    }

    fun showTabB() {
        binding?.flContentGroupA?.visibility = View.GONE
        binding?.flContentGroupB?.visibility = View.VISIBLE
        binding?.flContentGroupC?.visibility = View.GONE

        groupManager?.addFragmentToGroup(
            "group_b",
            R.id.fl_content_group_b,
            fragmentB1,
            "tag_b2"
        )
    }

    fun showTabC() {
        binding?.flContentGroupA?.visibility = View.GONE
        binding?.flContentGroupB?.visibility = View.GONE
        binding?.flContentGroupC?.visibility = View.VISIBLE

        groupManager?.addFragmentToGroup(
            "group_c",
            R.id.fl_content_group_c,
            fragmentC1,
            "tag_c1"
        )
    }
}