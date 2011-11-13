import eu.hansolo.steelseries.extras.Clock;
import eu.hansolo.steelseries.gauges.LinearBargraph;
import org.joda.time.LocalTime;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

/**
 * User: chgv
 * Date: 9/21/11
 * Time: 9:33 AM
 */
public class TimerApp implements ActionListener {

    private static final String AIMP_PATH = "D:\\Program Files\\AIMP3\\AIMP3.exe";
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
    private static final URL playURL = ClassLoader.getSystemResource("images/play.png");
    private static final URL pauseURL = ClassLoader.getSystemResource("images/pause.png");
    private static final URL soundURL = ClassLoader.getSystemResource("beep.wav");
    private static final String FINAL_MESSAGE = "БДЖЫНЬ.";
    private static final String THEME_NAME = "Nimbus";


    //entry point
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (THEME_NAME.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.print(e.getMessage());
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
            playSound();
            JOptionPane.showMessageDialog(mainPanel, FINAL_MESSAGE, FINAL_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
            stopTimer();
        }
    }

    public static synchronized void playSound() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundURL);
                    final Clip clip = AudioSystem.getClip();
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
        String[] cmd = {AIMP_PATH, "/play"};
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private int getSecondsCount() {
        return localTime.getHourOfDay() * 3600 + localTime.getMinuteOfHour() * 60 + localTime.getSecondOfMinute();
    }

    //init app
    public TimerApp() {
        timer = new Timer(DEFAULT_STEP, this);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (localTime == null) {
                    localTime = new LocalTime(
                            (Integer) hours.getValue(),
                            (Integer) minutes.getValue(),
                            (Integer) seconds.getValue());
                    updateTimeLabel();
                    startTime = getSecondsCount();
                    enableComponents(false);
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
        enableComponents(true);
        bargraph.setValue(0);
        setTime(0, 0, 0);
    }

    private void enableComponents(final boolean isEnabled) {
        hours.setEnabled(isEnabled);
        minutes.setEnabled(isEnabled);
        seconds.setEnabled(isEnabled);
        stopButton.setEnabled(!isEnabled);
    }

    private void updateTimeLabel() {
        setTime(localTime.getHourOfDay(), localTime.getMinuteOfHour(), localTime.getSecondOfMinute());
    }

    private void setTime(int hour, int minute, int second) {
        hours.setValue(hour);
        minutes.setValue(minute);
        seconds.setValue(second);

        clock.setHour(hour);
        clock.setMinute(minute);
        clock.setSecond(second);
    }

    private void createUIComponents() {
        final SpinnerModel hourLimits = new SpinnerNumberModel(0, 0, 23, 1);
        hours = new JSpinner(hourLimits);
        final SpinnerModel minuteLimits = new SpinnerNumberModel(0, 0, 59, 1);
        minutes = new JSpinner(minuteLimits);
        final SpinnerModel secondsLimits = new SpinnerNumberModel(0, 0, 59, 5);
        seconds = new JSpinner(secondsLimits);
    }
}
