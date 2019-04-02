package blog.testing_http4k.`1_zero_infra_endpoint_testing`

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test

class EndpointTest {

    val app: HttpHandler = TODO()

    @Test
    fun `responds to response with the time`() {
        assertThat(app(Request(Method.GET, "")), hasStatus(OK).and(hasBody("2000/01/01")))
    }
}