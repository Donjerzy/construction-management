package com.construction.management.cm.init

import com.construction.management.cm.expense.ExpenseRepository
import com.construction.management.cm.expensetype.ExpenseType
import com.construction.management.cm.expensetype.ExpenseTypeRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DbInit(private val expenseTypeRepository: ExpenseTypeRepository): CommandLineRunner {

    override fun run(vararg args: String?) {
        val expenseTypesCount = expenseTypeRepository.findAll().size
        if (expenseTypesCount == 0) {
            val expenseTypes = mutableListOf<ExpenseType>()

            val typeOne = ExpenseType()
            typeOne.id = 1
            typeOne.name = "Materials and Supplies"

            val typeTwo = ExpenseType()
            typeTwo.id = 2
            typeTwo.name = "Equipment and Machinery"

            val typeThree = ExpenseType()
            typeThree.id = 3
            typeThree.name = "Permits and Fees"

            val typeFour = ExpenseType()
            typeFour.id = 4
            typeFour.name = "Overhead and Administrative Costs"

            val typeFive = ExpenseType()
            typeFive.id = 5
            typeFive.name = "Site Preparation and Development"

            val typeSix = ExpenseType()
            typeSix.id = 6
            typeSix.name = "Other"

            expenseTypes.addAll(listOf(typeOne, typeTwo, typeThree, typeFour, typeFive, typeSix))
            expenseTypeRepository.saveAll(expenseTypes)
            println("DB Setup complete")
        }
    }
}