package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Auto: Mecanum Movement Easy", group = "Autonomous")
public class MecanumAutoStrafeSafe extends LinearOpMode {

    private Robot robot;
    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        robot = new Robot(hardwareMap);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Auto Initialized - Power + Encoder Mode");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Strategic Autonomous Sequence Execution
            driveInches(24.0, 0.5, 3.0);    // Step 1: Drive Forward 24 inches
            sleep(250);
            strafeInches(18.0, 0.5, 3.0);   // Step 2: Strafe Right 18 inches
            sleep(250);
            turnDegrees(90.0, 0.4, 3.0);    // Step 3: Turn 90 Degrees Counter-Clockwise
            sleep(250);
            driveInches(-12.0, 0.5, 2.0);   // Step 4: Reverse 12 inches
        }

        stopMotors();
    }

    /**
     * Drives forward (+inches) or backward (-inches) using direct motor powers
     * and encoder tick feedback.
     */
    public void driveInches(double inches, double power, double timeoutSeconds) {
        int targetTicks = (int) Math.abs(inches * Robot.TICKS_PER_INCH);
        double dir = (inches >= 0) ? 1.0 : -1.0;
        double speed = Math.abs(power) * dir;

        // Forward: (+, +, +, +) | Reverse: (-, -, -, -)
        moveWithPower(targetTicks, speed, speed, speed, speed, timeoutSeconds);
    }

    /**
     * Strafes right (+inches) or left (-inches) using direct motor powers
     * and encoder tick feedback.
     */
    public void strafeInches(double inches, double power, double timeoutSeconds) {
        int targetTicks = (int) Math.abs(inches * Robot.TICKS_PER_INCH * Robot.LATERAL_MULTIPLIER);
        double dir = (inches >= 0) ? 1.0 : -1.0;
        double speed = Math.abs(power) * dir;

        // Strafe Right: (+, -, -, +) | Strafe Left: (-, +, +, -)
        moveWithPower(targetTicks, speed, -speed, -speed, speed, timeoutSeconds);
    }

    /**
     * Turns counter-clockwise (+degrees) or clockwise (-degrees) using direct motor powers
     * and encoder tick feedback.
     */
    public void turnDegrees(double degrees, double power, double timeoutSeconds) {
        double radians = Math.toRadians(Math.abs(degrees));
        double turnArcInches = (Robot.TRACK_WIDTH_INCHES / 2.0) * radians;
        int targetTicks = (int) (turnArcInches * Robot.TICKS_PER_INCH * Robot.TURN_MULTIPLIER);

        double dir = (degrees >= 0) ? 1.0 : -1.0;
        double speed = Math.abs(power) * dir;

        // Counter-Clockwise: (-, +, -, +) | Clockwise: (+, -, +, -)
        moveWithPower(targetTicks, -speed, speed, -speed, speed, timeoutSeconds);
    }

    /**
     * Resets encoders, sets explicit motor power on all 4 wheels, and waits until
     * the average distance traveled reaches targetTicks.
     */
    private void moveWithPower(int targetTicks, double flPower, double frPower, double blPower, double brPower, double timeoutSeconds) {
        if (!opModeIsActive()) return;

        // Reset encoders to 0 and keep motors in RUN_USING_ENCODER (velocity PID) mode
        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);

        runtime.reset();

        // Explicitly set power on each motor
        robot.frontLeft.setPower(flPower);
        robot.frontRight.setPower(frPower);
        robot.backLeft.setPower(blPower);
        robot.backRight.setPower(brPower);

        // Loop until average encoder ticks across all 4 wheels reaches targetTicks
        while (opModeIsActive() && (runtime.seconds() < timeoutSeconds)) {
            int flPos = Math.abs(robot.frontLeft.getCurrentPosition());
            int frPos = Math.abs(robot.frontRight.getCurrentPosition());
            int blPos = Math.abs(robot.backLeft.getCurrentPosition());
            int brPos = Math.abs(robot.backRight.getCurrentPosition());

            int averageTicks = (flPos + frPos + blPos + brPos) / 4;

            if (averageTicks >= targetTicks) {
                break;
            }

            telemetry.addData("Target Ticks", targetTicks);
            telemetry.addData("Avg Ticks", averageTicks);
            telemetry.addData("Encoders", "FL:%d FR:%d BL:%d BR:%d", flPos, frPos, blPos, brPos);
            telemetry.update();
        }

        // Immediately brake all motors once target distance is reached
        stopMotors();
    }

    private void setDriveMode(DcMotor.RunMode mode) {
        robot.frontLeft.setMode(mode);
        robot.frontRight.setMode(mode);
        robot.backLeft.setMode(mode);
        robot.backRight.setMode(mode);
    }

    private void stopMotors() {
        robot.frontLeft.setPower(0.0);
        robot.frontRight.setPower(0.0);
        robot.backLeft.setPower(0.0);
        robot.backRight.setPower(0.0);
    }
}
