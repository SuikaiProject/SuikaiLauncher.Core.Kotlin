package io.github.shimoranla.utils.accounts

import com.microsoft.aad.msal4j.AuthorizationCodeParameters
import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters
import com.microsoft.aad.msal4j.DeviceCode
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters
import com.microsoft.aad.msal4j.IPublicClientApplication
import com.microsoft.aad.msal4j.InteractiveRequestParameters
import com.microsoft.aad.msal4j.PublicClientApplication
import com.microsoft.aad.msal4jbrokers.Broker
import java.net.URI
import java.util.function.Consumer

class MicrosoftAccount {
    companion object{
        private var oauthClient: IPublicClientApplication? = null
        var customCallback: Consumer<DeviceCode>? = null
        fun login(){

        }
        fun setCustomCallback(action: Consumer<DeviceCode>){
            customCallback = action
        }
        fun defaultDeviceCodeCallback(code: DeviceCode){
            if(customCallback != null) customCallback?.accept(code)
        }
        private fun loginWithWAM(){
            oauthClient?.acquireToken(InteractiveRequestParameters.builder(
                URI("https://login.microsoftonline.com/common/oauth2/nativeclient")
            ).scopes(setOf("XboxLive.Signin","offline_access")).build())
        }
        fun resetClient(id: String,appName: String = "SuikaiLauncher.Core"){
            oauthClient = null
            oauthClient = PublicClientApplication.builder(id)
                .authority("https://login.microsoftonline.com/consumers")
                .applicationName(appName)
                .broker(Broker.Builder().supportWindows(true).build())
                .build()
        }
        private fun loginWithAuthorizeCode(code:String){
            java.awt.Desktop.getDesktop().browse(oauthClient?.getAuthorizationRequestUrl(
                AuthorizationRequestUrlParameters
                    .builder("http://localhost",setOf("XboxLive.Signin","offline_access"))
                .build())?.toURI())
            oauthClient?.acquireToken(
                AuthorizationCodeParameters
                    .builder(code,URI("http://localhost"))
                    .scopes(setOf("XboxLive.Signin","offline_access")).build()
            )
        }
        private fun loginWithDeviceCode(){
            oauthClient.acquireToken(
                DeviceCodeFlowParameters.builder(setOf("XboxLive.Signin","offline_access"),{ device: DeviceCode ->
                    defaultDeviceCodeCallback(device)
                    }
                ).build()
            )

        }
        private fun refreshToken(){

        }
    }
}