package ca.team3571.offseason.commands;

import ca.team3571.offseason.Robot;
import ca.team3571.offseason.util.RobotMath;
import edu.wpi.first.wpilibj.command.Command;


public class TurnWithDegrees extends Command {

    private static final double TURN_RADIUS = 315;

    private double distance;
    private double leftSpeed = 0.7;
    private double rightSpeed = 0.7;

    public TurnWithDegrees(double degrees) {
        if(degrees<0) {
            leftSpeed *= -1;
        }
        else {
            rightSpeed *= -1;
        }

        requires(Robot.getInstance().getDrive());

        distance = RobotMath.getDistanceFromDegrees(degrees, TURN_RADIUS);

    }

    @Override
    protected void initialize() {
        Robot.getInstance().getDrive().reset();
        Robot.getInstance().getDrive().drive(leftSpeed, rightSpeed);
    }

    @Override
    protected boolean isFinished() {
        // TODO Auto-generated method stub
        return Robot.getInstance().getDrive().getTurnDistance() >= distance;
    }

    @Override
    protected void end() {
        Robot.getInstance().getDrive().drive(0, 0);
    }

}
