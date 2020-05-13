package com.example.receiver_and_notification

import android.content.Context
import android.os.AsyncTask
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class JavaMailAPI(
    context: Context,
    email: String,
   subject: String,
    message: String
) : AsyncTask<Void, Void, Void>() {

    private val mContext:Context
    private val mEmail:String
    private val mSubject:String
    private val mMessage:String

    private lateinit var session: Session

    init {
        this.mContext=context
        this.mEmail=email
        this.mSubject=subject
        this.mMessage=message
    }

    override fun doInBackground(vararg voids: Void?): Void? {
       val props = Properties()
        props.put("mail.smtp.host", "smtp.gmail.com")
        props.put("mail.smtp.socketFactory.port", "465")
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "465")

        session =  Session.getDefaultInstance(props,
            object : javax.mail.Authenticator() {
                //Authenticating the password
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
                }
            })
        try {
            //Creating MimeMessage object
            val mm = MimeMessage(session)
            //Setting sender address
            mm.setFrom(InternetAddress(Credentials.EMAIL))
            //Adding receiver
            mm.addRecipient(
                Message.RecipientType.TO,
                InternetAddress(mEmail))
            //Adding subject
            mm.setSubject(mSubject)
            //mm.subject = "Your mail's subject."

            //Adding message
            mm.setText(mMessage)
            //mm.setText("Your mail body.")

            //Sending email
            Transport.send(mm)


        } catch (e: MessagingException) {
            e.printStackTrace()
        }

        return null
    }

}