package io.github.shimoranla.utils.accounts

import com.microsoft.aad.msal4j.IPublicClientApplication
import com.microsoft.aad.msal4j.PublicClientApplication
import com.microsoft.aad.msal4jbrokers.Broker

class MicrosoftAccount {
    companion object{
        private var oauthClient: IPublicClientApplication? = null
        fun login(){

        }
        private fun loginWithWAM(){
            oauthClient.acquireToken()
        }
        fun resetClient(id: String,appName: String = "SuikaiLauncher.Core"){
            oauthClient = null
            oauthClient = PublicClientApplication.builder(id)
                .authority("https://login.microsoftonline.com/consumers")
                .applicationName(appName)
                .broker(Broker.Builder().supportWindows(true).build())
                .build()
        }
        private fun loginWithAuthorizeCode(){

        }
        private fun loginWithDeviceCode(){

        }
        private fun refreshToken(){

        }
    }
}