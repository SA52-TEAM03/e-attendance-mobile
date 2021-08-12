package sa52.team03.adproject;

public class QRCodeData {

    private int scheduleId;
    private Long signInSignOutId;

    public QRCodeData(int scheduleId, Long signInSignOutId) {
        this.scheduleId = scheduleId;
        this.signInSignOutId = signInSignOutId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getSignInSignOutId() {
        return signInSignOutId;
    }

    public void setSignInSignOutId(Long signInSignOutId) {
        this.signInSignOutId = signInSignOutId;
    }
}
