package com.construction.management.cm.emailsender

import com.construction.management.cm.Runner.Runner
import com.construction.management.cm.dto.CommentAddedNotification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class EmailSender(
    private val runner: Runner
) {

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


    @Async
    fun commentAddedNotification(payload: CommentAddedNotification) {
        val subject = "New comment on Task: ${payload.task} by ${payload.commenter}"
        for (user in payload.toAlert.keys) {
            val recipient = payload.toAlert[user]
            val body = "Hi $recipient,\n \nA new comment has been added to the task ${payload.task}. Please see the details below:\n \nProject: ${payload.project}\nTask: ${payload.task}\nCommented by: ${payload.commenter}\nDate: ${payload.date}\nComment:\n\"${payload.comment}\" \n \nBest Regards,\nMjengo Bora Construction"
            try {
                sendMailToUser(toEmail = user, body = body, subject = subject)
            } catch (e: Exception) {
                runner.printOwner("Could not send comment notification to $user due to ${e.message}")
            }
        }
    }







}