package car.rccontroller.network

import fi.iki.elonen.NanoHTTPD

// constructor default parameters are for emulator
class Server(val ip: String = "localhost", val port: Int = 8081) : NanoHTTPD(ip, port) {

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri

        return when (uri) {
            "/a_uri" -> {
                // TODO an action
                newFixedLengthResponse(OK_DATA)
        }
            else -> newFixedLengthResponse(NO_DATA)
        }
    }
}