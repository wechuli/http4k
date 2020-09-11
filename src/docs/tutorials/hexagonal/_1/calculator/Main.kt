package tutorials.hexagonal._1.calculator

import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    CalculatorApp().asServer(SunHttp(8000)).start()
}
