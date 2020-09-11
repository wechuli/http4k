package tutorials.hexagonal

import org.http4k.client.OkHttp
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters

fun localHttpClientOn(port: Int) =
    ClientFilters.SetBaseUriFrom(Uri.of("http://localhost:$port")).then(OkHttp())
