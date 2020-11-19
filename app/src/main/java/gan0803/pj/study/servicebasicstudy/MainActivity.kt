package gan0803.pj.study.servicebasicstudy

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var isServiceBound = false
    private lateinit var service: MyService
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            unbindMyService()
        }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            val binder = binder as MyService.MyBinder
            service = binder.getService()
            isServiceBound = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.start_service_button).setOnClickListener {
            startMyService()
        }
        findViewById<Button>(R.id.bind_service_button).setOnClickListener {
            bindMyService()
        }
        findViewById<Button>(R.id.bound_method_call_button).setOnClickListener {
            callMyServiceMethod()
        }
        findViewById<Button>(R.id.unbind_service_button).setOnClickListener {
            unbindMyService()
        }
        findViewById<Button>(R.id.stop_service_button).setOnClickListener {
            stopMyService()
        }
    }

    private fun startMyService() {
        val intent = Intent(this, MyService::class.java)
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun bindMyService() {
        // We can also write like this using "also".
        Intent(this, MyService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun callMyServiceMethod() {
        if (isServiceBound) {
            service.awesomeMethod("Hello MyService!")
        }
    }

    private fun unbindMyService() {
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }

    private fun stopMyService() {
        unbindMyService()
        val intent = Intent(this, MyService::class.java)
        stopService(intent)
    }
}