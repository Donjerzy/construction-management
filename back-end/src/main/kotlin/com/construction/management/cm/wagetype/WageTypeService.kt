package com.construction.management.cm.wagetype

import com.construction.management.cm.auth.TokenService
import com.construction.management.cm.dto.WageTypes
import com.construction.management.cm.user.UserRepository
import com.construction.management.cm.user.UserService
import org.springframework.stereotype.Service

@Service
class WageTypeService(private val repository: WageTypeRepository,
                      private val userRepository: UserRepository) {
    fun getWageTypes(userEmail: String): MutableSet<WageTypes> {
        val userId = userRepository.getUserId(userEmail)
        return mapWageTypesToGetWageTypesResponse(repository.getUserWageTypes(user = userId!!))
    }

    fun mapWageTypesToGetWageTypesResponse(wageTypes: MutableList<WageType>): MutableSet<WageTypes> {
        val result = mutableSetOf<WageTypes>()
        for(type in wageTypes) {
            result.add(
                WageTypes(
                    id = type.id,
                    name = type.name
                )
            )
        }
        return result
    }

}