package car.rccontroller.network.server.feedback.data

enum class CarPartTemperature(val id: String) {
    MOTOR_REAR_LEFT("${CarPart.MOTOR_REAR_LEFT.id}_temp"),
    MOTOR_REAR_RIGHT("${CarPart.MOTOR_REAR_RIGHT.id}_temp"),
    MOTOR_FRONT_LEFT("${CarPart.MOTOR_FRONT_LEFT.id}_temp"),
    MOTOR_FRONT_RIGHT("${CarPart.MOTOR_FRONT_RIGHT.id}_temp"),
    H_BRIDGE_REAR("${CarPart.H_BRIDGE_REAR.id}_temp"),
    H_BRIDGE_FRONT("${CarPart.H_BRIDGE_FRONT.id}_temp"),
    RASPBERRY_PI("${CarPart.RASPBERRY_PI.id}_temp"),
    BATTERIES("${CarPart.BATTERIES.id}_temp"),
    SHIFT_REGISTERS("${CarPart.SHIFT_REGISTERS.id}_temp")
}