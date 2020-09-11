package calc

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.server.SunHttp
import org.http4k.server.asServer

interface Adder {
    operator fun invoke(a: Int, b: Int): Int
}

fun DomainAdder() = object : Adder {
    override operator fun invoke(a: Int, b: Int) = a + b
}

fun HttpAdder(http: HttpHandler) = object : Adder {
    override operator fun invoke(a: Int, b: Int) =
        http(Request(GET, "")
            .query("a", a.toString())
            .query("b", a.toString())
        ).bodyString().toInt()
}

fun AdderServer(adder: Adder): HttpHandler = {
    Response(OK).body(adder(it.query("a")!!.toInt(), it.query("b")!!.toInt()).toString())
}

fun AdderApp() = AdderServer(DomainAdder())

fun main() {
    AdderApp().asServer(SunHttp(8000)).start()
}
