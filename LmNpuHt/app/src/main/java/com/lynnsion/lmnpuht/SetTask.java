package com.lynnsion.lmnpuht;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wizrobo_npu.ImgPath;
import wizrobo_npu.ImgStation;
import wizrobo_npu.NpuException;
import wizrobo_npu.Task;
import wizrobo_npu.TaskInfo;
import wizrobo_npu.actionname;


public class SetTask extends AppCompatActivity {


    public static List<TaskAction> task_action_list = new ArrayList<TaskAction>();
    TaskActionTableAdapter taskActionAdapter;
    ListView taskActionListView;
    Button  bt_add_task,bt_delete_task,bt_add_action,bt_delete_action,bt_save_task;
    Spinner sp_task_list;
    CheckBox cb_enb_loop;
    EditText et_loop_times;
    List<String> listTaskId;

    List<String> pathListId,stationListId;
    private ArrayAdapter<String> adapterTaskList;

    int taskActionId=0,taskId=0;
    public static final String DEFAULT_QUERY_REGEX = "[@!$^&*+=|{}';'\",<>/?~！#￥%……&*——|{}【】‘；：”“'。，、？]";
    private boolean addNewTask=false;
    TaskInfo taskInfo;
    Task[] taskList;
    wizrobo_npu.TaskAction[] actionList;


    public static ArrayList StationList =new ArrayList() ;
    public static ArrayList ActionList =new ArrayList();
    public static ArrayList PathList= new ArrayList();
    public static ArrayList OthersList =new ArrayList();
    public static String[] others= {"0.2","1","2","30","90"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_task);

        bt_add_task=(Button)findViewById(R.id.bt_add_task);
        bt_add_task.setOnClickListener(Bt_Add_Task_Onclick);
        bt_delete_task=(Button)findViewById(R.id.bt_delete_task);
        bt_delete_task.setOnClickListener(Bt_Delete_Task_Onclick);
        bt_save_task=(Button)findViewById(R.id.bt_save_task);
        bt_save_task.setOnClickListener(Bt_Save_Task_Onclick);

        bt_add_action=(Button)findViewById(R.id.bt_add_action);
        bt_add_action.setOnClickListener(Bt_Add_Action_Onclick);
        bt_delete_action=(Button)findViewById(R.id.bt_delete_action);
        bt_delete_action.setOnClickListener(Bt_Delete_Action_Onclick);

        cb_enb_loop=(CheckBox)findViewById(R.id.cb_enb_loop);
        et_loop_times=(EditText)findViewById(R.id.et_loop_times) ;

        taskActionListView = (ListView) findViewById(R.id.task_action_list);
//        task_action_list.add(new TaskAction(taskActionId, "follow","Path3",11));
//        task_action_list.add(new TaskAction(taskActionId, "navi","Sta2",12));
//        task_action_list.add(new TaskAction(taskActionId, "forward","1m",10));
        setListViewHeightBasedOnChildren(taskActionListView);

        stationListId = new ArrayList<String>();
        pathListId = new ArrayList<String>();


        try
        {
            ImgStation[] newImgStationList = WizRoboNpu.mynpu.GetImgStations(WizRoboNpu.mapname);
            for (int i = 0; i < newImgStationList.length; i++) {
                stationListId.add(newImgStationList[i].info.id);
            }

            ImgPath[]  newImgPathList = WizRoboNpu.mynpu.GetImgPaths(WizRoboNpu.mapname);
            for (int i = 0; i < newImgPathList.length; i++) {
                pathListId.add(newImgPathList[i].info.id);
            }

            //pathListId = stationListId;
        }

        catch (NpuException e)
        {
            NpuExceptionAlert(e);
        }


        ActionList.clear();
        for(int i = 0; i< actionname.values().length; i++) {
            ActionList.add(i, actionname.valueOf(i).toString());
        }

        StationList.clear();
        for(int i=0;i<stationListId.size();i++)
        {
            StationList.add(i,stationListId.get(i).toString());
        }

        PathList.clear();
        for(int i=0;i<pathListId.size();i++)
        {
            PathList.add(i,pathListId.get(i).toString());
        }

        OthersList.clear();
        for(int i=0;i<others.length;i++) {
            OthersList.add(i,others[i].toString());
        }
        taskActionAdapter = new TaskActionTableAdapter(getApplicationContext(), task_action_list);
        taskActionListView.setAdapter(taskActionAdapter);


        sp_task_list = (Spinner) findViewById(R.id.sp_task_list);
        listTaskId = new ArrayList<String>();
        adapterTaskList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listTaskId);
        sp_task_list.setAdapter(adapterTaskList);
        listTaskId.add("任务列表");
        adapterTaskList.notifyDataSetChanged();

        GetTaskList();
        if(taskList.length>=1)
        {
            GetTaskActionList(taskList[0].info.action_list);
            cb_enb_loop.setChecked(taskList[taskId].enb_taskloop);
            et_loop_times.setText(Integer.toString(taskList[taskId].task_loop_times));
        }


        sp_task_list.setOnTouchListener(new Spinner.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: //Click select map
                        try {
                            GetTaskList();
                            if (taskList == null || taskList.length == 0)
                                return false;
                            GetTaskActionList(taskList[0].info.action_list);
                            cb_enb_loop.setChecked(taskList[0].enb_taskloop);
                            et_loop_times.setText(Integer.toString(taskList[0].task_loop_times));
                        }
                        catch (Exception e)
                        {
                            NormalException(e);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        sp_task_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (WizRoboNpu.isInited) {
                    if (taskList == null||taskList.length==0)
                        return;
                    Spinner spinner = (Spinner) parent;
                    adapterTaskList.notifyDataSetChanged();
                    taskId=spinner.getSelectedItemPosition();
                    GetTaskActionList(taskList[taskId].info.action_list);
                    cb_enb_loop.setChecked(taskList[taskId].enb_taskloop);
                    et_loop_times.setText(Integer.toString(taskList[taskId].task_loop_times));
                    System.out.println("taskid:" + spinner.getSelectedItem().toString());
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)        //按home退出
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            StationList.clear();
            PathList.clear();
            task_action_list.clear();
            this.finish();

        }
        return false;
    }

    @Override
    protected void onDestroy() {
        StationList.clear();
        PathList.clear();
        task_action_list.clear();
        super.onDestroy();
        this.finish();
        System.out.println(" AdapterView22388: 2");

    }


    private View.OnClickListener Bt_Add_Task_Onclick = new View.OnClickListener() {
        public void onClick(View v) {

            if(WizRoboNpu.isInited)
            {
                task_action_list.clear();
                taskActionId=0;
                taskActionAdapter = new TaskActionTableAdapter(getApplicationContext(), task_action_list);
                taskActionListView.setAdapter(taskActionAdapter);
                addNewTask=true;
                Toast toast = Toast.makeText(getApplicationContext(), "请在下方添加动作名称，完成后点击Save按钮！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    };




    private View.OnClickListener Bt_Delete_Task_Onclick = new View.OnClickListener() {
        public void onClick(View v) {

            if(WizRoboNpu.isInited)
            DeleteTask();

        }
    };


    private View.OnClickListener Bt_Save_Task_Onclick = new View.OnClickListener() {
        public void onClick(View v) {

           if(WizRoboNpu.isInited) {
               if (addNewTask) {
                   AddNewTask();
               }

               else
               {
                   ModifyTask();
               }
           }
        }
    };


    private View.OnClickListener Bt_Add_Action_Onclick = new View.OnClickListener() {
        public void onClick(View v) {

            if(WizRoboNpu.isInited) {
                task_action_list.add(new TaskAction(taskActionId, actionname.forward.name(),OthersList.get(0).toString(), 3));
                taskActionAdapter = new TaskActionTableAdapter(getApplicationContext(), task_action_list);
                taskActionListView.setAdapter(taskActionAdapter);
                setListViewHeightBasedOnChildren(taskActionListView);
                taskActionId++;
            }
        }
    };


    private View.OnClickListener Bt_Delete_Action_Onclick = new View.OnClickListener() {
        public void onClick(View v) {

            if(WizRoboNpu.isInited) {
                if (taskActionId == 0)
                    return;
                task_action_list.remove(taskActionId - 1);
                taskActionAdapter = new TaskActionTableAdapter(getApplicationContext(), task_action_list);
                taskActionListView.setAdapter(taskActionAdapter);
                setListViewHeightBasedOnChildren(taskActionListView);
                taskActionId--;
            }
        }
    };

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    private void GetTaskList()
    {
        try {
            if (WizRoboNpu.isInited) {

                addNewTask = false;

                taskList = WizRoboNpu.mynpu.GetTaskList(WizRoboNpu.mapname);

                listTaskId.clear();

                if (taskList == null || taskList.length == 0) {
                    listTaskId.add("任务列表:空");
                    adapterTaskList.notifyDataSetChanged();
                    return;
                }

                for (int i = 0; i < taskList.length; i++) {
                    listTaskId.add(taskList[i].info.task_id);
                }

                adapterTaskList.notifyDataSetChanged();

                //GetTaskActionList(taskList[0].info.action_list);

            }
        }
        catch (NpuException e)
        {
            NpuExceptionAlert(e);
        }

    }


    private void GetTaskActionList(wizrobo_npu.TaskAction[] actions)
    {
        task_action_list.clear();
        taskActionId=0;
        if(actions == null || actions.length <=0)
            return;

        for(int i=0;i<actions.length;i++)
        {
            task_action_list.add(new TaskAction(taskActionId, actions[i].action_name.name(),actions[i].action_args,actions[i].duration));
            taskActionId++;
        }
        taskActionAdapter = new TaskActionTableAdapter(getApplicationContext(), task_action_list);
        taskActionListView.setAdapter(taskActionAdapter);
        setListViewHeightBasedOnChildren(taskActionListView);
    }

    private void ModifyTask()
    {
        try {
            wizrobo_npu.TaskAction[] actions = new wizrobo_npu.TaskAction[task_action_list.size()];
            for (int i = 0; i < task_action_list.size(); i++) {
                wizrobo_npu.TaskAction action=new wizrobo_npu.TaskAction();
                action.action_name= actionname.valueOf(task_action_list.get(i).getActionName());
                action.action_args = task_action_list.get(i).getActionArgs();
                action.duration = task_action_list.get(i).getDuration();

                actions[i]=action;
            }

            taskList[taskId].info.action_list = actions;
            WizRoboNpu.mynpu.SetTaskList(WizRoboNpu.mapname, taskList);

            Toast toast = Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        catch (NpuException e)
        {
            NpuExceptionAlert(e);
        }
    }


    private void AddNewTask()
    {
        final EditText et_tasknamex = new EditText(SetTask.this);
        final AlertDialog dialog = new AlertDialog.Builder(SetTask.this)
                .setTitle("提示")
                // .setIcon(R.drawable.warming)
                .setMessage("请输入任务名称！")
                //.setMessage("不能包含@#￥%&*等特殊字符！")
                .setView(et_tasknamex)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", null)
                .setCancelable(true)
                .create();
        dialog.show();

        //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {

             try {
                 boolean a;
                 String name = et_tasknamex.getText().toString();
                 if (name == null || name.length() <= 0) {
                 } else {
                     a = SpecialSymbols(name);
                     if (a) {
                         Toast toast = Toast.makeText(getApplicationContext(), "不能包含*&%$#@!等特殊字符", Toast.LENGTH_SHORT);
                         toast.setGravity(Gravity.CENTER, 0, 0);
                         toast.show();
                         return;
                     }
                     dialog.dismiss();
                 }

                 System.out.println("IS SAVING PATH");
                 listTaskId.clear();
                 List<Task> tasks = new ArrayList<Task>(0);

                 if (taskList == null) {
                     System.out.println("Is Adding");
                 } else {
                     for (int i = 0; i < taskList.length; i++) {
                         tasks.add(taskList[i]);
                     }
                 }
                 TaskInfo newTaskInfo = new TaskInfo();
                 wizrobo_npu.TaskAction[] actions = new wizrobo_npu.TaskAction[task_action_list.size()];
                 Task task = new Task();
                 for (int i = 0; i < task_action_list.size(); i++) {
                     wizrobo_npu.TaskAction action=new wizrobo_npu.TaskAction();
                     action.action_name= actionname.valueOf(task_action_list.get(i).getActionName());
                     action.action_args = task_action_list.get(i).getActionArgs();
                     action.duration = task_action_list.get(i).getDuration();
                     actions[i]=action;
                 }

                 newTaskInfo.map_id = WizRoboNpu.mapname;
                 newTaskInfo.task_id = name;
                 newTaskInfo.action_list = actions;

                 task.enb_taskloop = cb_enb_loop.isChecked();
                 task.info = newTaskInfo;
                 task.task_loop_times = Integer.valueOf(et_loop_times.getText().toString());
                 tasks.add(task);

                 Task[] taskLists = new Task[tasks.size()];
                 for (int i = 0; i < tasks.size(); i++) {
                     taskLists[i] = tasks.get(i);
                 }
                 WizRoboNpu.mynpu.SetTaskList(WizRoboNpu.mapname, taskLists);
                 GetTaskList();
                 if(taskList==null || taskList.length==0)
                 {
                     Toast toast = Toast.makeText(getApplicationContext(), "列表为空！", Toast.LENGTH_LONG);
                     toast.setGravity(Gravity.CENTER, 0, 0);
                     toast.show();
                 }
                 else
                 {
                     GetTaskActionList(taskList[0].info.action_list);
                     Toast toast = Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_LONG);
                     toast.setGravity(Gravity.CENTER, 0, 0);
                     toast.show();
                 }

                 addNewTask=false;
             }

             catch (NpuException e)
             {
                 NpuExceptionAlert(e);
             }


         }
     }
        );

        //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

                                                                             @Override
                                                                             public void onClick(View v) {
                                                                                 dialog.dismiss();

                                                                             }
                                                                         }
        );
    }

    protected String GetQueryRegex() {
        return DEFAULT_QUERY_REGEX;
    }

    public boolean SpecialSymbols(String value) {
        Pattern pattern = Pattern.compile(GetQueryRegex());
        Matcher matcher = pattern.matcher(value);
        char[] specialSymbols = GetQueryRegex().toCharArray();
        boolean isStartWithSpecialSymbol = false; // 是否以特殊字符开头
        for (int i = 0; i < specialSymbols.length; i++) {
            char c = specialSymbols[i];
            if (value.indexOf(c) == 0) {
                isStartWithSpecialSymbol = true;
                break;
            }
        }
        //return matcher.find() && isStartWithSpecialSymbol;   // Modify by Jeremy,if the string start with special symbol
        return matcher.find();    //  if the string include special symbol,return true;
    }


    private void DeleteTask() {
        if (WizRoboNpu.isInited) {
            if(taskList==null ||taskList.length==0)
                return;
            new AlertDialog.Builder(SetTask.this)
                    .setTitle(R.string.str_warming)
                    // .setIcon(R.drawable.warming)
                    .setMessage("是否删除该任务")
                    .setPositiveButton(R.string.str_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    try {
                                        listTaskId.clear();
                                        List<Task> tasks = new ArrayList<Task>(0);
                                        if (taskList == null || taskList.length == 0)
                                            return;
                                        for (int j = 0; j < taskList.length; j++) {
                                            tasks.add(taskList[j]);
                                        }
                                        tasks.remove(sp_task_list.getSelectedItemPosition());
                                        Task[] newTaskList = new Task[tasks.size()];
                                        for (int j = 0; j < tasks.size(); j++) {
                                            newTaskList[j] = tasks.get(j);
                                        }
                                        WizRoboNpu.mynpu.SetTaskList(WizRoboNpu.mapname, newTaskList);

                                        for (int j = 0; j < newTaskList.length; j++) {
                                            listTaskId.add(newTaskList[j].info.task_id);
                                        }
                                        taskList = newTaskList;
                                        GetTaskList();
                                        if(taskList.length>=1)
                                        GetTaskActionList(taskList[0].info.action_list);
                                        newTaskList = null;
                                        tasks = null;
                                        Toast toast = Toast.makeText(getApplicationContext(), "删除完成！", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                    catch (NpuException e)
                                    {
                                        NpuExceptionAlert(e);
                                    }
                                }
                            })
                    .setNegativeButton(R.string.str_no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                }
                            }).show();
        }

    }

    public void NpuExceptionAlert(NpuException e)
    {
        new AlertDialog.Builder(SetTask.this,AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle(R.string.str_warming)
                //.setIcon(R.drawable.warming)
                .setMessage("异常："+e.msg)
                .setPositiveButton(R.string.str_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {


                            }
                        })
                .setNegativeButton(R.string.str_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                            }
                        }).show();
    }



    public void NormalException(Exception e)
    {
        new AlertDialog.Builder(SetTask.this,AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle(R.string.str_warming)
                //.setIcon(R.drawable.warming)
                .setMessage("异常："+e.toString())
                .setPositiveButton(R.string.str_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {


                            }
                        })
                .setNegativeButton(R.string.str_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                            }
                        }).show();
    }
}
