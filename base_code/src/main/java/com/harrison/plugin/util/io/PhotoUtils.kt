package com.harrison.plugin.util.io

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.harrison.plugin.util.developer.LogUtils
import com.harrison.plugin.util.ui.BitmapUtils
import com.yalantis.ucrop.UCrop
import java.io.File

/**
 *  处理本地图片获取流程工具
 */
class PhotoUtils(context: Context) {

    var callBack:((path: Uri, base64: String?)->Unit)? = null

    private var cameraSavePath: Uri //外部软件向当前软件提交文件的地址
    private var imageResultPath: Uri  //图片处理完成最终放置位置


    var output_size = 160  //图像输出大小
    companion object{
        const val tempImageName = "temporary_img.jpg" //获取到的图片缓存文件名
        const val imageResultName = "user_img.jpg"    //裁剪结束图片的文件名

        const val SELECT_FROM_PHOTO = 102   // 从相册中选择图片
        const val SELECT_FROM_CAMERA = 104  // 从相机中选择图片
        const val REQUEST_CAMERA_PERMISSION = 106   // 请求摄像头使用权限

    }

    init {
        var cameraOutputPath = File(context.cacheDir.toString())
        if (!cameraOutputPath.exists()) {
            cameraOutputPath.mkdir()
        }
        var cameraOutputFile = File(cameraOutputPath, tempImageName)
        cameraSavePath = FileProvider.getUriForFile(
            context, context.packageName + ".fileprovider",  // 如果跟换配置需要对当前"域名"进行调整
            cameraOutputFile
        )

        imageResultPath = Uri.fromFile(File(context.filesDir.path.toString() + File.separator + imageResultName))

    }

    /**
     * 在ActivityResult回掉中调用该函数
     */
    fun onActivityResult(activity: Activity, intent: Intent){

        var resultCode = intent.extras?.getInt("resultCode")
        var requestCode = intent.extras?.getInt("requestCode")
        if(requestCode == null)return

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION ->{
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(activity,"请允许软件调用摄像头为您服务",Toast.LENGTH_LONG).show()
                }else{
                    jumpToCamera(activity)
                }
            }
            SELECT_FROM_PHOTO -> {
                if (resultCode == Activity.RESULT_OK && intent?.data != null) {  //从相册选择照片不裁切
                    UCrop.of(intent?.data!!, imageResultPath)  //输入图像的data数据，编辑后输出到imageResultPath
                        .withAspectRatio(1.0f, 1.0f)
                        .withMaxResultSize(output_size, output_size)
                        .start(activity)
                }
            }
            SELECT_FROM_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    UCrop.of(cameraSavePath, imageResultPath)//输入图像的url，编辑后输出到imageResultPath
                        .withAspectRatio(1.0f, 1.0f)
                        .withMaxResultSize(output_size, output_size)
                        .start(activity)
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    callBack?.let {
                        it(imageResultPath, BitmapUtils.readUriToBase64(activity,imageResultPath))
                    }
                }
            }
        }
    }

    /**
     * 从相册中获取图片
     */
    fun requestPhotoFromAlbum(activity: Activity){
        val intent = Intent()
        intent.action = Intent.ACTION_PICK  //Pick an item from the data
        intent.type = "image/*"             //从所有图片中进行选择
        activity.startActivityForResult(intent, SELECT_FROM_PHOTO)
    }

    /**
     * 通过相机拍摄图片
     */
    fun requestPhotoFromCamera(activity: Activity){
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ){
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),REQUEST_CAMERA_PERMISSION)
        }else{
            jumpToCamera(activity)
        }
    }

    private  fun jumpToCamera(activity: Activity){
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE //设置Action为拍照
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            cameraSavePath
        ) //将拍取的照片保存到指定URI
        activity.startActivityForResult(intent, SELECT_FROM_CAMERA)
    }

    /**
     *  设置回掉
     */
    fun setCalBack(callBack: (path: Uri, base64: String?) -> Unit){
        this.callBack = callBack
    }

}