package com.example.demo_deploy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoDeployApplication

fun main(args: Array<String>) {
	runApplication<DemoDeployApplication>(*args)
}
