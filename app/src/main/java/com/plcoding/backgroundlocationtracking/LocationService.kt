package com.plcoding.backgroundlocationtracking

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonObject
import com.plcoding.backgroundlocationtracking.retrofit.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.log


class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private  var userId=""
    private  var deviceId=""
    private  var groupCode=""
    private  var latCopy=""
    private  var longCopy=""
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()


        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setSilent(true)
            .setAutoCancel(false)
            .setPriority(999999)
            .setOngoing(true)
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        userId= preferences.getString("userId","").toString()
        deviceId= preferences.getString("deviceId","").toString()
        groupCode= preferences.getString("groupCode","").toString()
        Log.d("TAG", "onResponse: ab chala ye...............Location: ($userId, $deviceId, $groupCode)")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.168:8084/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

//3. Create an instance of the interface:
        locationClient
            .getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updatedNotification = notification.setContentText(
                    "Location: ($lat, $long)"

                )
        val apiService = retrofit.create(APIService::class.java)
        val jsonObject = JsonObject()
        jsonObject.addProperty("userId", userId)
        jsonObject.addProperty("deviceId", deviceId)
        jsonObject.addProperty("groupCode", groupCode)
        jsonObject.addProperty("trackAppVersion", "1.14")
        jsonObject.addProperty("latitude",lat )
        jsonObject.addProperty("longitude", long)



//                Timer().schedule(200000) {
//                    Log.d("TAG", "onResponse: ab chala ye...............Location: ($lat, $long)")
//                    Log.d("TAG", "onResponse: ab chala ye...............Location: ($userId, $deviceId, $groupCode)")
//                if (!userId.equals("") && !latCopy.equals(lat)){
//                    apiService.trackUser(jsonObject).enqueue(object : Callback<ResponseBody>{
//                        override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
//                            if (response != null) {
//                                Log.d("TAG", "onResponse: "+response.message())
//                                latCopy=lat
//                                longCopy=long
//                            }
//                        }
//
//                        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
//                            Log.d("TAG", "onFailure: $t")
//                        }
//                    })
//                }
//
//                }
                Log.d("TAG", "start: "+ "Location: ($lat, $long)")
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())

    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}