package com.construction.management.cm.Runner

import com.construction.management.cm.config.JwtProperties
import com.construction.management.cm.project.Project
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Runner: CommandLineRunner {


    fun printOwner(str: String) {
        println(str)
    }


    override fun run(vararg args: String?) {
        println(System.currentTimeMillis())
    }

}