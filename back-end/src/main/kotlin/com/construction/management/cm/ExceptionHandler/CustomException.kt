package com.construction.management.cm.ExceptionHandler
class CustomException(override val message: String?, val additionalInfo:String?): Exception()