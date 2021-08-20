package sa52.team03.adproject.models;

import java.time.LocalDate;
import java.util.List;

public class ClassModule {

    private String moduleCode;

    private String moduleName;

    private String lecturerName;

    private Integer minAttendance;

    private Integer studentAttendance;

    private List<String> schedules;

    public ClassModule(){
        super();
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public Integer getMinAttendance() {
        return minAttendance;
    }

    public void setMinAttendance(Integer minAttendance) {
        this.minAttendance = minAttendance;
    }

    public List<String> getSchedules() {
        return schedules;
    }

    public Integer getStudentAttendance() {
        return studentAttendance;
    }

    public void setStudentAttendance(Integer studentAttendance) {
        this.studentAttendance = studentAttendance;
    }

    public void setSchedules(List<String> schedules) {
        this.schedules = schedules;
    }
}
