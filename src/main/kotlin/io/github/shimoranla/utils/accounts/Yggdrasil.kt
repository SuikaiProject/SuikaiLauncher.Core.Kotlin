package io.github.shimoranla.utils.accounts

import io.github.shimoranla.utils.basic.*
import io.github.shimoranla.utils.basic.net.HttpRequestOptions
import io.github.shimoranla.utils.basic.net.HttpWebRequest


fun login(yggdrasilApiAddressOrNideId: String, account: YggdrasilAccount,loginType: LoginType){
    val request: HttpRequestOptions =
        HttpRequestOptions(
            (
                    if (loginType == LoginType.NideAuth) "https://auth.mc-user.com:233"
                            + yggdrasilApiAddressOrNideId
                    else yggdrasilApiAddressOrNideId)
                    + "/authserver/authenticate",
            "POST"
        )
    request.withRequestData(Text.JsonResolver.toJson(account))
    HttpWebRequest.getServerResponse(request)
}