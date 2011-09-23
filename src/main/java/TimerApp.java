import eu.hansolo.steelseries.extras.Clock;
import eu.hansolo.steelseries.gauges.LinearBargraph;
import org.joda.time.LocalTime;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * User: chgv
 * Date: 9/21/11
 * Time: 9:33 AM
 */
public class TimerApp implements ActionListener {

    //components
    private JPanel mainPanel;
    private JButton startButton;
    private JButton stopButton;
    private JSpinner hours;
    private JSpinner minutes;
    private JSpinner seconds;
    private Clock clock;
    private LinearBargraph bargraph;
    private JButton closeButton;

    //variables
    private Timer timer;
    private LocalTime localTime;
    private boolean isPause = false;
    private int startTime;

    //constants
    private static final int DEFAULT_STEP = 1000;
    private static final String APP_NAME = "TimerApp";
    private final ClassLoader classLoader = this.getClass().getClassLoader();
    private final URL playURL = classLoader.getResource("images/play.png");
    private final URL pauseURL = classLoader.getResource("images/pause.png");


    //entry point
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        JFrame frame = new JFrame(APP_NAME);
        frame.setContentPane(new TimerApp().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    //timer fired event
    public void actionPerformed(ActionEvent e) {
        if (localTime.getMillisOfDay() != 0) {
            localTime = localTime.minusMillis(DEFAULT_STEP);
            bargraph.setValue(100 - getSecondsCount() * 100 / startTime);
            updateTimeLabel();
        } else {
            stopTimer();
        }
    }

    private int getSecondsCount() {
        return localTime.getHourOfDay() * 3600 + localTime.getMinuteOfHour() * 60 + localTime.getSecondOfMinute();
    }

    //init app
    public TimerApp() {
        timer = new Timer(DEFAULT_STEP, this);
        stopButton.setEnabled(false);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (localTime == null) {
                    localTime = new LocalTime(
                            (Integer) hours.getValue(),
                            (Integer) minutes.getValue(),
                            (Integer) seconds.getValue());
                    updateTimeLabel();
                    startTime = getSecondsCount();
                    hours.setEnabled(false);
                    minutes.setEnabled(false);
                    seconds.setEnabled(false);
                    stopButton.setEnabled(true);
                    bargraph.setValue(100);
                    bargraph.setValueAnimated(0);
                }
                if (isPause) {
                    timer.stop();
                    startButton.setIcon(new ImageIcon(playURL));
                } else {
                    timer.start();
                    startButton.setIcon(new ImageIcon(pauseURL));
                }
                isPause = !isPause;
            }
        });
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopTimer();
            }
        });
        hours.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                clock.setHour((Integer) hours.getValue());
            }
        });
        minutes.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                clock.setMinute((Integer) minutes.getValue());
            }
        });
        seconds.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                clock.setSecond((Integer) seconds.getValue());
            }
        });
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void stopTimer() {
        timer.stop();
        startButton.setIcon(new ImageIcon(playURL));
        isPause = false;
        localTime = null;
        hours.setEnabled(true);
        minutes.setEnabled(true);
        seconds.setEnabled(true);
        hours.setValue(0);
        minutes.setValue(0);
        seconds.setValue(10);
        bargraph.setValue(0);
        stopButton.setEnabled(false);
        clock.setHour((Integer) hours.getValue());
        clock.setMinute((Integer) minutes.getValue());
        clock.setSecond((Integer) seconds.getValue());
    }

    private void updateTimeLabel() {
        int hourOfDay = localTime.getHourOfDay();
        clock.setHour(hourOfDay);
        int minuteOfHour = localTime.getMinuteOfHour();
        clock.setMinute(minuteOfHour);
        int secondOfMinute = localTime.getSecondOfMinute();
        clock.setSecond(secondOfMinute);

        hours.setValue(hourOfDay);
        minutes.setValue(minuteOfHour);
        seconds.setValue(secondOfMinute);
    }

    private void createUIComponents() {
        final SpinnerModel hourLimits = new SpinnerNumberModel(0, 0, 23, 1);
        hours = new JSpinner(hourLimits);
        final SpinnerModel minuteLimits = new SpinnerNumberModel(0, 0, 59, 1);
        minutes = new JSpinner(minuteLimits);
        final SpinnerModel secondsLimits = new SpinnerNumberModel(10, 0, 59, 10);
        seconds = new JSpinner(secondsLimits);
    }
}
