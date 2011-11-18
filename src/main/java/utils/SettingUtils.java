package utils;

import java.io.File;
import java.util.Collection;

public class SettingUtils {
    private Integer step = 1000;
    private String playURL;
    private String pauseURL;
    private String soundURL;
    private String finalMessage = "The end";
    private String addingMessage = "Wake up";
    private String aimpPath;
    private String aimpOption;
    private String stopURL;
    private String closeURL;

    public SettingUtils() {
        File file = new File(new File("."), "Settings.ini");
        Collection<String> strings = FileUtils.readFile(file.getPath());
        if (strings != null) {
            for (String s : strings) {
                if (s.contains("=")) {
                    String[] split = s.split("=", 2);
                    String key = split[0];
                    String value = split[1];
                    checkKeyValue(key, value);
                }
            }
        }
        System.out.println(this.toString());
    }

    private void checkKeyValue(String key, String value) {
        if ("PLAY_PATH".equals(key)) {
            playURL = value;
        } else if ("STOP_PATH".equals(key)) {
            stopURL = value;
        } else if ("PAUSE_PATH".equals(key)) {
            pauseURL = value;
        } else if ("BEEP_PATH".equals(key)) {
            soundURL = value;
        } else if ("CLOSE_PATH".equals(key)) {
            closeURL = value;
        } else if ("AIMP_PATH".equals(key)) {
            aimpPath = value;
        } else if ("AIMP_OPTION".equals(key)) {
            aimpOption = value;
        } else if ("STEP".equals(key) && value != null) {
            step = Integer.valueOf(value);
        } else if ("FINAL_MESSAGE".equals(key)) {
            finalMessage = value;
        } else if ("ADD_MESSAGE".equals(key)) {
            addingMessage = value;
        }
    }

    public Integer getStep() {
        return step;
    }

    public String getStopURL() {
        return stopURL;
    }

    public String getCloseURL() {
        return closeURL;
    }

    public String getAddingMessage() {
        return addingMessage;
    }

    public String getPlayURL() {
        return playURL;
    }

    public String getPauseURL() {
        return pauseURL;
    }

    public String getSoundURL() {
        return soundURL;
    }

    public String getFinalMessage() {
        return finalMessage;
    }

    public String getAimpPath() {
        return aimpPath;
    }

    public String getAimpOption() {
        return aimpOption;
    }

    @Override
    public String toString() {
        return "SettingUtils{" +
                "step=" + step +
                ", playURL='" + playURL + '\'' +
                ", pauseURL='" + pauseURL + '\'' +
                ", soundURL='" + soundURL + '\'' +
                ", finalMessage='" + finalMessage + '\'' +
                ", addingMessage='" + addingMessage + '\'' +
                ", aimpPath='" + aimpPath + '\'' +
                ", aimpOption='" + aimpOption + '\'' +
                ", stopURL='" + stopURL + '\'' +
                ", closeURL='" + closeURL + '\'' +
                '}';
    }
}
