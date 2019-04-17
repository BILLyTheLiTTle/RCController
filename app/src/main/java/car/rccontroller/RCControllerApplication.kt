package car.rccontroller

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import car.rccontroller.network.EMPTY_STRING

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