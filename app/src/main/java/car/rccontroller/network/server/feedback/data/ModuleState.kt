package car.rccontroller.network.server.feedback.data

import car.rccontroller.network.EMPTY_STRING

enum class ModuleState(val id: String) {
    NOTHING(EMPTY_STRING),
    OFF("module_off_state"),
    ON("module_on_state"),
    IDLE("module_idle_state"),
    UNCHANGED("module_unchanged_state")
}