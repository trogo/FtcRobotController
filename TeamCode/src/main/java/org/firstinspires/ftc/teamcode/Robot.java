package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Robot {
    // Physical drivetrain constants (goBILDA 312 RPM Yellow Jacket + 104mm GripForce Mecanum)
    public static final double TICKS_PER_MOTOR_REV   = 537.7;
    public static final double DRIVE_GEAR_REDUCTION  = 1.0;
    public static final double WHEEL_DIAMETER_INCHES = 4.09;  // 104 mm
    public static final double TRACK_WIDTH_INCHES    = 16.34; // From simulator/robot.properties (415 mm)
    public static final double TICKS_PER_INCH        =
            (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);

    // Empirical scrub multipliers (tune on the field/simulator as needed)
    public static final double LATERAL_MULTIPLIER    = 1.12;
    public static final double TURN_MULTIPLIER       = 1.05;

    // Drivetrain motors
    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;

    // Intake mechanism
    public DcMotorEx intake;
    public DcMotorEx intakeMotor;          // Alias to intake
    public CRServo leftIntakeServo;
    public CRServo rightIntakeServo;
    public Servo intakeServo;              // Optional positional intake servo

    // Outtake / Launcher mechanism
    public DcMotorEx outtake;
    public DcMotorEx launcher;             // Alias to outtake
    public CRServo outtakeServo;           // Feeder / Windmill CRServo
    public CRServo windmillServo;          // Alias to outtakeServo
    public Servo outtakePositionServo;     // Optional positional outtake servo

    public Robot() {
    }

    public Robot(HardwareMap hardwareMap) {
        init(hardwareMap);
    }

    public void init(HardwareMap hardwareMap) {
        // --- Drivetrain Initialization ---
        frontLeft  = getHardware(hardwareMap, DcMotorEx.class, "leftfront", "left_front_drive");
        frontRight = getHardware(hardwareMap, DcMotorEx.class, "rightfront", "right_front_drive");
        backLeft   = getHardware(hardwareMap, DcMotorEx.class, "leftback", "left_back_drive");
        backRight  = getHardware(hardwareMap, DcMotorEx.class, "rightback", "right_back_drive");

        if (frontLeft != null) frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        if (backLeft != null) backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        if (frontRight != null) frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        if (backRight != null) backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        if (frontLeft != null) frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        if (frontRight != null) frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        if (backLeft != null) backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        if (backRight != null) backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // --- Intake Initialization ---
        intake = getHardware(hardwareMap, DcMotorEx.class, "intake", "intakeMotor", "intake_motor");
        intakeMotor = intake;
        if (intake != null) {
            intake.setDirection(DcMotorSimple.Direction.FORWARD);
            intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        leftIntakeServo = getHardware(hardwareMap, CRServo.class, "left_intake_servo", "leftIntakeServo", "intake_servo_left");
        rightIntakeServo = getHardware(hardwareMap, CRServo.class, "right_intake_servo", "rightIntakeServo", "intake_servo_right");
        if (rightIntakeServo != null) {
            rightIntakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        intakeServo = getHardware(hardwareMap, Servo.class, "intakeServo", "intake_servo", "intakePitch");

        // --- Outtake / Launcher Initialization ---
        outtake = getHardware(hardwareMap, DcMotorEx.class, "launcher", "outtake", "flywheel", "outtakeMotor", "outtake_motor");
        launcher = outtake;
        if (outtake != null) {
            outtake.setDirection(DcMotorSimple.Direction.FORWARD);
            outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        outtakeServo = getHardware(hardwareMap, CRServo.class, "windmillServo", "windmill", "outtakeServo", "feederServo", "crServo");
        windmillServo = outtakeServo;
        if (outtakeServo != null) {
            outtakeServo.setDirection(DcMotorSimple.Direction.REVERSE);
        }

        outtakePositionServo = getHardware(hardwareMap, Servo.class, "outtakeServo", "outtake_servo", "pollenServo", "claw", "wrist");
    }

    /**
     * Safely queries hardwareMap with one or more possible configuration names.
     */
    private <T> T getHardware(HardwareMap hardwareMap, Class<T> clazz, String... names) {
        if (hardwareMap == null) return null;
        for (String name : names) {
            try {
                return hardwareMap.get(clazz, name);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    // --- Intake Controls ---
    public void setIntakePower(double power) {
        if (intake != null) intake.setPower(power);
        if (leftIntakeServo != null) leftIntakeServo.setPower(power);
        if (rightIntakeServo != null) rightIntakeServo.setPower(power);
    }

    public void stopIntake() {
        setIntakePower(0.0);
    }

    // --- Outtake / Launcher Controls ---
    public void setOuttakePower(double power) {
        if (outtake != null) outtake.setPower(power);
    }

    public void setFeederPower(double power) {
        if (outtakeServo != null) outtakeServo.setPower(power);
    }

    public void stopOuttake() {
        if (outtake != null) outtake.setPower(0.0);
        if (outtakeServo != null) outtakeServo.setPower(0.0);
    }

    // --- Drivetrain Controls ---
    public void stopMotors() {
        if (frontLeft != null) frontLeft.setPower(0);
        if (frontRight != null) frontRight.setPower(0);
        if (backLeft != null) backLeft.setPower(0);
        if (backRight != null) backRight.setPower(0);
    }

    public void stopAll() {
        stopMotors();
        stopIntake();
        stopOuttake();
    }

    public void stopAndResetEncoderMode() {
        if (frontLeft != null) {
            frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        if (frontRight != null) {
            frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        if (backLeft != null) {
            backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        if (backRight != null) {
            backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void driveMecanum(double axial, double lateral, double yaw) {
        double denominator = Math.max(Math.abs(axial) + Math.abs(lateral) + Math.abs(yaw), 1.0);

        double flPower = (axial + lateral + yaw) / denominator;
        double frPower = (axial - lateral - yaw) / denominator;
        double blPower = (axial - lateral + yaw) / denominator;
        double brPower = (axial + lateral - yaw) / denominator;

        if (frontLeft != null) frontLeft.setPower(flPower);
        if (frontRight != null) frontRight.setPower(frPower);
        if (backLeft != null) backLeft.setPower(blPower);
        if (backRight != null) backRight.setPower(brPower);
    }
}
