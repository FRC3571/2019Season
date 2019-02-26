/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package ca.team3571.offseason;

import ca.team3571.offseason.auto.AutonomousExecutor;
import ca.team3571.offseason.commands.auto.PracticeAuto;
import ca.team3571.offseason.component.CameraController;
import ca.team3571.offseason.component.RobotCamera;
import ca.team3571.offseason.subsystem.DriveTrain;
import ca.team3571.offseason.subsystem.Intake;
import ca.team3571.offseason.subsystem.Tilt;
import ca.team3571.offseason.subsystem.elevator.Elevator;
import ca.team3571.offseason.util.RioDuino;
import ca.team3571.offseason.util.XboxController;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
// If you rename or move this class, update the build.properties file in the project root
public class Robot extends IterativeRobot
{

    private DriveTrain driveTrain;
    private Elevator elevator;
    private Intake intake;
    private Tilt tilt;
    private Logger logger;
    private RioDuino rioDuino;
    private CameraController cameraController;
    private XboxController subsystemController = new XboxController(1);
    private static Robot exposedInstance;

    /**
     * Exposes instance once it's ready and populated
     * @return singleton instance of robot
     */
    public static Robot getInstance() {
        if(exposedInstance==null) {
            throw new IllegalStateException("#robotInit must finish its invocation!");
        }
        return exposedInstance;
    }

    public XboxController getSubsystemController() {
        return subsystemController;
    }

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        //initialize subsystems
        driveTrain = new DriveTrain();
        elevator = new Elevator();
        intake = new Intake();
        tilt = new Tilt();

        //logger
        logger = Logger.getLogger(getClass().getName());

        //rio
        rioDuino = new RioDuino();

        //set reference for exposed instance
        exposedInstance = this;

        runCamera();
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different autonomous modes using the dashboard. The sendable
     * chooser code works with the Java SmartDashboard. If you prefer the
     * LabVIEW Dashboard, remove all of the chooser code and uncomment the
     * getString line to get the auto name from the text box below the Gyro
     *
     * <p>You can add additional auto modes by adding additional comparisons to
     * the switch structure below with additional strings. If using the
     * SendableChooser make sure to add them to the chooser code above as well.
     */
    @Override
    public void autonomousInit() {
//        boolean signalReceived = false;
//        String signal = null;
//        while(!signalReceived) {
//            signal = DriverStation.getInstance().getGameSpecificMessage();
//            if(signal.length()==AutonomousExecutor.SIGNAL_LENGTH) {
//                signalReceived = true;
//            }
//        }
//        new AutonomousExecutor().accept(signal);
        new PracticeAuto().start();
    }

    /**
     * This function is called periodically during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run(); //dont delete this u idiot
    }

    /**
     * This function is called periodically during operator control.
     * gets called every 20 millis
     */
    @Override
    public void teleopPeriodic() {
        driveTrain.refresh();
        elevator.refresh();
        intake.refresh();
        tilt.refresh();
        debug();
    }

    /**
     * This function is called periodically during test mode.
     */
    @Override
    public void testPeriodic() {
        new Thread() {
            @Override
            public void start() {
                while(true) {
                    System.out.println(rioDuino.receiveData());
                    for(byte b: rioDuino.getRaw()) {
                        System.out.print(" "+(int)b);
                    }
                    try {
                        sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void runCamera() {
        UsbCamera cameraOne = CameraServer.getInstance().startAutomaticCapture(0);
        UsbCamera cameraTwo = CameraServer.getInstance().startAutomaticCapture(1);
        //UsbCamera cameraThree = CameraServer.getInstance().startAutomaticCapture(2);
        cameraOne.setWhiteBalanceAuto();
        cameraTwo.setWhiteBalanceAuto();
        //cameraThree.setWhiteBalanceAuto();

        cameraController = new CameraController();//new RobotCamera(cameraOne), new RobotCamera(cameraTwo));
        cameraController.addCamera(new RobotCamera(cameraOne));
        cameraController.addCamera(new RobotCamera(cameraTwo));
        //cameraController.addCamera(new RobotCamera(cameraThree));
        cameraController.begin();
        cameraController.setPriority(0);
    }

    private void debug() {
        driveTrain.log();
        elevator.log();
        intake.log();
        tilt.log();
    }

    public void log(Level logLevel, String message) {
        logger.log(logLevel, message);
    }

    //getters
    public DriveTrain getDrive() {
        return driveTrain;
    }

}
