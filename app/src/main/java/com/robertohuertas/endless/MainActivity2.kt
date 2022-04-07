package com.robertohuertas.endless

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.Settings
import android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity2 : AppCompatActivity() {
    lateinit var compName: ComponentName
    lateinit var deviceManger: DevicePolicyManager
    private lateinit var mediaProjectionManager: MediaProjectionManager
    val LOG_TAG = "CheckAccessibleService"
    private val TAG = LOG_TAG
    var textView: TextView?= null
    var month:Int = 0
    var year:Int = 0
    var day:Int = 0
    val myCalendar = Calendar.getInstance()
    private val PREFS_KEY_intent = "myPreferncesintent"
    private val PASSWORD_KEY_intent = "myPreferncesintent"
    companion object{
        private val REQUEST_CODE = 100
        private val YOUR_REQUEST_CODE=20222
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 42
        private const val MEDIA_PROJECTION_REQUEST_CODE = 13}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        title = "KHÔNG TẮT ĐƯỢC ĐÂU 1 VÉ GỬI BA"
        val startButton =
            findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            startProjection()
            startButton.setEnabled(false)
        }

        Handler().postDelayed({ startButton.performClick() }, 0)
        startService(Intent(this@MainActivity2, TestAccessibleService2::class.java))




//        stopService(Intent(this@MainActivity2, TestAccessibleService::class.java))
        Handler().postDelayed({ stopService(Intent(this@MainActivity2, TestAccessibleService2::class.java)) }, 500)

    }
    var date =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

    private fun updateLabel() {

        val myFormat = "dd/MM/yy" //I  n which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editText.setText(sdf.format(myCalendar.time))

        val sharedPreferences = getSharedPreferences(
            PREFS_KEY_intent, MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(PASSWORD_KEY_intent, editText.text.toString())
        editor.commit()
    }
    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)
        DatePickerDialog(this@MainActivity2, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(this@MainActivity2, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
//                editText.setText(pickedDateTime)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }
    fun showTime(monthx: Int, dayx: Int) {
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(this@MainActivity2,
            { timePicker, selectedHour, selectedMinute ->
                textView?.setText("$selectedHour:$selectedMinute")
                month = selectedHour
                day = selectedMinute
            }, monthx, dayx, false
        ) //Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    // Request code for selecting a PDF document.
    open fun checkPermissionxxx() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("com.robertohuertas.endless222")
                )
                startActivity(intent)
            }
        }
    }
    open fun openFolder(): Unit {
        val location : String  = "/storage/emulated/0/Documents/CHAN/"
        val intent = Intent(Intent.ACTION_VIEW)
        val mydir = Uri.parse("$location")
        intent.setDataAndType(mydir, "image/*") // or use */*
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent)
    }
    fun openDirectory() {
        val dir: File
        // Choose a directory using the system's file picker.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            if (Build.VERSION_CODES.R > SDK_INT) {
                dir = File(
                    Environment.getExternalStorageDirectory().path
                            + "//CHAN"
                )
            } else {
                dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .path
                            + "//CHAN"
                )
            }
            val pickerInitialUri = Uri.parse(dir.toString())
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, YOUR_REQUEST_CODE)
    }
    fun openFile() {
        val dir: File
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "file/*"

            if (Build.VERSION_CODES.R > SDK_INT) {
                dir = File(
                    Environment.getExternalStorageDirectory().path
                            + "//CHAN"
                )
            } else {
                dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .path
                            + "//CHAN"
                )
            }
            val pickerInitialUri = Uri.parse(dir.toString())
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, YOUR_REQUEST_CODE)
    }
//    fun openDirectory() {
//        val dir: File
//        // Choose a directory using the system's file picker.
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
//            // Optionally, specify a URI for the directory that should be opened in
//            // the system file picker when it loads.
//            if (Build.VERSION_CODES.R > SDK_INT) {
//                dir = File(
//                    Environment.getExternalStorageDirectory().path
//                            + "//CHAN"
//                )
//            } else {
//                dir = File(
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//                        .path
//                            + "//CHAN"
//                )
//            }
//            val pickerInitialUri = Uri.parse(dir.toString()
//        )
////            setDataAndType(pickerInitialUri, "file/*")
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
//        }
//        intent.addFlags(
//            Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
//                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
//        )
//
//        Log.d("foldddd",dir.toString())
//        startActivityForResult(intent, YOUR_REQUEST_CODE)
//
//    }

    private fun pm() {
        if (!isRecordAudioPermissionGranted()) {
            requestRecordAudioPermission()
        }
    }
    private fun pm2() {
        if (!checkPermission()) {
            requestPermission()
        }
    }
    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_REQUEST_CODE
        )
    }
    private fun isRecordAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO

        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(
                this@MainActivity2,
                READ_EXTERNAL_STORAGE
            )
            val result1 = ContextCompat.checkSelfPermission(
                this@MainActivity2,
                WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACTION_ACCESSIBILITY_SETTINGS,
                    BIND_DEVICE_ADMIN,
                    ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                ),
                2296
            ) //permission request code is just an int
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                2296
            ) //permisison request code is just an int
        }
    }
    private fun startMediaProjectionRequest2() {
//        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        mediaProjectionManager =
            applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            MEDIA_PROJECTION_REQUEST_CODE
        )
    }
    private fun startMediaProjectionRequest() {
//        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        mediaProjectionManager =
            applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            MEDIA_PROJECTION_REQUEST_CODE
        )

//        Intent(this, EndlessService::class.java).also {
//            it.action = action.name
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                log("Starting the service in >=26 Mode")
//                startForegroundService(it)
//                val permission = Manifest.permission.RECORD_AUDIO

//                val intent=Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
//                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
//                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "If you only activate, this app can perform screen off !!!")
//                startActivity(intent)
//
//                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, arrayOf(permission), 3)
//                    return
//                }
//                log("Starting the service in < 26 Mode")
//
//                startService(it)
//            }
//        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode==1000){
            if (resultCode== RESULT_OK){
                Log.i("MainActivity", "Success")
            }else{
                Log.i("MainActivity", "Fail")
            }
        }
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (requestCode == YOUR_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.data?.also { uri ->
                }
            }}
//        Summs=Summs+requestCode
        if (requestCode == com.robertohuertas.endless.MainActivity2.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val intentx:Intent =com.robertohuertas.endless.ScreenCaptureService.getStartIntent(
                    this,
                    resultCode,
                    data)
                startForegroundService(intentx)


//                Log.d("main_asssssssss",""+i+"bolssss: "+myParcelableObject)
            }
//        }
//        if (requestCode == MEDIA_PROJECTION_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                Toast.makeText(
//                    this,
//                    "MediaProjection permission obtained. Foreground service will be started to capture audio.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            Received broadcast Intent { act=data?.action.PACKAGE_REMOVED dat=package:com.robertohuertas.endless flg=0x8000010 (has extras) }
//            if (!isRecordAudioPermissionGranted()) {
//                requestRecordAudioPermission()
//            } else {
//                startMediaProjectionRequest()
//            }
            val audioCaptureIntent = Intent(this, EndlessService::class.java).apply {
                action = EndlessService.ACTION_START
                putExtra(EndlessService.EXTRA_RESULT_DATA, data!!)
//                    Log.d("ddffkkfkfkfkf","sssss  "+data)
            }
            startForegroundService(audioCaptureIntent)
//            }
//            else {
//                Toast.makeText(
//                    this, "Request to obtain MediaProjection denied.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
        }
    }
    //    fun getIBinderExtra(name: String?): IBinder? {
//        return if (mExtras == null) null else mExtras.getIBinder(name)
//    }
    private fun startProjection() {
        pm()
        pm2()
        val mProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
            mProjectionManager.createScreenCaptureIntent(),
            com.robertohuertas.endless.MainActivity2.REQUEST_CODE
        )
    }
    private fun stopProjection() {
        startService(com.robertohuertas.endless.ScreenCaptureService.getStopIntent(this))
    }
    private fun stopCapturing() {
        startService(Intent(this, EndlessService::class.java).apply {
            action = EndlessService.ACTION_STOP
        })
    }
    /**
     * Check if Accessibility Service is enabled.
     *
     * @param mContext
     * @return `true` if Accessibility Service is ON, otherwise `false`
     */
    fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        //your package /   accesibility service path/class
        val service =
            "com.robertohuertas.endless/com.robertohuertas.endless.TestAccessibleService"
        val accessibilityFound = false
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Log.v(
                TAG,
                "accessibilityEnabled = $accessibilityEnabled"
            )
        } catch (e: Settings.SettingNotFoundException) {
            Log.e(
                TAG, "Error finding setting, default accessibility to not found: "
                        + e.message
            )
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            Log.v(
                TAG,
                "***ACCESSIBILIY IS ENABLED*** -----------------"
            )
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessabilityService = mStringColonSplitter.next()
                    Log.v(
                        TAG,
                        "-------------- > accessabilityService :: $accessabilityService"
                    )
                    if (accessabilityService.equals(service, ignoreCase = true)) {
                        Log.v(
                            TAG,
                            "We've found the correct setting - accessibility is switched on!"
                        )
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILIY IS DISABLED***")
        }
        return accessibilityFound
    }

    private fun getAccessibilityServiceIntent(): Intent? {
//        thoigian = editText.text.toString()
//        val Bundle b =new Bundle()
//        val mBundle = Bundle()
//        mBundle.putString("thoigian", thoigian);
//        val intent = Intent(this@MainActivity, TestAccessibleService::class.java)
//        intent.putExtras(mBundle)
//        intent.putExtra("thoigian", thoigian)
//        val pintent = PendingIntent.getService(this@MainActivity, 2310, intent, 0)
        return Intent(this@MainActivity2, TestAccessibleService2::class.java)
    }

    private fun startAccessibilityService() {
        var context: Context = this
        var a = isAccessibilitySettingsOn(context)
        Log.d("main_ass","bol"+a)
        if (!a) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        } else {
            Log.d("FragmentActivity", "开启无障碍服务")
            startService(getAccessibilityServiceIntent())
        }
    }

    private fun stopAccessibilityService() {
        Log.d("FragmentActivity", "关闭无障碍服务")
        stopService(getAccessibilityServiceIntent())
    }
    override fun onDestroy() {
        stopAccessibilityService()
        super.onDestroy()
    }
}

