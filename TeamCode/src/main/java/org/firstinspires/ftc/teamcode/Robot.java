package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

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

    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backLeft;
    public DcMotorEx backRight;

    public Robot(HardwareMap hardwareMap) {
        frontLeft  = hardwareMap.get(DcMotorEx.class, "leftfront");
        frontRight = hardwareMap.get(DcMotorEx.class, "rightfront");
        backLeft   = hardwareMap.get(DcMotorEx.class, "leftback");
        backRight  = hardwareMap.get(DcMotorEx.class, "rightback");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void driveMecanum(double axial, double lateral, double yaw) {
        double denominator = Math.max(Math.abs(axial) + Math.abs(lateral) + Math.abs(yaw), 1.0);

        double flPower = (axial + lateral + yaw) / denominator;
        double frPower = (axial - lateral - yaw) / denominator;
        double blPower = (axial - lateral + yaw) / denominator;
        double brPower = (axial + lateral - yaw) / denominator;

        frontLeft.setPower(flPower);
        frontRight.setPower(frPower);
        backLeft.setPower(blPower);
        backRight.setPower(brPower);
    }
}