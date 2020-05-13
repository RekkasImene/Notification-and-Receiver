package com.example.receiver_and_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.graphics.Color
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService as getSystemService1


class SmsReceiver : BroadcastReceiver() {
    lateinit var  notificationManager :NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder :NotificationCompat.Builder
    private val channelId ="com.example.receiver_and_notification"
    private val description ="Mail sanded after receiving sms"

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
        val extras = intent.extras
        if (extras != null){
            val sms= extras.get("pdus") as Array<Any>

            for ( i in sms.indices) {
                val format = extras.getString("format")

                var smsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SmsMessage.createFromPdu(sms[i] as ByteArray, format)
                } else {
                    SmsMessage.createFromPdu(sms[i] as ByteArray)
                }

                val phoneNumber = smsMessage.originatingAddress.toString()
                val messageText = smsMessage.messageBody.toString()

                Toast.makeText(
                    context,
                    "phoneNumber: $phoneNumber\n" +
                            "messageText: $messageText",
                    Toast.LENGTH_SHORT
                ).show()

                var selectedContacts=intent.getStringArrayListExtra("listContacts")
                for (index :Int in selectedContacts.indices) {
                    if (phoneNumber === selectedContacts.get(index)) {
                        //Reply with mail
                        val javaMailAPI=JavaMailAPI(context,"gi_rekkas@esi.dz","Answer for your SMS","I am in busy")
                        javaMailAPI.execute()

                        //Notification
                        Notify(context)

                    }
                }

            }
            }

        }

    }

    private fun Notify(context: Context){
        val i=Intent(context,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, i, 0)
        notificationManager= context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel= NotificationChannel(channelId,description, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor= Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            //builder
            builder = NotificationCompat.Builder(context,channelId)
                .setContentTitle("Mail sanded") // title for notification
                .setContentText("Mail sanded automatically after receiving an SMS ")
                .setSmallIcon(R.drawable.ic_notification) //small icon for notification
                .setContentIntent(pendingIntent)

        }
        else{
            builder = NotificationCompat.Builder(context)
                .setContentTitle("Mail sanded") // title for notification
                .setContentText("Mail sanded automatically after receiving an SMS ")
                .setSmallIcon(R.drawable.ic_notification) //small icon for notification
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234,builder.build())
    }

}