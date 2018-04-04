package com.lynnsion.lmnpuht;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yangqiangyu on 15/10/26.
 */
public class MultiSpinner extends TextView implements View.OnClickListener,DialogInterface.OnClickListener{

    private ListView listView;

    private Context context;

    private String title;

    private List<SimpleSpinnerOption> dataList;

    private Adapter adapter;

    private Set<Object> checkedSet;

    private int selectCount=-1;

    private boolean isEmpty(){
        return dataList==null?true:dataList.isEmpty();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCheckedSet(Set<Object> checkedSet) {
        this.checkedSet = checkedSet;
        showSelectedContent();
    }

    public List<SimpleSpinnerOption> getDataList() {
        return dataList;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public void setDataList(List<SimpleSpinnerOption> dataList) {
        this.dataList = dataList;
        if (adapter==null){
            adapter=new Adapter(dataList);
            this.listView.setAdapter(adapter);
        }else {
            adapter.setList(dataList);
            adapter.notifyDataSetChanged();
        }
    }

    public MultiSpinner(Context context) {
        super(context, null);
    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.setOnClickListener(this);
        listView=new ListView(context);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        adapter = new Adapter(null);
        this.listView.setAdapter(adapter);
    }

    public MultiSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View view) {
        ViewGroup parent=(ViewGroup)listView.getParent();
        if(parent!=null){
            parent.removeView(listView);
        }
        if (dataList==null){
            Log.d("MultiSpinner","no data to show");
        }
        adapter.setCheckedSet(checkedSet);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("确定",this)
                .setNegativeButton("取消",this)
                .setView(listView).show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i){
            case -1:
                this.checkedSet=adapter.getCheckedSet();
                showSelectedContent();
                break;
        }
    }

    private void showSelectedContent(){
        StringBuilder sb=new StringBuilder();
        for(SimpleSpinnerOption option:getCheckedOptions()){
            sb.append(option.getName()).append(",");
        }
        if(sb.length()>0){
            sb.setLength(sb.length()-1);
        }
        setText(sb.toString());
    }

    public List<SimpleSpinnerOption> getCheckedOptions(){
        List<SimpleSpinnerOption> list=new ArrayList<SimpleSpinnerOption>();
        if(isEmpty()||checkedSet==null||checkedSet.isEmpty()){
            return  list;
        }
        for(SimpleSpinnerOption option:dataList){
            if(checkedSet.contains(option.getValue())){
                list.add(option);
            }
        }
        return list;
    }

    class Adapter extends BaseAdapter implements OnClickListener {

        private List<SimpleSpinnerOption> list;

        private Set<Object> checkedSet;

        public Adapter(List<SimpleSpinnerOption> list){
            this.list=list;
            checkedSet=new HashSet<Object>();
        }

        public void setList(List<SimpleSpinnerOption> list) {
            this.list = list;
        }

        public Set<Object> getCheckedSet(){
            return this.checkedSet;
        }

        public void setCheckedSet(Set<Object> checkedSet) {
            this.checkedSet=new HashSet<Object>();
            if(checkedSet!=null){
                this.checkedSet.addAll(checkedSet);
            }
        }

        @Override
        public int getCount() {
            return list==null?0:list.size();
        }

        @Override
        public Object getItem(int position) {
            return list==null?null:list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SimpleSpinnerOption mul=(SimpleSpinnerOption)this.getItem(position);
            Wrapper wrapper=null;
            if(convertView==null){
                convertView = LayoutInflater.from(MultiSpinner.this.getContext()).inflate(R.layout.multi_spinner_item,null);
                wrapper=new Wrapper();
                wrapper.textView=(TextView)convertView.findViewById(R.id.textView);
                wrapper.checkBox=(CheckBox)convertView.findViewById(R.id.checkBox);
                wrapper.checkBox.setOnClickListener(this);
                convertView.setTag(wrapper);
            }
            wrapper=(Wrapper)convertView.getTag();
            wrapper.textView.setText(mul.getName());

            if(checkedSet!=null){
                if(checkedSet.contains(mul.getValue())){
                    wrapper.checkBox.setChecked(true);
                }else{
                    wrapper.checkBox.setChecked(false);
                }
            }
            wrapper.checkBox.setTag(position);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            CheckBox checkBox=(CheckBox)v;
            Integer position=(Integer)checkBox.getTag();
            if(position==null){
                return;
            }
            SimpleSpinnerOption op=(SimpleSpinnerOption)getItem(position);
            if(checkBox.isChecked()){
                int maxCount= MultiSpinner.this.getSelectCount();
                if(maxCount>-1&&checkedSet.size()>=maxCount){
                    checkBox.setChecked(false);
                    Toast.makeText(MultiSpinner.this.getContext(), String.format("最多只能选择 %s 个", selectCount), Toast.LENGTH_SHORT).show();
                    return;
                }
                checkedSet.add(op.getValue());
               // System.out.println("selected"+position);

            }else{
                checkedSet.remove(op.getValue());
            }
        }

        class Wrapper{
            public TextView textView;
            public CheckBox checkBox;
        }
    }
}
