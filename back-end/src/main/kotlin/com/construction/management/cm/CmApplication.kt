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
class CmApplication {

	@Component
	class InitialDataLoader(private val employeeTypeRepository: EmployeeTypeRepository,
							private val wageTypeRepository: WageTypeRepository): CommandLineRunner {
		override fun run(vararg args: String?) {
			val numberOfWageTypes = wageTypeRepository.isEmpty()
			if (numberOfWageTypes == 0) {
				val wageTypeOne = WageType()
				val wageTypeTwo = WageType()
				val wageTypeThree = WageType()
				val outOfBoxWageTypes = mutableListOf<WageType>()

				wageTypeOne.name = "Daily"
				wageTypeOne.period = 1
				wageTypeTwo.name = "Weekly"
				wageTypeTwo.period = 7
				wageTypeThree.name = "Monthly"
				wageTypeThree.period = 30

				outOfBoxWageTypes.add(wageTypeOne)
				outOfBoxWageTypes.add(wageTypeTwo)
				outOfBoxWageTypes.add(wageTypeThree)

				wageTypeRepository.saveAll(outOfBoxWageTypes)
			}
			val numberOfEmployeeTypes = employeeTypeRepository.isEmpty()
			if (numberOfEmployeeTypes == 0) {
				val outOfTheBoxEmployeeRoles = mutableListOf(
					"Architectural Team",
					"Engineering Team",
					"Project Manager",
					"Construction Manager",
					"Site Supervisor",
					"Foreman",
					"Labourers",
					"Carpenters",
					"Electricians",
					"Plumbers",
					"Masons",
					"Painters",
					"Welders",
					"Heavy Equipment Operators",
					"Surveyors",
					"Safety Officers",
					"Quality Control Inspectors",
					"Estimators",
					"Procurement Specialists",
					"Administrative Staff"
				)
				val employeeTypesToBeAdded = mutableListOf<EmployeeType>()
				for (role in outOfTheBoxEmployeeRoles) {
					val empType = EmployeeType()
					empType.name = role
					employeeTypesToBeAdded.add(empType)
				}
				employeeTypeRepository.saveAll(employeeTypesToBeAdded)
			}

		}

	}

}

fun main(args: Array<String>) {
	runApplication<CmApplication>(*args)
}
