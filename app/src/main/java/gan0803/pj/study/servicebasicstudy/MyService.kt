package gan0803.pj.study.servicebasicstudy

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MyService : Service() {
    companion object {
        val TAG: String = this::class.java.simpleName
        const val ONGOING_NOTIFICATION_ID = 1
        const val CHANNEL_ID = "ServiceBasicStudy"
        const val CHANNEL_NAME = "ServiceBasicStudy"
    }

    private var startMode = START_NOT_STICKY    // indicates how to behave if the service is killed
    private var binder = MyBinder()             // interface for clients that bind
    private var allowRebind = true              // indicates whether onRebind should be used

    private val isUiThread = isUiThread()

    override fun onCreate() {
        // The service is being created
        Log.d(TAG, "onCreate")
    }

    private val channelId by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this, CHANNEL_ID, CHANNEL_NAME)
        } else {
            // If earlier version channel ID is not used
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String
    ): String {
        val channel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // The service is starting, due to a call to startService()
        Log.d(TAG, "onStartCommand")
        Toast.makeText(applicationContext, "onStartCommand", Toast.LENGTH_SHORT).show()

        val notification = buildNotification(this, channelId)
        startForeground(ONGOING_NOTIFICATION_ID, notification)
        return startMode
    }

    override fun onBind(intent: Intent): IBinder? {
        // A client is binding to the service with bindService()
        Log.d(TAG, "onBind")
        Toast.makeText(applicationContext, "onBind", Toast.LENGTH_SHORT).show()
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        // All clients have unbound with unbindService()
        Log.d(TAG, "onUnbind")
        Toast.makeText(applicationContext, "onUnbind", Toast.LENGTH_SHORT).show()

        stopForeground(false)
        return allowRebind
    }

    override fun onRebind(intent: Intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.d(TAG, "onRebind")
        Toast.makeText(applicationContext, "onRebind", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d(TAG, "onDestroy")
        Toast.makeText(applicationContext, "onDestroy", Toast.LENGTH_SHORT).show()
    }

    inner class MyBinder : Binder() {
        fun getService(): MyService = this@MyService
    }


    fun awesomeMethod(msg: String) {
        Log.d(TAG, "awesomeMethod")

        Toast.makeText(
            applicationContext,
            "isUiThread $isUiThread / $msg",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun buildNotification(context: Context, channelId: String): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Service Basic Study")
            .setContentText("Application is active.")
            .setSmallIcon(R.drawable.ic_baseline_tag_faces_24)
            .setContentIntent(pendingIntent)
            .setTicker("Application is active")
            .build()
    }

    private fun isUiThread(): Boolean {
        return Thread.currentThread() == Looper.getMainLooper().thread
    }
}