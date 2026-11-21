package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Mecanum Auto Movement", group = "Auto")
public class MecanumAutoStrafe extends LinearOpMode {

    private Robot robot;
    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        robot = new Robot(hardwareMap);

        setDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized - Ready for Auto");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Example Auto Path:
            // 1. Drive forward 24 inches (1 full field tile)
            driveInches(0.6, 24.0, 4.0);
            sleep(250);

            // 2. Strafe RIGHT 24 inches
            strafeInches(0.6, 24.0, 4.0);
            sleep(250);

            // 3. Turn 90 degrees counter-clockwise (left)
            turnDegrees(0.5, 90.0, 3.0);
            sleep(250);

            // 4. Drive backward 12 inches
            driveInches(0.5, -12.0, 3.0);
        }
    }

    /**
     * Drives straight forward (+) or backward (-).
     */
    public void driveInches(double speed, double inches, double timeoutSeconds) {
        int ticks = (int) Math.round(inches * Robot.TICKS_PER_INCH);
        moveToTargets(speed, ticks, ticks, ticks, ticks, timeoutSeconds);
    }

    /**
     * Strafes laterally right (+) or left (-).
     */
    public void strafeInches(double speed, double inches, double timeoutSeconds) {
        int ticks = (int) Math.round(inches * Robot.TICKS_PER_INCH * Robot.LATERAL_MULTIPLIER);
        moveToTargets(speed, ticks, -ticks, -ticks, ticks, timeoutSeconds);
    }

    /**
     * Turns in place counter-clockwise/left (+) or clockwise/right (-) in degrees.
     */
    public void turnDegrees(double speed, double degrees, double timeoutSeconds) {
        // Arc length each wheel travels around the track-width circle
        double arcInches = (Math.toRadians(degrees) * (Robot.TRACK_WIDTH_INCHES / 2.0)) * Robot.TURN_MULTIPLIER;
        int ticks = (int) Math.round(arcInches * Robot.TICKS_PER_INCH);
        // Left wheels reverse (-), right wheels go forward (+) for counter-clockwise turn
        moveToTargets(speed, -ticks, ticks, -ticks, ticks, timeoutSeconds);
    }

    private void moveToTargets(double speed, int flDelta, int frDelta, int blDelta, int brDelta, double timeoutSeconds) {
        if (!opModeIsActive()) return;

        int flTarget = robot.frontLeft.getCurrentPosition()  + flDelta;
        int frTarget = robot.frontRight.getCurrentPosition() + frDelta;
        int blTarget = robot.backLeft.getCurrentPosition()   + blDelta;
        int brTarget = robot.backRight.getCurrentPosition()  + brDelta;

        robot.frontLeft.setTargetPosition(flTarget);
        robot.frontRight.setTargetPosition(frTarget);
        robot.backLeft.setTargetPosition(blTarget);
        robot.backRight.setTargetPosition(brTarget);

        setDriveMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();
        setAllPower(Math.abs(speed));

        while (opModeIsActive() &&
                runtime.seconds() < timeoutSeconds &&
                robot.frontLeft.isBusy() &&
                robot.frontRight.isBusy() &&
                robot.backLeft.isBusy() &&
                robot.backRight.isBusy()) {

            telemetry.addData("Target", "FL:%7d FR:%7d BL:%7d BR:%7d", flTarget, frTarget, blTarget, brTarget);
            telemetry.addData("Actual", "FL:%7d FR:%7d BL:%7d BR:%7d",
                    robot.frontLeft.getCurrentPosition(),
                    robot.frontRight.getCurrentPosition(),
                    robot.backLeft.getCurrentPosition(),
                    robot.backRight.getCurrentPosition());
            telemetry.update();
        }

        setAllPower(0.0);
        setDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void setDriveMode(DcMotor.RunMode mode) {
        robot.frontLeft.setMode(mode);
        robot.frontRight.setMode(mode);
        robot.backLeft.setMode(mode);
        robot.backRight.setMode(mode);
    }

    private void setAllPower(double power) {
        robot.frontLeft.setPower(power);
        robot.frontRight.setPower(power);
        robot.backLeft.setPower(power);
        robot.backRight.setPower(power);
    }
}