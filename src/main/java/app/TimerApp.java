package app;

import eu.hansolo.steelseries.extras.Clock;
import eu.hansolo.steelseries.gauges.LinearBargraph;
import org.joda.time.LocalTime;
import utils.SettingUtils;
import utils.ThemeUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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

    private SettingUtils settings;

    //entry point
    public static void main(String[] args) {
        ThemeUtils.initTheme();
        JFrame frame = new JFrame("Timer");
        frame.setContentPane(new TimerApp().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    //timer fired event
    public void actionPerformed(ActionEvent e) {
        if (localTime.getMillisOfDay() != 0) {
            localTime = localTime.minusMillis(settings.getStep());
            bargraph.setValue(100 - getSecondsCount() * 100 / startTime);
            updateTimeLabel();
        } else {
            playSound();
            JOptionPane.showMessageDialog(mainPanel, settings.getFinalMessage(), settings.getAddingMessage(), JOptionPane.INFORMATION_MESSAGE);
            stopTimer();
        }
    }

    public synchronized void playSound() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(settings.getSoundURL()));
                    final Clip clip = AudioSystem.getClip();
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
        String[] cmd = {settings.getAimpPath(), settings.getAimpOption()};
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
        settings = new SettingUtils();
        timer = new Timer(settings.getStep(), this);

        startButton.setIcon(new ImageIcon(settings.getPlayURL()));
        stopButton.setIcon(new ImageIcon(settings.getStopURL()));
        closeButton.setIcon(new ImageIcon(settings.getCloseURL()));

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
                    if (settings.getPlayURL() !=null)
                    startButton.setIcon(new ImageIcon(settings.getPlayURL()));
                } else {
                    timer.start();
                    startButton.setIcon(new ImageIcon(settings.getPauseURL()));
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
        startButton.setIcon(new ImageIcon(settings.getPlayURL()));
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
