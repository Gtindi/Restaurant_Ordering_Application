package crabfood;

import java.util.regex.Pattern;

public class SimulatedTime {

    private volatile int hour = 0;
    private volatile int minute = 0;

    public SimulatedTime() {
        this.hour = 0;
        this.minute = 0;
    }

    public SimulatedTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public void resetTime() {
        this.hour = 0;
        this.minute = 0;
    }

    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void tick() {
        if (minute == 59) {
            if (hour == 23) {
                hour = 0;
            } else {
                hour++;
            }
            minute = 0;
        } else {
            minute++;
        }
    }

    public void tickFor(int minutesToTick) {
        for (int i = 0; i < minutesToTick; i++) {
            this.tick();
        }
    }

    public boolean equalsTime(int hour, int minute) {
        return this.hour == hour && this.minute == minute;
    }

    public static SimulatedTime parseTimeToSimulatedTime(String timeStr) {
        SimulatedTime sim = new SimulatedTime();
        if (Pattern.matches("(//s)*([0-9])+(//s)*:(//s)*([0-9])+(//s)*", timeStr)) {
            sim.setHour(Integer.parseInt(timeStr.split(":")[0]));
            sim.setMinute(Integer.parseInt(timeStr.split(":")[1]));
        } else {
            sim.setHour(Integer.parseInt(timeStr));
        }
        return sim;
    }

    public static String parseTimeToString(String timeStr) {
        if (Pattern.matches("(//s)*([0-9])+(//s)*:(//s)*([0-9])+(//s)*", timeStr)) {
            return String.format("%02d:%02d",
                    Integer.parseInt(timeStr.split(":")[0]),
                    Integer.parseInt(timeStr.split(":")[1]));
        } else {
            return String.format("%02d:00", Integer.parseInt(timeStr));
        }
    }

    public String getTime() {
        return this.toString();
    }

    public void addTime(int hourToAdd, int minuteToAdd) {
        // must setHour first as setMinute will affect the result of setHour
        this.setHour((this.getHour() + ((this.getMinute() + minuteToAdd) / 60) + hourToAdd) % 24);
        this.setMinute((this.getMinute() + minuteToAdd) % 60);
    }

    public void addTime(int minuteToAdd) {
        // must setHour first as setMinute will affect the result of setHour
        this.setHour((this.getHour() + (this.getMinute() + minuteToAdd) / 60) % 24);
        this.setMinute((this.getMinute() + minuteToAdd) % 60);
    }

    public static int differenceTime(String time1str, String time2str) {
        SimulatedTime time1 = parseTimeToSimulatedTime(time1str);
        SimulatedTime time2 = parseTimeToSimulatedTime(time2str);
        return Math.abs(time1.getHour() * 60 + time1.getMinute() - (time2.getHour() * 60 + time2.getMinute()));
    }

    public String getTimeAfter(int minutePassed) {
        SimulatedTime tempClock = new SimulatedTime(this.getHour(), this.getMinute());
        tempClock.addTime(minutePassed);
        return tempClock.getTime();
    }

    public String getTimeAfter(int hourPassed, int minutePassed) {
        SimulatedTime tempClock = new SimulatedTime(this.getHour(), this.getMinute());
        tempClock.addTime(hourPassed, minutePassed);
        return tempClock.getTime();
    }

    /**
     * @param time1
     * @param time2
     * @return 1 if time1 is later than time2, -1 if time1 is earlier than
     * time2, 0 if time1 is equal to time2
     */
    public static int compareStringTime(String time1, String time2) {
        SimulatedTime temp1 = SimulatedTime.parseTimeToSimulatedTime(time1);
        SimulatedTime temp2 = SimulatedTime.parseTimeToSimulatedTime(time2);
        if (temp1.getHour() > temp2.getHour()) {
            return 1;
        } else if (temp1.getHour() == temp2.getHour()) {
            if (temp1.getMinute() > temp2.getMinute()) {
                return 1;
            } else if (temp1.getMinute() == temp2.getMinute()) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static String getTimeAfter(String time, int minutePassed) {
        SimulatedTime temp = SimulatedTime.parseTimeToSimulatedTime(time);
        return temp.getTimeAfter(minutePassed);
    }

    public static String addStringTime(String time1, String time2) {
        SimulatedTime temp1 = SimulatedTime.parseTimeToSimulatedTime(time1);
        SimulatedTime temp2 = SimulatedTime.parseTimeToSimulatedTime(time2);
        return temp1.getTimeAfter(temp2.getHour(), temp2.getMinute());
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }
}
