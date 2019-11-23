package net.kwmt27.retrofitsample

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.fab
import kotlinx.android.synthetic.main.activity_main.toolbar
import net.kwmt27.retrofitsample.MainActivity.Companion.TAG
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            Log.d(TAG, "start")
            val some: Service = SomeImpl()
            some.doSomething(100)
            Log.d(TAG, "end")

            val service = create(Service::class.java)
            service.doSomething(1000)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            Array(1) { service },
            InvocationHandler { _, method, args ->
                Log.d(TAG, "start")
                Log.d(TAG, method.name + if (args.isNotEmpty()) args[0] else "")
                Log.d(TAG, "end")
            }
        ) as T
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }
}

interface Service {
    fun doSomething(value: Int)
}

class SomeImpl : Service {
    override fun doSomething(value: Int) {
        Log.d(TAG, value.toString())
    }
}