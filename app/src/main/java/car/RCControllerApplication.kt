package car

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import car.feedback.EMPTY_STRING

class RCControllerApplication : Application() {
    val myIP:String
        get() {
            val wifiMgr = applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager?
            val wifiInfo = wifiMgr?.connectionInfo
            return if (wifiInfo != null) {
                Formatter.formatIpAddress(wifiInfo.ipAddress)
            }
            else
                EMPTY_STRING
        }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    companion object {
        var instance: RCControllerApplication? = null
            private set
    }
}

/**
 * Returns `true` if enum T contains an entry with the specified name.
 */
inline fun <reified T : Enum<T>> enumContains(name: String?): Boolean {
    return if (name == null) false else enumValues<T>().any { it.name == name}
}