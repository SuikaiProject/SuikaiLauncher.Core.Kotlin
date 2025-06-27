package io.github.shimoranla.utils.accounts

data class YggdrasilAccount(
    val username: String,
    val password:String,
    val agent: Agent
)

data class Agent(
    val name: String = "Minecraft",
    val version:Int = 1
)
