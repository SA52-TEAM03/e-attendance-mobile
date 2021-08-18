package sa52.team03.adproject.models;

public class QRCodeData {

    private int studentId;
    private int scheduleId;
    private String signInSignOutId;

    public QRCodeData(int studentId, int scheduleId, String signInSignOutId) {
        this.studentId = studentId;
        this.scheduleId = scheduleId;
        this.signInSignOutId = signInSignOutId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSignInSignOutId() {
        return signInSignOutId;
    }

    public void setSignInSignOutId(String signInSignOutId) {
        this.signInSignOutId = signInSignOutId;
    }
}
