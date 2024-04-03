package com.construction.management.cm.exceptionhandler
class CustomException(override val message: String?, val additionalInfo:String?): Exception()