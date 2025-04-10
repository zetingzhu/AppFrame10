package com.zzt.zt_groupfragment.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zzt.zt_groupfragment.act.ActTabV2
import com.zzt.zt_groupfragment.databinding.FragmentLoginBinding
import com.zzt.zt_groupfragment.util.TimeUtils
import java.util.Date

class LoginFragment : Fragment() {
    companion object {
        fun newInstance(): LoginFragment {
            val args = Bundle()
            val fragment = LoginFragment()
            fragment.arguments = args
            return fragment
        }
    }

    val TAG = LoginFragment::class.java.simpleName
    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {

            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE

        }

        binding.btnAdd.setOnClickListener {
            val newFrag = AddFragment.newInstance(" A > ", TimeUtils.date2String(Date()))
            (activity as? ActTabV2)?.addFragment(newFrag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "生命周期 Activity onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "生命周期 Activity onPause")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "生命周期 Activity onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "生命周期 Activity onStop")
    }
}