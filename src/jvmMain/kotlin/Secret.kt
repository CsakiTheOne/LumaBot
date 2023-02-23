import java.time.LocalDateTime

class Secret {
    companion object {

        private val appId = "1069976226535571497"
        private val token = "MTA2OTk3NjIyNjUzNTU3MTQ5Nw.GMawdN.tpYW5AM4Wb1Uoyo7KfUmiV-avCxgB1IXIkDNTA"

        fun requestInviteUrl(): String {
            println("Invite URL requested at ${LocalDateTime.now()}")
            return "https://discord.com/oauth2/authorize?client_id=${appId}&scope=bot&permissions=8"
        }

        fun requestToken(): String {
            println("Token requested at ${LocalDateTime.now()}")
            return token
        }

    }
}