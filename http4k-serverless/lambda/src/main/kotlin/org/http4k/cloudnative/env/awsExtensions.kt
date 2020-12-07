package org.http4k.cloudnative.env

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.cloudnative.env.EnvironmentKey.optional
import org.http4k.cloudnative.env.EnvironmentKey.required
import org.http4k.core.with
import org.http4k.lens.BiDiLens
import org.http4k.lens.composite

data class AwsLambdaEnvironment(val credentials: AwsCredentials, val region: String) {
    fun scopeFor(service: String) = AwsCredentialScope(region, service)
}

private val AWS_ACCESS_KEY_ID = required("AWS_ACCESS_KEY_ID")
private val AWS_SECRET_ACCESS_KEY = required("AWS_SECRET_ACCESS_KEY")
private val AWS_SESSION_TOKEN = optional("AWS_SESSION_TOKEN")
private val AWS_REGION = required("AWS_REGION")

val EnvironmentKey.aws.LAMBDA_ENVIRONMENT: BiDiLens<Environment, AwsLambdaEnvironment>
    get() = EnvironmentKey.composite({
        AwsLambdaEnvironment(AwsCredentials(AWS_ACCESS_KEY_ID(it), AWS_SECRET_ACCESS_KEY(it), AWS_SESSION_TOKEN(it)), AWS_REGION(it))
    }, {
        it.with(
            AWS_ACCESS_KEY_ID of credentials.accessKey,
            AWS_SECRET_ACCESS_KEY of credentials.secretKey,
            AWS_SESSION_TOKEN of credentials.sessionToken,
            AWS_REGION of region,
        )
    })

