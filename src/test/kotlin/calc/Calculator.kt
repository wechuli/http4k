package calc

import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer

interface Calculator {
    fun square(a: Int): Int
}

fun DomainCalculator(multiplier: Multiplier) = object : Calculator {
    override fun square(a: Int) = multiplier(a, a)
}

fun CalculatorServer(calculator: Calculator) = routes(
    "/square" bind GET to {
        Response(OK).body(calculator.square(it.query("a")!!.toInt()).toString())
    }
)

fun CalculatorApp(http: HttpHandler) = CalculatorServer(
    DomainCalculator(HttpMultiplier(http)))


fun main() {
    CalculatorApp(JavaHttpClient()).asServer(SunHttp(8000)).start()
}
