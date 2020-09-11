package tutorials.hexagonal._1.tests

import org.http4k.server.Http4kServer
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import tutorials.hexagonal._1.calculator.CalculatorApp
import tutorials.hexagonal.localHttpClientOn

object PortBoundHttpTest : CalculatorContract {
    private lateinit var calculatorServer: Http4kServer

    override val calculator = HttpCalculator(localHttpClientOn(8000))

    @BeforeEach
    fun start() {
        calculatorServer = CalculatorApp().asServer(SunHttp(8000)).start()
    }

    @AfterEach
    fun stop() {
        calculatorServer.stop()
    }
}

