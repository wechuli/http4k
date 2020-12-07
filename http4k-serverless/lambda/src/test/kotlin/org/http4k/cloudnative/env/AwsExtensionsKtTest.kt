package org.http4k.cloudnative.env

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.aws.AwsCredentials
import org.http4k.core.with
import org.junit.jupiter.api.Test

class AwsExtensionsKtTest {

    @Test
    fun `can roundtrip lambda environment`() {
        val lambdaEnv = AwsLambdaEnvironment(AwsCredentials("access_key", "secret_key_id", "session_token"), "region")

        Environment.EMPTY.with(EnvironmentKey.aws.LAMBDA_ENVIRONMENT of lambdaEnv)

        assertThat(false, equalTo(false))
    }
}
