package com.construction.management.cm.employeetype

import com.construction.management.cm.dto.EmployeeTypes
import com.construction.management.cm.user.UserRepository
import org.springframework.stereotype.Service

@Service
class EmployeeTypeService(private val repository: EmployeeTypeRepository,
                          private val userRepository: UserRepository) {
    fun getEmployeeTypes(userEmail: String): MutableSet<EmployeeTypes> {
        val userId = userRepository.getUserId(userEmail)
        return mapEmployeeTypesToGetEmployeeTypesResponse(repository.getUserEmployeeTypes(user= userId!!))
    }


    fun mapEmployeeTypesToGetEmployeeTypesResponse(employeeTypes: MutableList<EmployeeType>): MutableSet<EmployeeTypes> {
        val result = mutableSetOf<EmployeeTypes>()
        for(type in employeeTypes) {
            result.add(
                EmployeeTypes(
                    id = type.id,
                    name = type.name
                )
            )
        }
        return result
    }


}