package io.github.shimoranla.utils.accounts

class Profile (
    var username:String,
    val uuid:String,
    var skin:String,
    var cape:String,
    var accessToken:String,
    var accountType: LoginType,
    var expiredIn:Long,
    var isSelected: Boolean,
)

enum class LoginType {
    Offline,
    Microsoft,
    AuthLib,
    NideAuth
}

class ProfileManager{
    init {

    }
}