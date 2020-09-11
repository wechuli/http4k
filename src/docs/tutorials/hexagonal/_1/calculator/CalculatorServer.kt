package tutorials.hexagonal._1.calculator

import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes

fun CalculatorServer(calculator: Calculator) = routes(
    "/reset" bind GET to {
        Response(OK).body(calculator.reset().toString())
    }
)
