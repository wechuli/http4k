package org.http4k.servirtium

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_IMPLEMENTED
import org.http4k.core.Uri
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.http4k.testing.assertApproved
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import java.nio.file.Files

@ExtendWith(ApprovalTest::class)
class ServirtiumReplayServerTest : TestContract {

    override val uri get() = Uri.of("http://localhost:${replay.port()}")

    private val root = Files.createTempDirectory(".").toFile().apply { deleteOnExit() }
    private lateinit var replay: ServirtiumReplayServer

    override val control by lazy { replay }

    @BeforeEach
    fun start(info: TestInfo) {
        javaClass.getResourceAsStream("/org/http4k/servirtium/ServirtiumReplayServerTest.txt").reader().use { r ->
            File(root, "${info.displayName}.md").writer().use { r.copyTo(it) }
        }

        replay = ServirtiumReplayServer(
            info.displayName,
            root,
            requestManipulations = { it.removeHeader("Host").removeHeader("User-agent") }
        )
        replay.start()
    }

    @AfterEach
    fun stop() {
        replay.stop()
    }

    @Test
    fun `unexpected content`(approver: Approver) {
        approver.assertApproved(createHandler()(Request(GET, "/foo")), NOT_IMPLEMENTED)
    }

    @Test
    fun `too many requests`(approver: Approver) {
        super.scenario()
        val httpMessage = createHandler()(Request(GET, "/foo")).run {
            body(bodyString()
                .replace(Regex("Host.*"), "Host: localhost")
                .replace(Regex("User-agent.*"), "User-agent: bob")
            )
        }

        approver.assertApproved(httpMessage, NOT_IMPLEMENTED)
    }
}
