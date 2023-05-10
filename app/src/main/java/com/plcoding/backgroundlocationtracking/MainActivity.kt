package com.plcoding.backgroundlocationtracking

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.plcoding.backgroundlocationtracking.ui.theme.BackgroundLocationTrackingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId: String? = intent.getStringExtra("userId")
        val deviceId: String? = intent.getStringExtra("deviceId")
        val groupCode: String? = intent.getStringExtra("groupCode")
        Log.d("TAG", "onCreate: "+userId+deviceId+groupCode)
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        preferences.edit().putString("userId",userId).commit()
        preferences.edit().putString("deviceId",deviceId).commit()
        preferences.edit().putString("groupCode",groupCode).commit()


        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
        val intent = Intent(applicationContext, LocationService::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("deviceId", deviceId)
        intent.putExtra("groupCode", groupCode)
       intent.apply {
            action = LocationService.ACTION_START
            startService(this)



            setContent {
                BackgroundLocationTrackingTheme {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {


                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }
            }
            finish()
        }
    }
}