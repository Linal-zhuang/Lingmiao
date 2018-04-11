package com.lynnsion.lmnpuht;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TaskActionTableAdapter extends BaseAdapter {
	private List<TaskAction> list= SetTask.task_action_list;
	private LayoutInflater inflater;
	private Integer index = 0;
	enum functionName{setId,setActionName,setActionArgs,setDuration};
	private functionName funcName= functionName.setId;
	ArrayList listActionName=new ArrayList() ;
	ArrayList listActionArgs=new ArrayList() ;
	private Context mContext;
	private Map<String, Integer> actionNameValues,actionArgsValues;
	private boolean action_name_touched=false,action_args_touched=false;
    ViewHolder newViewHolder;

	public TaskActionTableAdapter(Context applicationContext, List<TaskAction> list2) {
		// TODO Auto-generated constructor stub
		this.list = list2;
		this.mContext = applicationContext;
		for(int i=0;i<list2.size();i++)
		{
			listActionName.add(list2.get(i).getActionName());
			listActionArgs.add(list2.get(i).getActionArgs());
		}
		actionNameValues = new HashMap<String, Integer>();  //Must added!
		actionArgsValues = new HashMap<String, Integer>();  //Must added!

		//putAllValues();

		inflater = LayoutInflater.from(applicationContext);

	}

	private void putAllValues() {

		for(int i=0;i<listActionName.size();i++) {

			int j;
			String str;
			str=listActionName.get(i).toString();
			j=listActionName.indexOf(str);
			actionNameValues.put(str, j);

			str=listActionArgs.get(i).toString();
			j=listActionArgs.indexOf(str);
			actionArgsValues.put(str,j);

		}

	}

	public void setActionNameValues(Map<String, Integer> actionNameValues){
		this.actionNameValues = actionNameValues;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (list != null) {
			ret = list.size();
		}
		return ret;
	}

	@Override
	public Object getItem(int position) {
		return SetTask.task_action_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TaskAction taskAction = (TaskAction) this.getItem(position);
		final ViewHolder viewHolder;
		if (convertView == null) {

			viewHolder = new ViewHolder();

			convertView = inflater.inflate(R.layout.task_manage, null);
			viewHolder.tv_action_id = (TextView) convertView
					.findViewById(R.id.tv_action_id);
			viewHolder.sp_action_name = (Spinner) convertView
					.findViewById(R.id.sp_action_name);
			viewHolder.sp_action_args = (Spinner) convertView
					.findViewById(R.id.sp_action_args);
			viewHolder.et_duration = (EditText) convertView
					.findViewById(R.id.et_duration_s);

			SpinnerAdapter adapter = new SpinnerAdapter(mContext,"ActionList");

			viewHolder.sp_action_name.setAdapter(adapter);
			viewHolder.sp_action_name
					.setOnItemSelectedListener(new ActionNameItemClickSelectListener(
							viewHolder.sp_action_name));

			String checkedName = listActionName.get(position).toString();
			viewHolder.sp_action_name.setPrompt(checkedName);
			int spinnerOptionPosition =0;
			for(int i = 0; i<SetTask.ActionList.size(); i++)
			{
				if(SetTask.ActionList.get(i).equals(checkedName))
				{
					spinnerOptionPosition=i;
				}
			}
			viewHolder.sp_action_name.setSelection(spinnerOptionPosition);

			viewHolder.sp_action_args
					.setOnItemSelectedListener(new ActionArgsItemClickSelectListener(
							viewHolder.sp_action_args));

			checkedName = listActionArgs.get(position).toString();
			viewHolder.sp_action_args.setPrompt(checkedName);

			if(spinnerOptionPosition==0)
			{
				adapter=new SpinnerAdapter(mContext,"StationList");
				viewHolder.sp_action_args.setAdapter(adapter);
				for(int i=0;i<SetTask.StationList.size();i++)
				{
					if(SetTask.StationList.get(i).equals(checkedName))
					{
						spinnerOptionPosition=i;
					}
				}
			}
			else if(spinnerOptionPosition==1)
			{
				adapter=new SpinnerAdapter(mContext,"PathList");
				viewHolder.sp_action_args.setAdapter(adapter);
				for(int i=0;i<SetTask.PathList.size();i++)
				{
					if(SetTask.PathList.get(i).equals(checkedName))
					{
						spinnerOptionPosition=i;
					}
				}
			}

			else
			{
				adapter=new SpinnerAdapter(mContext,"OthersList");
				viewHolder.sp_action_args.setAdapter(adapter);
				for(int i = 0; i<SetTask.OthersList.size(); i++)
				{
					if(SetTask.OthersList.get(i).equals(checkedName))
					{
						spinnerOptionPosition=i;
					}
				}
			}
			viewHolder.sp_action_args.setSelection(spinnerOptionPosition);



			viewHolder.sp_action_name.setTag(position);
			viewHolder.sp_action_args.setTag(position);
			viewHolder.et_duration.setTag(position);



			viewHolder.sp_action_name.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						index = (Integer) v.getTag();
						funcName= functionName.setActionName;
						System.out.println("AdapterView1："+index);
						newViewHolder=viewHolder;
						action_name_touched=true;
					}
					return false;
				}
			});

			viewHolder.sp_action_args.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						index = (Integer) v.getTag();
						funcName= functionName.setActionArgs;
						System.out.println("AdapterView1："+index);
						action_args_touched=true;
					}
					return false;
				}
			});


			viewHolder.et_duration.setOnFocusChangeListener(new View.
					OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						index = (Integer) v.getTag();
						funcName= functionName.setDuration;
						// 此处为得到焦点时的处理内容
					} else {
						// 此处为失去焦点时的处理内容
					}
				}
			});



			class MyTextWatcher implements TextWatcher {
				public MyTextWatcher(ViewHolder holder) {
					mHolder = holder;
				}

				private ViewHolder mHolder;

				@Override
				public void onTextChanged(CharSequence s, int start,
										  int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
											  int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					if (s != null && !"".equals(s.toString())) {
						int position = index;
						int data=0;
						try {
							data = Integer.valueOf(s.toString());
						}

						catch(Exception e)
						{
							System.out.println(e.toString());
						}
						switch (funcName)
						{
							case setDuration:
								SetTask.task_action_list.get(position).setDuration(data);
								break;
//							case setMaxRange:
//								SetSensorParam.infrd_list.get(position).setMaxRange(data);
//								break;
//							case setFov:
//								SetSensorParam.infrd_list.get(position).setFov(data);
//								break;

							case setId:
								break;
						}

						System.out.println("position："+position);
					}
				}
			}
//			viewHolder.et_infrd_max_range.addTextChangedListener(new MyTextWatcher(viewHolder));
			viewHolder.et_duration.addTextChangedListener(new MyTextWatcher(viewHolder));


			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

//		//viewHolder.et_infrd_min_range.addTextChangedListener(mTextWatcher);
		viewHolder.tv_action_id.setText(Integer.toString(taskAction.getId()));
		viewHolder.tv_action_id.setTextSize(10);
		viewHolder.et_duration.setText(Integer.toString(taskAction.getDuration()));
		viewHolder.et_duration.setTextSize(10);
//		viewHolder.et_infrd_max_range.setText(Float.toString(infrds.getMaxRange()));
//		viewHolder.et_infrd_max_range.setTextSize(10);
//		viewHolder.et_infrd_fov.setText(Float.toString(infrds.getFov()));
//		viewHolder.et_infrd_fov.setTextSize(10);
//		viewHolder.et_infrd_install_x.setText(Float.toString(infrds.getInstallPose().x) + "");
//		viewHolder.et_infrd_install_x.setTextSize(10);
//		viewHolder.et_infrd_install_y.setText(Float.toString(infrds.getInstallPose().y) + "");
//		viewHolder.et_infrd_install_y.setTextSize(10);
//		viewHolder.et_infrd_install_z.setText(Float.toString(infrds.getInstallPose().z) + "");
//		viewHolder.et_infrd_install_z.setTextSize(10);
//		viewHolder.et_infrd_install_yaw.setText(Float.toString(infrds.getInstallPose().yaw) + "");
//		viewHolder.et_infrd_install_yaw.setTextSize(10);
		return convertView;
	}


	private class ActionNameItemClickSelectListener implements OnItemSelectedListener {
		Spinner sp_action_name ;

		public ActionNameItemClickSelectListener(Spinner checkinfo_item_value) {
			this.sp_action_name = checkinfo_item_value;
			System.out.println(" AdapterView333: " +sp_action_name.toString());
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
								   int ActionNameItemposition, long id) {
			//actionNameValues.put(sp_action_name.getPrompt().toString(), position);
			if(	action_name_touched)
			{

				System.out.println(" AdapterView666: " +ActionNameItemposition+"ID:"+id);

				if(ActionNameItemposition==0)
				{
					SpinnerAdapter adapter=new SpinnerAdapter(mContext,"StationList");
					if(SetTask.StationList.size()>=1)
					SetTask.task_action_list.get(index).setActionArgs(SetTask.StationList.get(0).toString());
					newViewHolder.sp_action_args.setAdapter(adapter);
				}

					else if(ActionNameItemposition==1)
				{
					SpinnerAdapter adapter=new SpinnerAdapter(mContext,"PathList");
					if(SetTask.PathList.size()>=1)
					SetTask.task_action_list.get(index).setActionArgs(SetTask.PathList.get(0).toString());
					newViewHolder.sp_action_args.setAdapter(adapter);
				}

				else
				{
					SpinnerAdapter adapter=new SpinnerAdapter(mContext,"OthersList");
					SetTask.task_action_list.get(index).setActionArgs(SetTask.OthersList.get(0).toString());
					newViewHolder.sp_action_args.setAdapter(adapter);
				}

				SetTask.task_action_list.get(index).setActionName(SetTask.ActionList.get(ActionNameItemposition).toString());
				action_name_touched=false;

			}


			System.out.println(" AdapterView223: " +ActionNameItemposition+"ID:"+id);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}



	private class ActionArgsItemClickSelectListener implements OnItemSelectedListener {
		Spinner sp_action_args ;

		public ActionArgsItemClickSelectListener(Spinner checkinfo_item_value) {
			this.sp_action_args = checkinfo_item_value;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
								   int position, long id) {

			//SetTask.task_action_list.get(index).setActionArgs(sp_action_args.get);
			//System.out.println(" AdapterView222: " +SetTask.StationList.get(position).toString());

			if(action_args_touched)
			{
				if(SetTask.task_action_list.get(index).getActionName().equals("navi")) {
					SetTask.task_action_list.get(index).setActionArgs(SetTask.StationList.get(position).toString());
					System.out.println(" AdapterView222: " +SetTask.StationList.get(position).toString());

				}

				else if(SetTask.task_action_list.get(index).getActionName().equals("follow")) {
					SetTask.task_action_list.get(index).setActionArgs(SetTask.PathList.get(position).toString());
				}

				else
				{
					SetTask.task_action_list.get(index).setActionArgs(SetTask.OthersList.get(position).toString());
				}

				action_args_touched=false;
			}



		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			System.out.println(" AdapterView225: " );

		}
	}

	public Map<String,Integer> getSelectValues() {
		return actionNameValues;
	}




	public static class ViewHolder {
		public TextView tv_action_id;
		public Spinner sp_action_name;
		public Spinner sp_action_args;
		public EditText et_duration;

	}
}

