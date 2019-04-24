package car.feedback

/* The hardware parts of the car. This class should not be used as it is.
    Use this class to create new enum ids like @see car.feedback.cockpit.MainLight
 */
enum class CarPart(val id: String) {
    MOTOR_REAR_LEFT("motor_rear_left"),
    MOTOR_REAR_RIGHT("motor_rear_right"),
    MOTOR_FRONT_LEFT("motor_front_left"),
    MOTOR_FRONT_RIGHT("motor_front_right"),
    H_BRIDGE_REAR("h_bridge_rear"),
    H_BRIDGE_FRONT("h_bridge_front"),
    RASPBERRY_PI("raspberry_pi"),
    BATTERIES("batteries"),
    SHIFT_REGISTERS("shift_registers"),
    LIGHTS("lights"),
    DIRECTION_LIGHTS("${LIGHTS.id}_direction");


}