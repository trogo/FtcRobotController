package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.crossbotics.ftchelper.util.Angles;

@TeleOp(name = "Helper Check")
public class HelperCheck extends LinearOpMode {
    @Override
    public void runOpMode() {
        telemetry.addData("Library works", Angles.shortestDiff(-170, 170) == 20);
        telemetry.update();
        waitForStart();
    }
}