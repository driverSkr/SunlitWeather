package com.driverskr.sunlitweather.dialog

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.driverskr.lib.dialog.BaseDialogFragment
import com.driverskr.lib.utils.ApkInstallUtil
import com.driverskr.lib.utils.LogUtil
import com.driverskr.sunlitweather.R
import com.driverskr.sunlitweather.bean.VersionBean
import com.driverskr.sunlitweather.databinding.DialogUpgradeBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.File

class UpgradeDialog(private val version: VersionBean) :
    BaseDialogFragment<DialogUpgradeBinding>(0.8f, 0f) {

    lateinit var downloadManager: DownloadManager

    override fun bindView() = DialogUpgradeBinding.inflate(layoutInflater)

    override fun initView() {
        isCancelable = false

        mBinding.tvVersionName.text =
            requireContext().getString(R.string.new_version, version.versionName)
        mBinding.tvVersionDescribe.text = version.describe
        mBinding.tvCancel.visibility = if (version.isForce) View.GONE else View.VISIBLE
    }

    private var downloadId: Long = -1

    override fun initEvent() {
        // 取消
        mBinding.tvCancel.setOnClickListener { dismiss() }
        // 升级
        mBinding.tvUpdate.setOnClickListener {
            mBinding.llTip.visibility = View.GONE
            mBinding.llDownload.visibility = View.VISIBLE
            downloadId = startDownload(version.urlFull)
            monitorProgress()
        }

        // 后台下载
//        mBinding.tvInBackground.setOnClickListener { dismiss() }
    }

    @SuppressLint("Range")
    private fun monitorProgress() {
        var job: Job? = null
        job = lifecycleScope.launch {
            flow {
                while (isActive) {
                    val cursor =
                        downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                    if (cursor != null && cursor.moveToFirst()) {
                        when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_RUNNING -> {
                                val downloadProgress: Int =
                                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                val downloadAll: Int =
                                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                                val progress =
                                    ((downloadProgress.toFloat() / downloadAll * 100).toInt())
                                emit(progress)
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                emit(101)
                            }
                            DownloadManager.STATUS_FAILED -> {
                                emit(-1)
                            }
                        }
                        cursor.close()
                    }
                    delay(50)
                }
//            }.flowOn(Dispatchers.Main).catch { e ->

            }.collect {
                LogUtil.e("it: $it")
                when (it) {
                    -1 -> {
                        LogUtil.e("error ...")
                        resetView()
                        job?.cancelAndJoin()
                    }
                    101 -> {
                        mBinding.numberProgress.setProgress(100)
                        dismiss()
                        install()
                        LogUtil.d("finish ...")
                        job?.cancelAndJoin()
                    }
                    else -> {
                        mBinding.numberProgress.setProgress(it)
                    }
                }
            }
        }
    }

    private fun resetView() {
        mBinding.llTip.visibility = View.VISIBLE
        mBinding.llDownload.visibility = View.GONE
    }

    private fun install() {
        var downloadFileUri = downloadManager.getUriForDownloadedFile(downloadId)
//        LogUtil.e("downloadId：" + downloadId)
        downloadFileUri?.let {
            ApkInstallUtil.installApk(requireContext(), it)
        } ?: let {
            Toast.makeText(requireContext(), "下载失败，请重试", Toast.LENGTH_LONG).show()
            resetView()
        }
    }

    private fun startDownload(url: String): Long {
        val request = DownloadManager.Request(Uri.parse(url))
        //设置漫游条件下是否可以下载
        request.setAllowedOverRoaming(false)
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        //设置通知标题
        request.setTitle(requireContext().getString(R.string.app_name))
        //设置通知标题message
        request.setDescription(getString(R.string.downloading_new_version))
        request.setMimeType("application/vnd.android.package-archive")

        //设置文件存放路径
        val fileName = url.substringAfterLast("/")
        val file = File(requireContext().externalCacheDir, fileName)
        LogUtil.d("file path :" + file.absoluteFile)
        request.setDestinationUri(Uri.fromFile(file))

        downloadManager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        return downloadManager.enqueue(request)
    }
}