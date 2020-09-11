package calc

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.SunHttp
import org.http4k.server.asServer

interface Multiplier {
    operator fun invoke(a: Int, b: Int): Int
}

fun DomainMultiplier() = object : Multiplier {
    override operator fun invoke(a: Int, b: Int) = a * b
}

fun HttpMultiplier(http: HttpHandler) = object : Multiplier {
    override operator fun invoke(a: Int, b: Int) =
        http(Request(Method.GET, "")
            .query("a", a.toString())
            .query("b", a.toString())
        ).bodyString().toInt()
}

fun MultiplierServer(multiplier: Multiplier): HttpHandler = {
    Response(Status.OK).body(multiplier(it.query("a")!!.toInt(), it.query("b")!!.toInt()).toString())
}

fun MultiplierApp() = MultiplierServer(DomainMultiplier())

fun main() {
    MultiplierApp().asServer(SunHttp(8000)).start()
}
