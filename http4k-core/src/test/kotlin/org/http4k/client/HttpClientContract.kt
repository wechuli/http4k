package org.http4k.client

import com.natpryce.hamkrest.anything
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.OPTIONS
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Method.TRACE
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.ServerFilters
import org.http4k.server.ServerConfig
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

abstract class HttpClientContract(serverConfig: (Int) -> ServerConfig,
                                  val client: HttpHandler,
                                  private val timeoutClient: HttpHandler) : AbstractHttpClientContract(serverConfig) {

    @Test
    open fun `can forward response body to another request`() {
        val response = client(Request(GET, "http://localhost:$port/stream"))
        val echoResponse = client(Request(POST, "http://localhost:$port/echo").body(response.body))
        assertThat(echoResponse.bodyString(), equalTo("stream"))
    }

    @Test
    fun `supports gzipped content`() {
        val asServer = ServerFilters.GZip().then { Response(Status.OK).body("hello") }.asServer(SunHttp(0))
        asServer.start()
        val client = JavaHttpClient()

        val request = Request(GET, "http://localhost:${asServer.port()}").header("accept-encoding", "gzip")
        client(request)
        client(request)
        client(request)
        asServer.stop()
    }

    @Test
    fun `can make call`() {
        val response = client(Request(POST, "http://localhost:$port/someUri")
            .query("query", "123")
            .header("header", "value").body("body"))

        assertThat(response.status, equalTo(OK))
        assertThat(response.header("uri"), equalTo("/someUri?query=123"))
        assertThat(response.header("query"), equalTo("123"))
        assertThat(response.header("header"), equalTo("value"))
        assertThat(response.bodyString(), equalTo("body"))
    }

    @Test
    fun `performs simple GET request`() {
        val response = client(Request(GET, "http://localhost:$port/echo").query("name", "John Doe"))

        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), containsSubstring("/echo?name=John+Doe"))
    }

    @Test
    fun `performs simple POST request`() {
        val response = client(Request(POST, "http://localhost:$port/echo").body("foobar"))

        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), containsSubstring("foobar"))
    }

    @Test
    open fun `performs simple POST request - stream`() {
        val response = client(Request(POST, "http://localhost:$port/echo").body("foobar".byteInputStream(), 6))

        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), containsSubstring("foobar"))
    }

    @Test
    fun `performs simple DELETE request`() {

        val response = client(Request(DELETE, "http://localhost:$port/echo"))

        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), containsSubstring("delete"))
    }

    @Test
    fun `does not follow redirects`() {
        val response = client(Request(GET, "http://localhost:$port/redirect"))

        assertThat(response.status, equalTo(Status.FOUND))
        assertThat(response.header("location"), equalTo("/someUri"))
    }

    @Test
    fun `does not store cookies`() {
        client(Request(GET, "http://localhost:$port/cookies/set").query("name", "foo").query("value", "bar"))

        val response = client(Request(GET, "http://localhost:$port/cookies"))

        assertThat(response.status.successful, equalTo(true))
        assertThat(response.bodyString(), !containsSubstring("foo"))
    }

    @Test
    fun `filters enable cookies and redirects`() {
        val enhancedClient = ClientFilters.FollowRedirects().then(ClientFilters.Cookies()).then(client)

        val response = enhancedClient(Request(GET, "http://localhost:$port/cookies/set").query("name", "foo").query("value", "bar"))

        assertThat(response.status.successful, equalTo(true))
        assertThat(response.bodyString(), containsSubstring("foo"))
    }

    @Test
    fun `empty body`() {
        val response = client(Request(GET, "http://localhost:$port/empty"))
        assertThat(response.status.successful, equalTo(true))
        assertThat(response.bodyString(), equalTo(""))
    }

    @Test
    fun `redirection response`() {
        val response = ClientFilters.FollowRedirects()
            .then(client)(Request(GET, "http://localhost:$port/relative-redirect/5"))
        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), anything)
    }

    @Test
    fun `send binary data`() {
        val response = client(Request(POST, "http://localhost:$port/check-image").body(Body(ByteBuffer.wrap(testImageBytes()))))
        assertThat(response.status, equalTo(OK))
    }

    @Test
    @Disabled
    open fun `socket timeouts are converted into 504`() {
        val response = timeoutClient(Request(GET, "http://localhost:$port/delay/150"))

        assertThat(response.status, equalTo(Status.CLIENT_TIMEOUT))
    }

    @Test
    open fun `connection refused are converted into 503`() {
        val response = client(Request(GET, "http://localhost:1"))

        assertThat(response.status, equalTo(Status.CONNECTION_REFUSED))
    }

    @Test
    open fun `unknown host are converted into 503`() {
        val response = client(Request(GET, "http://foobar.bill"))

        assertThat(response.status, equalTo(Status.UNKNOWN_HOST))
    }

    @Test
    fun `can retrieve body for different statuses`() {
        listOf(200, 301, 404, 500).forEach { statusCode ->
            val response = client(Request(GET, "http://localhost:$port/status/$statusCode"))
            assertThat(response.status, equalTo(Status(statusCode, "")))
            assertThat(response.bodyString(), equalTo("body for status $statusCode"))
        }
    }

    @Test
    open fun `handles response with custom status message`() {
        listOf(200, 301, 404, 500).forEach { statusCode ->
            val response = client(Request(GET, "http://localhost:$port/status/$statusCode"))
            response.use {
                assertThat(response.status.description, equalTo("Description for $statusCode"))
            }
        }
    }

    @Test
    fun `handles empty response body for different statuses`() {
        listOf(200, 301, 400, 404, 500).forEach { statusCode ->
            val response = client(Request(GET, "http://localhost:$port/status-no-body/$statusCode"))
            assertThat(response.status, equalTo(Status(statusCode, "")))
            assertThat(response.bodyString(), equalTo(""))
        }
    }

    @Test
    fun `requests have expected headers`() {
        fun checkNoBannedHeaders(m: Method, vararg banned: String) {
            val response = client(Request(m, "http://localhost:$port/headers"))
            val bannedHeaders = banned.intersect(response.bodyString().split(","))
            println("$m contained headers ${response.bodyString().split(",")}")
            assertThat("$m contained banned headers $bannedHeaders", bannedHeaders.isEmpty(), equalTo(true))
            response.close()
        }
        checkNoBannedHeaders(GET, "Transfer-encoding")
        checkNoBannedHeaders(TRACE, "Transfer-encoding")
        checkNoBannedHeaders(OPTIONS, "Transfer-encoding")
        checkNoBannedHeaders(DELETE, "Transfer-encoding")
        checkNoBannedHeaders(POST)
        checkNoBannedHeaders(PUT)
    }
}