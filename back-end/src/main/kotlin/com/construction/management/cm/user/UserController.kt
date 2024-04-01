package com.construction.management.cm.user

import com.construction.management.cm.dto.SignUp
import com.construction.management.cm.response.DefaultBoolean
import com.construction.management.cm.response.DefaultInt
import com.construction.management.cm.response.DefaultNa
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("api/v1/user/")
class UserController