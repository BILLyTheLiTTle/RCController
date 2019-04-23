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
    SHIFT_REGISTERS("shift_registers");

    enum class MainLight(val id: String) {
        LIGHTS_OFF("lights_off"),
        POSITION_LIGHTS("lights_position"),
        DRIVING_LIGHTS("lights_driving"),
        LONG_RANGE_LIGHTS("lights_long_range"),
        LONG_RANGE_SIGNAL_LIGHTS("lights_long_range_signal")
    }

    enum class DirectionLight(val id:String){
        DIRECTION_LIGHTS_RIGHT("lights_direction_right"),
        DIRECTION_LIGHTS_LEFT("lights_direction_left"),
        DIRECTION_LIGHTS_STRAIGHT("lights_direction_straight");
    }

    enum class Other(val id:String){
        BRAKING_LIGHTS("lights_braking"),
        REVERSE_LIGHTS("lights_reverse"),
        EMERGENCY_LIGHTS("lights_emergency")
    }
}