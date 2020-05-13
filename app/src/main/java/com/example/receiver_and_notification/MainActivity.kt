package com.example.receiver_and_notification

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.BroadcastReceiver as BroadcastReceiver1

class MainActivity : AppCompatActivity() , ContactAdapter.OnItemClickListner {

    private val requestReceiveSms = 2
    lateinit var replybtn : Button
    lateinit var loadbtn : Button
    var selectedContacts: ArrayList<Contact> = ArrayList()
    var numberContacts: ArrayList<String> = ArrayList()

    //SMS Receiver
    lateinit var  receiver: BroadcastReceiver1

    lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context=this

        val filter =IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        receiver=object : BroadcastReceiver1(){
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(context,intent?.action,Toast.LENGTH_SHORT).show()
            }
        }
        registerReceiver(receiver,filter)

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS),
                requestReceiveSms)
        }

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET),
                1)
        }

        replybtn=findViewById(R.id.ReplyBtn)
        loadbtn=findViewById(R.id.LoadBtn)
        var contacts: MutableList<Contact> = ArrayList()
        loadbtn.setOnClickListener{

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

                    //permission non authorise

                } else {
                    //permission authorise

                    var recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                    recyclerView.layoutManager = LinearLayoutManager(this)

                    var list = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                    )

                    if (list != null) {
                        while (list.moveToNext()) {
                            val nom =
                                list.getString(list.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            val numero =
                                list.getString(list.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))


                            var contact = Contact(nom, numero)
                            contacts.add(contact)
                        }
                        recyclerView.adapter = ContactAdapter(contacts as ArrayList<Contact>,this)
                        list.close()
                    }
                }
            }
        }

        replybtn.setOnClickListener {

           var intent=Intent(this@MainActivity,SmsReceiver::class.java)
            intent.putExtra("listContacts",numberContacts)

            //Afficher les contacts selectionnes
            var recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = ContactAdapter(selectedContacts ,this)

            //SMS Receiver
            sendBroadcast(Intent(context,SmsReceiver::class.java))

        }

    }
    override fun OnSelectItemClick(item:Contact, Position:Int,nom : TextView,numero: TextView,select:Button){
       numberContacts.add(item.numero)
        selectedContacts.add(item)
        select.text="Selected"

    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

}