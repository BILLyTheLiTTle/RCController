package car.rccontroller.network.server.feedback.data

// The hardware parts of the car
enum class CarPart(val id: String) {
    MOTOR_REAR_LEFT("motor_rear_left"),
    MOTOR_REAR_RIGHT("motor_rear_right"),
    MOTOR_FRONT_LEFT("motor_front_left"),
    MOTOR_FRONT_RIGHT("motor_front_right"),
    H_BRIDGE_REAR("h_bridge_rear"),
    H_BRIDGE_FRONT("h_bridge_front"),
    RASPBERRY_PI("raspberry_pi"),
    BATTERIES("batteries"),
    SHIFT_REGISTERS("shift_registers")
}