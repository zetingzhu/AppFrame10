package com.zzt.zt_groupfragment.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.zzt.zt_groupfragment.R
import com.zzt.zt_groupfragment.act.ActTabV2
import com.zzt.zt_groupfragment.databinding.FragmentSettingBinding
import com.zzt.zt_groupfragment.util.TimeUtils
import java.util.Date

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance(): SettingsFragment {
            val args = Bundle()
            val fragment = SettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var binding: FragmentSettingBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnAdd?.setOnClickListener {
            var newFrag = AddFragment.newInstance(" C > ", TimeUtils.date2String(Date()))
            (activity as? ActTabV2)?.addFragment(newFrag)
        }
        binding?.btnNightSystem?.setOnClickListener {
            // 设置为跟随系统
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        binding?.btnLight?.setOnClickListener {
            // 强制禁用深色模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


        binding?.btnNight?.setOnClickListener {
            // 强制启用深色模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

    }
}