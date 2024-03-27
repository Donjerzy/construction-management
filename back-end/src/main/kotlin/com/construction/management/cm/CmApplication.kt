package com.construction.management.cm

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CmApplication

fun main(args: Array<String>) {
	val dotenv: Dotenv = Dotenv.load()
	runApplication<CmApplication>(*args)
}
