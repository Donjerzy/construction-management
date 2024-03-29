package com.construction.management.cm.EmailSender

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailSender {

    @Autowired
    lateinit var mailSender: JavaMailSender

    @Value("\${spring.mail.username}")
    private val fromEmail: String = "-"

    fun sendMailToUser(toEmail:String, body:String, subject:String){
        val message = SimpleMailMessage()
        message.from = fromEmail
        message.setTo(toEmail)
        message.subject = subject
        message.text = body
        mailSender.send(message)
    }


}