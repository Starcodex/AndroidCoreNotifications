package com.starcodex.notifyme.notifyme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.app.NotificationManager
import android.content.Context
import android.app.NotificationChannel
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import android.content.Intent
import android.app.PendingIntent
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import kotlinx.android.synthetic.main.activity_main.*
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.util.Log







class MainActivity : AppCompatActivity() {

    val TAG = javaClass.name
    private val ACTION_UPDATE_NOTIFICATION = "com.starcodex.notifyme.ACTION_UPDATE_NOTIFICATION"
    private val ACTION_CANCEL_NOTIFICATION = "com.starcodex.notifyme.ACTION_CANCEL_NOTIFICATION"

    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private var mNotifyManager: NotificationManager? = null
    private val NOTIFICATION_ID = 0

    private val updateReceiver = UpdateReceiver()
    private val cancelReceiver = CancelReceiver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(updateReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
        registerReceiver(cancelReceiver, IntentFilter(ACTION_CANCEL_NOTIFICATION))

        createNotificationChannel()
        setNotificationButtonState(true, false, false)
    }


    fun sendNotification(view: View) {
        mNotifyManager!!.notify(NOTIFICATION_ID, getNotificationBuilder().build())
        setNotificationButtonState(false, true, true)
    }

    fun updateNotification(view : View) {
        Log.d(TAG,"UPDATE NOTIFICATION")
        val androidImage = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder = getNotificationBuilder().setStyle(NotificationCompat.BigPictureStyle().bigPicture(androidImage).setBigContentTitle("Notification Updated!"))
        mNotifyManager!!.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(false, false, true)
    }

    fun cancelNotification(view : View) {
        setNotificationButtonState(true, false, false)
    }

    fun createNotificationChannel() {
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID,"Starcodex Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.setLightColor(Color.RED)
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from starcodex")
            mNotifyManager!!.createNotificationChannel(notificationChannel)

        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {

        val notificationPendingIntent = PendingIntent.getActivity(this,NOTIFICATION_ID, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        val updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, Intent(ACTION_UPDATE_NOTIFICATION), PendingIntent.FLAG_ONE_SHOT)
        val cancelPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, Intent(ACTION_CANCEL_NOTIFICATION), PendingIntent.FLAG_ONE_SHOT)

        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("You've been notified!")
            .setContentText("This is your notification text.")
            .setSmallIcon(R.drawable.ic_android)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
            .setDeleteIntent(cancelPendingIntent)
    }

    fun rootView() : View{
        return window.decorView
    }

    fun setNotificationButtonState(isNotifyEnabled: Boolean,isUpdateEnabled: Boolean,isCancelEnabled: Boolean) {
        notify.setEnabled(isNotifyEnabled)
        update.setEnabled(isUpdateEnabled)
        cancel.setEnabled(isCancelEnabled)
    }


    inner class UpdateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateNotification(rootView())
        }
    }

    inner class CancelReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setNotificationButtonState(true, false, false)
        }
    }


    override fun onDestroy() {
        unregisterReceiver(updateReceiver)
        unregisterReceiver(cancelReceiver)
        super.onDestroy()
    }
}
