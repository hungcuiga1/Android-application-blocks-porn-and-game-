package com.robertohuertas.endless

import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.*
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import android.media.projection.MediaProjection
import kotlinx.coroutines.launch
import org.deepspeech.libdeepspeech.DeepSpeechModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import android.media.projection.MediaProjectionManager
import android.media.AudioRecord
import java.text.SimpleDateFormat
import kotlin.experimental.and

class EndlessService : Service() {
    private lateinit var audioCaptureThread: Thread
    private var audioRecord: AudioRecord? = null
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false
    private var model: DeepSpeechModel? = null
    private var transcriptionThread: Thread? = null
    private var isRecording: AtomicBoolean = AtomicBoolean(false)
    lateinit var deviceManger: DevicePolicyManager
    lateinit var compName: ComponentName
    private var mediaProjection: MediaProjection? = null

    private val TFLITE_MODEL_FILENAME = "output_graph.tflite"
    private val SCORER_FILENAME = "kenlm_0-9.scorer"
    override fun onBind(intent: Intent): IBinder? {
        log("Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }
//    private fun checkAudioPermission() {
//        // Permission is automatically granted on SDK < 23 upon installation.
//        if (Build.VERSION.SDK_INT >= 23) {
//            val permission = Manifest.permission.RECORD_AUDIO
//
//            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this@Activity, arrayOf(permission), 3)
//            }
//        }
//    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            log("using an intent with action $action")
            when (action) {
                ACTION_START ->{
                    mediaProjection =
                        mediaProjectionManager.getMediaProjection(
                            Activity.RESULT_OK,
                            intent.getParcelableExtra(EXTRA_RESULT_DATA)!!
                        ) as MediaProjection
                    startService()
                Log.d("ddffkkfkfkfkf","sssss"+intent.getParcelableExtra(EXTRA_RESULT_DATA)!!)
                }
                ACTION_STOP -> stopService()
                else -> log("This should never happen. No action in the received intent")
            }
        } else {
            log(
                "with a null intent. It has been probably restarted by the system."
            )
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        log("The service has been created".toUpperCase())
        val notification = createNotification()
        mediaProjectionManager =
            applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startForeground(1, notification)

//        checkAudioPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
//        log("The service has been destroyed".toUpperCase())
//        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, EndlessService::class.java).also {
            it.setPackage(packageName)
        };
        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(
            this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE)
                as AlarmManager;
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
                + 1000, restartServicePendingIntent);
    }
    private fun transcribe() {
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection!!)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA) // TODO provide UI options for inclusion/exclusion
            .build()
        // We read from the recorder in chunks of 2048 shorts. With a model that expects its input
        // at 16000Hz, this corresponds to 2048/16000 = 0.128s or 128ms.
        val audioBufferSize = 2048
        val audioData = ShortArray(audioBufferSize)
        deviceManger=getSystemService(AppCompatActivity.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName= ComponentName(this, DeviceAdmin::class.java)
        val enable=deviceManger.isAdminActive(compName)
//        runOnUiThread { btnStartInference.text = "Stop Recording" }
//        val a:Float
//        a=1.1f
        model?.let { model ->
            var streamContext = model.createStream()

            val audioFormat = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(16000)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build()
            audioRecord = AudioRecord.Builder()
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(BUFFER_SIZE_IN_BYTES)
                .setAudioPlaybackCaptureConfig(config)
                .build()
            audioRecord!!.startRecording()
            var startt = System.currentTimeMillis()
            var endd =startt+ 10*1000
            while (isRecording.get()) {
                audioRecord!!.read(audioData, 0, audioBufferSize)
                model.feedAudioContent(streamContext, audioData, audioData.size)
                val decoded = model.intermediateDecode(streamContext)
                Log.d("decoded2decoded2decoded2",decoded)
                val array: Array<String> = decoded.split(" ").toTypedArray()
//                for (i in array){
//                Log.d("aaa",i)}
                writeToFile(decoded)
                val found = Arrays.stream(array).anyMatch { t -> t == "cặc" }
                if (found) {
                    streamContext = model.createStream()
//                    deviceManger.lockNow()
                    Common.ngay=3;
                }
//                runOnUiThread { transcription.text = decoded }
                if(System.currentTimeMillis()>endd){endd =System.currentTimeMillis()+ 10*1000
                    streamContext = model.createStream()}
//                runOnUiThread { transcription.text = decoded }
            }
//            audioCaptureThread = thread(start = true) {
//                val outputFile = createAudioFile()
//                Log.d(LOG_TAG, "Created file for capture target: ${outputFile.absolutePath}")
//                writeAudioToFile(outputFile)
//            }


//            runOnUiThread {
//                btnStartInference.text = "Start Recording"
//                transcription.text = decoded
//            }

//            audioRecord!!.stop()
//            audioRecord!!.release()
//            audioRecord = null
        }
    }
    private fun createAudioFile(): File {
        val audioCapturesDirectory = File(getExternalFilesDir(null), "/AudioCaptures")
        if (!audioCapturesDirectory.exists()) {
            audioCapturesDirectory.mkdirs()
        }
        val timestamp = SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.US).format(Date())
        val fileName = "Capture-$timestamp.pcm"
        return File(audioCapturesDirectory.absolutePath + "/" + fileName)
    }
    private fun writeToFile(data : String) {
        try {
            // Creates a FileOutputStream
            val dd = getExternalFilesDir(null).toString()
            val file = FileOutputStream("$dd/output.txt")

            // Creates an OutputStreamWriter
            val output = OutputStreamWriter(file)

            // Writes string to the file
            output.write(data)

            // Closes the writer
            output.close()
        } catch (e: java.lang.Exception) {
            e.stackTrace
        }
    }
    private fun startService() {
        if (isServiceStarted) return
        log("Starting the foreground service task")
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NGĂN::lock").apply {
                    acquire()
                }
            }

        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            if (isServiceStarted) {
                launch(Dispatchers.IO) {
                    onRecordClick()
                }
            }
            log("End of the loop for the service")
        }
    }
    fun onRecordClick() {
        if (model == null) {
            if (!createModel()) {
                return
            }
        }
        startListening()
    }
    private fun createModel(): Boolean {
        val tfliteModelPath =
            cacheDir.absolutePath + File.separator + TFLITE_MODEL_FILENAME
        Utils.copyFileFromAsset(
            this@EndlessService,
            TFLITE_MODEL_FILENAME,
            tfliteModelPath
        )

        val scorerPath =
            cacheDir.absolutePath + File.separator + SCORER_FILENAME
        Utils.copyFileFromAsset(
            this@EndlessService,
            SCORER_FILENAME,
            scorerPath
        )
        val modelsPath = getExternalFilesDir(null).toString()
//        val tfliteModelPath = "$modelsPath/$TFLITE_MODEL_FILENAME"
//        val scorerPath = "$modelsPath/$SCORER_FILENAME"
//        Log.d("aaa","$modelsPath")

//        for (path in listOf(tfliteModelPath, scorerPath)) {
//            if (!File(path).exists()) {
////                status.append("Model creation failed: $path does not exist.\n")
//                return false
//            }
//        }

        model = DeepSpeechModel(tfliteModelPath)
        model?.enableExternalScorer(scorerPath)
        model?.addHotWord("cặc",8f)
        model?.addHotWord("lồn",8f)
        return true
    }
    private fun startAudioCapture() {
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection!!)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA) // TODO provide UI options for inclusion/exclusion
            .build()

        /**
         * Using hardcoded values for the audio format, Mono PCM samples with a sample rate of 8000Hz
         * These can be changed according to your application's needs
         */
        val audioFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(8000)
            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
            .build()

        audioRecord = AudioRecord.Builder()
            .setAudioFormat(audioFormat)
            // For optimal performance, the buffer size
            // can be optionally specified to store audio samples.
            // If the value is not specified,
            // uses a single frame and lets the
            // native code figure out the minimum buffer size.
            .setBufferSizeInBytes(BUFFER_SIZE_IN_BYTES)
            .setAudioPlaybackCaptureConfig(config)
            .build()

        audioRecord!!.startRecording()

    }
    private fun startListening() {
        if (isRecording.compareAndSet(false, true)) {
//            transcriptionThread = Thread(Runnable { transcribe() }, "Transcription Thread")
//            transcriptionThread?.start()
            transcribe()
        }
    }
    private fun stopService() {
        log("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            log("Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

//    private fun writeAudioToFile(outputFile: File) {
//        val fileOutputStream = FileOutputStream(outputFile)
//        val capturedAudioSamples = ShortArray(NUM_SAMPLES_PER_READ)
//
//        while (!audioCaptureThread.isInterrupted) {
//            audioRecord?.read(capturedAudioSamples, 0, NUM_SAMPLES_PER_READ)
//
//            // This loop should be as fast as possible to avoid artifacts in the captured audio
//            // You can uncomment the following line to see the capture samples but
//            // that will incur a performance hit due to logging I/O.
//            // Log.v(LOG_TAG, "Audio samples captured: ${capturedAudioSamples.toList()}")
//
//            fileOutputStream.write(
//                capturedAudioSamples.toByteArray(),
//                0,
//                BUFFER_SIZE_IN_BYTES
//            )
//        }
//
//        fileOutputStream.close()
//        Log.d(LOG_TAG, "Audio capture finished for ${outputFile.absolutePath}. File size is ${outputFile.length()} bytes.")
//    }
//    private fun ShortArray.toByteArray(): ByteArray {
//        // Samples get translated into bytes following little-endianness:
//        // least significant byte first and the most significant byte last
//        val bytes = ByteArray(size * 2)
//        for (i in 0 until size) {
//            bytes[i * 2] = (this[i] and 0x00FF).toByte()
//            bytes[i * 2 + 1] = (this[i].toInt() shr 8).toByte()
//            this[i] = 0
//        }
//        return bytes
//    }
    private fun createNotification(): Notification {
        val notificationChannelId = "NGĂN CHANNEL"
        val NOTIFICATION_ID : Int = 1337
        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                "NGĂN CHANNEL",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "NGĂN CHANNEL"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, SplashActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        return builder
            .setContentTitle("NGĂN")
            .setContentText("KHÔNG NGHE")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }
    private fun stopAudioCapture() {
        requireNotNull(mediaProjection) { "Tried to stop audio capture, but there was no ongoing capture in place!" }

        audioCaptureThread.interrupt()
        audioCaptureThread.join()

        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null

        mediaProjection!!.stop()
        stopSelf()
    }
    companion object {
        private const val LOG_TAG = "AudioCaptureService"
        private const val SERVICE_ID = 123
        private const val NOTIFICATION_CHANNEL_ID = "AudioCapture channel"

        private const val NUM_SAMPLES_PER_READ = 1024
        private const val BYTES_PER_SAMPLE = 2 // 2 bytes since we hardcoded the PCM 16-bit format
        private const val BUFFER_SIZE_IN_BYTES = NUM_SAMPLES_PER_READ * BYTES_PER_SAMPLE

        const val ACTION_START = "AudioCaptureService:Start"
        const val ACTION_STOP = "AudioCaptureService:Stop"
        const val EXTRA_RESULT_DATA = "AudioCaptureService:Extra:ResultData"
    }
}
