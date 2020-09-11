package tutorials.hexagonal._1.tests

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import tutorials.hexagonal._1.calculator.Calculator

class HttpCalculator(private val http: HttpHandler): Calculator {
    override fun reset(): Int = http(Request(Method.GET, "/reset")).bodyString().toInt()
}
