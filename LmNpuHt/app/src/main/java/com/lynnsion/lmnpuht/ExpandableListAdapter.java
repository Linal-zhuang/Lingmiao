package com.lynnsion.lmnpuht;

/**
 * Created by wizrobo on 7/11/17.
 */

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


public class ExpandableListAdapter extends BaseExpandableListAdapter
{
    private Context mContext;
    private List<String> expandableListTitle;
    private Map<String, List<String>> expandableListDetail;
    private String name;

    public ExpandableListAdapter(List<String> expandableListTitle, Map<String,List<String>> expandableListDetail, String name, Context mContext)
    {
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = expandableListTitle;
        this.mContext = mContext;
        this.name=name;
    }

    @Override
    public int getGroupCount()//分组数
    {
        return this.expandableListTitle.size();
    }
    @Override
    public int getChildrenCount(int groupPosition)//分组内的item数
    {
        return this.expandableListDetail.get(expandableListTitle.get(groupPosition)).size();
    }
    @Override
    public Object getGroup(int groupPosition)//获取分组数据
    {
        return this.expandableListTitle.get(groupPosition);
    }
    @Override
    public Object getChild(int groupPosition, int childPosition)//获取第几分组第几个item的数据
    {
        return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition)).get(childPosition);
    }
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }
    @Override
    public boolean hasStableIds()
    {
        return false;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent)
    {
        String data = this.expandableListTitle.get(groupPosition);
        if(convertView == null)
        {
            convertView = View.inflate(mContext,R.layout.list_group, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.listTitle);
        tv.setTextColor(Color.rgb(255,116,0));
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
        tv.setText(data);
        return convertView;
    }
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent)
    {
        if(name=="Function") {
//		String data = this.expandableListDetail.get(this.expandableListTitle.get(groupPosition)).get(childPosition);
            String data = (String) this.getChild(groupPosition, childPosition);

            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.list_item, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.expandedListItem);
            //tv.setTextColor(Color.rgb(90, 90, 90)); black
            tv.setTextColor(Color.BLUE);
            tv.setTextSize(10);


            if (groupPosition == 0 && childPosition == 0) {
                WizRoboNpu.track_navi_parent = parent;
                WizRoboNpu.track_navi_view = convertView;
                data = WizRoboNpu.strNaviTrack;
                if (WizRoboNpu.isNavi || WizRoboNpu.isTrack)
                    tv.setTextColor(Color.rgb(207, 42, 48));
                else
                    tv.setTextColor(Color.BLUE);
            }

            if (groupPosition == 0 && childPosition == 1) {
                WizRoboNpu.slam_parent = parent;
                WizRoboNpu.slam_view = convertView;
                data = WizRoboNpu.strSlam;
                if (WizRoboNpu.isSlam)
                    tv.setTextColor(Color.rgb(207, 42, 48));
                else
                    tv.setTextColor(Color.BLUE);
            }
            tv.setText(data);
            return convertView;
        }

        if(name=="VirtualWall")
        {
            String data = (String) this.getChild(groupPosition, childPosition);

            if(convertView == null)
            {
                convertView = View.inflate(mContext, R.layout.list_item, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.expandedListItem);
            //tv.setTextColor(Color.rgb(90, 90, 90)); black
            tv.setTextColor(Color.BLUE);
            tv.setTextSize(10);

            if(WizRoboNpu.isTrack||WizRoboNpu.isNavi) {
                if (groupPosition == 0 && childPosition == 0 ) {
                    data = WizRoboNpu.strSaveVirtualWall;
                    if (WizRoboNpu.isSettingVirtualWall)
                        tv.setTextColor(Color.rgb(207, 42, 48));
                    if (!WizRoboNpu.isSettingVirtualWall)
                        tv.setTextColor(Color.BLUE);
                }
            }
            tv.setText(data);
            return convertView;
        }

        if(name=="TaskManage")
        {
            String data = (String) this.getChild(groupPosition, childPosition);

            if(convertView == null)
            {
                convertView = View.inflate(mContext, R.layout.list_item, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.expandedListItem);
            //tv.setTextColor(Color.rgb(90, 90, 90)); black
            tv.setTextColor(Color.BLUE);
            tv.setTextSize(10);
            tv.setText(data);
            return convertView;
        }

        if(name=="Operate")
        {
            String data = (String) this.getChild(groupPosition, childPosition);

            if(convertView == null)
            {
                convertView = View.inflate(mContext, R.layout.list_item, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.expandedListItem);
            tv.setTextColor(Color.BLUE);
            tv.setTextSize(10);



            if(WizRoboNpu.isNavi) {
                if (groupPosition == 0 && childPosition == 2 )
                {
                    data = WizRoboNpu.strSavePath;
                    if (WizRoboNpu.isSettingPathpose)
                        tv.setTextColor(Color.rgb(207, 42, 48));

                    if (!WizRoboNpu.isSettingPathpose)
                        tv.setTextColor(Color.BLUE);
                }

                if (groupPosition == 0 && childPosition == 6 )
                {
                    data = WizRoboNpu.strAddCoverageArea;
                    if (WizRoboNpu.toCoveragePathPlanning)
                        tv.setTextColor(Color.rgb(207, 42, 48));

                    if (!WizRoboNpu.toCoveragePathPlanning)
                        tv.setTextColor(Color.BLUE);
                }

            }
            tv.setText(data);
            return convertView;
        }


        if(name=="Setting")
        {
            String data = (String) this.getChild(groupPosition, childPosition);
            if(convertView == null)
            {
                convertView = View.inflate(mContext, R.layout.list_item, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.expandedListItem);
            tv.setTextColor(Color.BLUE);
            tv.setTextSize(10);

            if(WizRoboNpu.isNavi) {
                if (groupPosition == 0 && childPosition == 0 ) {
                    data = WizRoboNpu.strSetInitialPose;
                    if (WizRoboNpu.isSettingInitialPose)
                        tv.setTextColor(Color.rgb(207, 42, 48));
                    if (!WizRoboNpu.isSettingInitialPose)
                        tv.setTextColor(Color.BLUE);
                }

                if (groupPosition == 0 && childPosition == 1) {
                    data = WizRoboNpu.strSetGoalPose;
                    if (WizRoboNpu.isSettingGoalPose)
                        tv.setTextColor(Color.rgb(207, 42, 48));

                    if (!WizRoboNpu.isSettingGoalPose)
                        tv.setTextColor(Color.BLUE);
                }

                if (groupPosition == 0 && childPosition == 2) {
                    data = WizRoboNpu.strSetInitPoseArea;
                    if (WizRoboNpu.isSettingInitPoseArea)
                        tv.setTextColor(Color.rgb(207, 42, 48));

                    if (!WizRoboNpu.isSettingInitPoseArea)
                        tv.setTextColor(Color.BLUE);
                }


                if (groupPosition == 0 && childPosition == 3) {
                    data = WizRoboNpu.strSetFreePath;
                    if (WizRoboNpu.isSettingFreePath)
                        tv.setTextColor(Color.rgb(207, 42, 48));

                    if (!WizRoboNpu.isSettingFreePath)
                        tv.setTextColor(Color.BLUE);
                }

                if (groupPosition == 0 && childPosition == 4) {
                    data = WizRoboNpu.strSetCoveragePath;
                    if (WizRoboNpu.isSettingGoalPose)
                        tv.setTextColor(Color.rgb(207, 42, 48));

                    if (!WizRoboNpu.isSettingGoalPose)
                        tv.setTextColor(Color.BLUE);
                }
            }

            tv.setText(data);
            return convertView;
        }

        return null;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
