package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Mecanum Drive", group = "TeleOp")
public class MecanumTeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        Robot robot = new Robot(hardwareMap);

        telemetry.addData("Status", "Initialized - Ready to Drive");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Note: gamepad1.left_stick_y is negative when pushed forward
            double axial   = -gamepad1.left_stick_y;  // Forward / Backward (W / S)
            double lateral =  gamepad1.left_stick_x;  // Strafe Left / Right (A / D)
            double yaw     =  gamepad1.right_stick_x; // Rotate Left / Right (J / L)

            robot.driveMecanum(axial, lateral, yaw);

            telemetry.addData("Drive", "axial=%.2f, lateral=%.2f, yaw=%.2f", axial, lateral, yaw);
            telemetry.update();
        }
    }
}