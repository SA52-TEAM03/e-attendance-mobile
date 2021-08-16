package sa52.team03.adproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckAttendanceListViewAdapter extends ArrayAdapter {

    private final Context context;
    private TextView alertTextView;
    private Map<String, List<Integer>> studentAttendance = new HashMap<>();
    private List<String> moduleNameList;

    public CheckAttendanceListViewAdapter(Context context, Map<String, List<Integer>> studentAttendance, TextView alertTextView){
        super(context, R.layout.check_attendance_list_view);

        this.context = context;
        this.studentAttendance = studentAttendance;
        this.alertTextView = alertTextView;
        moduleNameList = new ArrayList<>(studentAttendance.keySet());

        for (int i=0; i<=studentAttendance.size(); i++){
            add(null);
        }
    }

    @Override
    public int getCount() {
        return studentAttendance.size()+1;
    }

    @Override
    public Object getItem(int position) {
        if (position==0)
            return null;
        return moduleNameList.get(position-1);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent){
        if (view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.check_attendance_list_view, parent,false);
        }

        TextView classModuleTextView = view.findViewById(R.id.classModuleName);
        TextView attendanceRateTextView = view.findViewById(R.id.attendanceRate);

        if (studentAttendance.size()==0){
            return view;
        }

        if(position==0){
            classModuleTextView.setText("Class");
            classModuleTextView.setTypeface(null,Typeface.BOLD);
            attendanceRateTextView.setText("Attendance Rate");
            attendanceRateTextView.setTypeface(null,Typeface.BOLD);
        }else{
            String moduleName = moduleNameList.get(position-1);
            Integer attendance = studentAttendance.get(moduleName).get(1);
            Integer minAttendance = studentAttendance.get(moduleName).get(0);

            classModuleTextView.setTypeface(null,Typeface.NORMAL);
            classModuleTextView.setText(moduleName);
            attendanceRateTextView.setTypeface(null,Typeface.NORMAL);

            if (attendance<minAttendance){
                attendanceRateTextView.setTextColor(Color.RED);
                classModuleTextView.setTextColor(Color.RED);
                attendanceRateTextView.setText(attendance+"%");
                alertTextView.setText(moduleName + " didn't meet the attendance requirement");
            }
            else{
                attendanceRateTextView.setTextColor(Color.BLACK);
                classModuleTextView.setTextColor(Color.BLACK);
                attendanceRateTextView.setText(attendance+"%");
            }
        }
        return view;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return false;
    }

    public void setData(Map<String, List<Integer>> studentAttendance){
        this.studentAttendance.clear();
        this.studentAttendance.putAll(studentAttendance);
        moduleNameList.clear();
        moduleNameList.addAll(studentAttendance.keySet());
        notifyDataSetChanged();
    }

}
