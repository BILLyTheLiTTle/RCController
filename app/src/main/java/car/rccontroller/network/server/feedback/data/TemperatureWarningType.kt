package car.rccontroller.network.server.feedback.data

import car.rccontroller.network.EMPTY_STRING

enum class TemperatureWarningType(val id: String) {
    NOTHING(EMPTY_STRING),
    UNCHANGED("unchanged"),
    NORMAL("normal"),
    MEDIUM("medium"),
    HIGH("high")
}