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
    SHIFT_REGISTERS("shift_registers"),
    LIGHTS("lights"),
    DIRECTION_LIGHTS("${CarPart.LIGHTS.id}_direction");

    enum class Thermometer(val id: String) {
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

    enum class MainLight(val id: String) {
        LIGHTS_OFF("${CarPart.LIGHTS.id}_off"),
        POSITION_LIGHTS("${CarPart.LIGHTS.id}_position"),
        DRIVING_LIGHTS("${CarPart.LIGHTS.id}_driving"),
        LONG_RANGE_LIGHTS("${CarPart.LIGHTS.id}_long_range"),
        LONG_RANGE_SIGNAL_LIGHTS("${CarPart.LIGHTS.id}_long_range_signal")
    }

    enum class DirectionLight(val id:String){
        DIRECTION_LIGHTS_RIGHT("${CarPart.DIRECTION_LIGHTS.id}_right"),
        DIRECTION_LIGHTS_LEFT("${CarPart.DIRECTION_LIGHTS.id}_left"),
        DIRECTION_LIGHTS_STRAIGHT("${CarPart.DIRECTION_LIGHTS.id}_straight");
    }

    enum class Other(val id:String){
        BRAKING_LIGHTS("${CarPart.LIGHTS.id}_braking"),
        REVERSE_LIGHTS("${CarPart.LIGHTS.id}_reverse"),
        EMERGENCY_LIGHTS("${CarPart.LIGHTS.id}_emergency")
    }
}