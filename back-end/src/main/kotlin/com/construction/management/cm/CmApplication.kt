package com.construction.management.cm

import com.construction.management.cm.employeetype.EmployeeType
import com.construction.management.cm.employeetype.EmployeeTypeRepository
import com.construction.management.cm.wagetype.WageType
import com.construction.management.cm.wagetype.WageTypeRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class CmApplication

fun main(args: Array<String>) {
	runApplication<CmApplication>(*args)
}
