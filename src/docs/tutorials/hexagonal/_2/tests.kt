package tutorials.hexagonal._2

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.server.Http4kServer
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

fun HttpCalculator(http: HttpHandler) = object : Calculator {
    override fun square(a: Int): Int = http(Request(GET, "/square")
        .query("a", a.toString())
    ).bodyString().toInt()
}

interface Contract {
    val calculator: Calculator

    @Test
    fun `can square a number`() {
        assertThat(calculator.square(2), equalTo(4))
    }
}

object DomainTest : Contract {
    override val calculator = DomainCalculator(DomainMultiplier())
}

object InMemoryHttpTest : Contract {
    private val multiplierHttp = MultiplierApp()
    private val calculatorHttp = CalculatorApp(multiplierHttp)

    override val calculator = HttpCalculator(calculatorHttp)
}

object PortBoundHttpTest : Contract {
    private lateinit var calculatorServer: Http4kServer
    private lateinit var multiplierServer: Http4kServer

    override val calculator = HttpCalculator(localHttpClientOn(8000))

    @BeforeEach
    fun start() {
        calculatorServer = CalculatorApp(localHttpClientOn(9000)).asServer(SunHttp(8000)).start()
        multiplierServer = MultiplierApp().asServer(SunHttp(9000)).start()
    }

    @AfterEach
    fun stop() {
        multiplierServer.stop()
        calculatorServer.stop()
    }

    private fun localHttpClientOn(port: Int) =
        SetBaseUriFrom(Uri.of("http://localhost:$port")).then(OkHttp())
}
