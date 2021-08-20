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

import sa52.team03.adproject.models.ClassModule;

public class CheckAttendanceListViewAdapter extends ArrayAdapter {

    private final Context context;
    private TextView alertTextView;
    private List<ClassModule> classModules = new ArrayList<>();

    public CheckAttendanceListViewAdapter(Context context, List<ClassModule> classModules, TextView alertTextView){
        super(context, R.layout.check_attendance_list_view);

        this.context = context;
        this.classModules = classModules;
        this.alertTextView = alertTextView;

        for (int i=0; i<=classModules.size(); i++){
            add(null);
        }
    }

    @Override
    public int getCount() {
        return classModules.size()+1;
    }

    @Override
    public Object getItem(int position) {
        if (position==0)
            return null;
        return classModules.get(position-1);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent){

        if (view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.check_attendance_list_view, parent,false);
        }

        TextView classModuleTextView = view.findViewById(R.id.classModuleName);
        TextView attendanceRateTextView = view.findViewById(R.id.attendanceRate);

        if (classModules.size()==0){
            return view;
        }

        if(position==0){
            classModuleTextView.setText("Class");
            classModuleTextView.setTypeface(null,Typeface.BOLD);
            attendanceRateTextView.setText("Attendance Rate");
            attendanceRateTextView.setTypeface(null,Typeface.BOLD);
        }
        else{

            String moduleName = classModules.get(position-1).getModuleCode();
            Integer attendance = classModules.get(position-1).getStudentAttendance();
            Integer minAttendance = classModules.get(position-1).getMinAttendance();

            classModuleTextView.setTypeface(null,Typeface.NORMAL);
            classModuleTextView.setText(moduleName);
            attendanceRateTextView.setTypeface(null,Typeface.NORMAL);

            if (attendance<minAttendance){
                attendanceRateTextView.setTextColor(Color.RED);
                classModuleTextView.setTextColor(Color.RED);
                attendanceRateTextView.setText(attendance+"%");
                alertTextView.setText("Alert: Class(es) didn't meet the attendance requirement");
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

    public void setData(List<ClassModule> classModules){
        this.classModules.clear();
        this.classModules.addAll(classModules);
        notifyDataSetChanged();
    }

}
