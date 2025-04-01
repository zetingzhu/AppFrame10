package com.zzt.zt_groupfragment.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zzt.zt_groupfragment.R
import com.zzt.zt_groupfragment.act.ActTabV2
import com.zzt.zt_groupfragment.databinding.FragmentScrollingBinding
import com.zzt.zt_groupfragment.util.TimeUtils
import java.util.Date

class ScrollingFragment : Fragment() {

    companion object {
        fun newInstance(): ScrollingFragment {
            val args = Bundle()
            val fragment = ScrollingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    var binding: FragmentScrollingBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScrollingBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnAdd?.setOnClickListener {
           var newFrag = AddFragment.newInstance(" B > " , TimeUtils.date2String(Date()))
            (activity as? ActTabV2)?.addFragment(newFrag)
        }

    }
}