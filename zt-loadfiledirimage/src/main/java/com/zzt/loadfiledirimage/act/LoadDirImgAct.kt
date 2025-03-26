package com.zzt.loadfiledirimage.act

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.zzt.loadfiledirimage.R
import com.zzt.loadfiledirimage.databinding.ActivityLoadDirImgBinding
import com.zzt.loadfiledirimage.util.DownFileImgUtil
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Random

class LoadDirImgAct : AppCompatActivity() {
    val TAG = "FileDir-act"
    var binding: ActivityLoadDirImgBinding? = null

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, LoadDirImgAct::class.java)
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(starter)
        }
    }

    var imtArray = arrayOf(
        "vip_upgrade_v2.webp",
        "vip_upgrade_v3.webp",
        "vip_upgrade_v4.webp",
        "vip_upgrade_v5.webp",
        "vip_upgrade_v6.webp",
        "vip_upgrade_v7.webp"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoadDirImgBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }


    private fun initView() {
        binding?.btn?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        111
                    )
                } else {

                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        111
                    )
                } else {

                }
            }
        }
        binding?.btn1?.setOnClickListener {
            DownFileImgUtil.getInstance().copyExtFileToCacheDir("drawable.zip")
        }

        binding?.btn2?.setOnClickListener {
            // 切换显示
            findImaByName(imtArray, Random().nextInt(4))
        }

        binding?.btn3?.setOnClickListener {
            // 移动文件到 Download
            var extDownDir = getExternalFilesDir("")
            if (extDownDir?.exists() == true) {
                var zipFile = File(extDownDir, "drawable.zip")
                if (zipFile.exists()) {
                    var inputS = FileInputStream(zipFile)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        DownFileImgUtil.getInstance().downloadWrite(inputS, "AAA.Zip")
                    }
                }
            }
        }
        binding?.btn4?.setOnClickListener {
            // 读取 Download
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                DownFileImgUtil.getInstance().queryDownloads29()
            }
        }

        binding?.btn5?.setOnClickListener {
            // 扫描 Download
            DownFileImgUtil.getInstance().scanDownloadDir()
        }

        // 默认显示
        findImaByName(imtArray, Random().nextInt(4))

    }

    /**
     * vip_upgrade_v2.webp
     * @param imgName String
     */
    fun findImaByName(imtArray: Array<String>?, rInt: Int) {
        if (imtArray != null) {
            var extDownDir = getExternalFilesDir("res/drawable")
            if (extDownDir?.exists() == true) {
                var newImgArr = arrayOf(imtArray[rInt], imtArray[rInt + 1], imtArray[rInt + 2])

                newImgArr.forEachIndexed { index, s ->
                    var imgFile = File(extDownDir, s)
                    DownFileImgUtil.getInstance().LoadImgPath(baseContext, imgFile,
                        object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable>?
                            ) {
                                if (index == 0) {
                                    binding?.ivImg1?.setImageDrawable(resource)
                                } else if (index == 1) {
                                    binding?.ivImg2?.setImageDrawable(resource)
                                } else if (index == 2) {
                                    binding?.ivImg3?.setImageDrawable(resource)
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
                }
            }
        }
    }


}