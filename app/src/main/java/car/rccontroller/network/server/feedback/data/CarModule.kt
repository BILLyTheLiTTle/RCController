package car.rccontroller.network.server.feedback.data

enum class CarModule(val id: String) {
    TRACTION_CONTROL("TCM"),
    ANTILOCK_BRAKING("ABM"),
    ELECTRONIC_STABILITY("ESM"),
    UNDERSTEER_DETECTION("UDM"),
    OVERSTEER_DETECTION("ODM"),
    COLLISION_DETECTION("CDM")
}