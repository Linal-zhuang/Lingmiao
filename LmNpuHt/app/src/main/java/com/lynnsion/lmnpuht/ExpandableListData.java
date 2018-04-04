package com.lynnsion.lmnpuht;

/**
 * Created by wizrobo on 7/11/17.
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExpandableListData
{

    public static Map<String, List<String>> getData(String name)
    {
        Map<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        if(name=="Function")
        {
            List<String> function = new ArrayList<String>();
            function.add("开始导航");
            function.add("开始建图");
            expandableListDetail.put("功能选择", function);
            return expandableListDetail;
        }
        if(name=="Operate")
        {
            if(WizRoboNpu.isTrack) {

                List<String> name1 = new ArrayList<String>();
                name1.add("多次循环执行");
                name1.add("单次循环执行");
                name1.add("单条路径执行");

                List<String> name0 = new ArrayList<String>();
                name0.add("自动添加路径");
                name0.add("设为路径点");
                name0.add("保存站点路径");

                List<String> name3 = new ArrayList<String>();
                name3.add("修改XY值");
                name3.add("修改角度");
                name3.add("删除路径");

                expandableListDetail.put("添加路径", name0);
                expandableListDetail.put("执行路径", name1);
                expandableListDetail.put("编辑路径", name3);
                expandableListDetail=sortMapByKey(expandableListDetail);//可解决在5.0以下版本显示不正常的问题
            }


            if(WizRoboNpu.isNavi)
            {
                List<String> setting = new ArrayList<String>();
                setting.add("添加站点");
                setting.add("删除站点");
                setting.add("添加路径");
                setting.add("删除路径");
                setting.add("设为路径点");
                setting.add("保存站点路径");
                setting.add("添加清扫区域");

                List<String> excute = new ArrayList<String>();
                excute.add("到达站点");
                excute.add("执行路径");
                expandableListDetail.put("路径站点处理", setting);
                expandableListDetail.put("路径站点执行", excute);

                expandableListDetail=sortMapByKey(expandableListDetail);//可解决在5.0以下版本显示不正常的问题

            }

            return expandableListDetail;
        }

        if(name=="Setting")
        {
            List<String> setting = new ArrayList<String>();
            if(WizRoboNpu.isNavi) {
                setting.add("设置初始位");
                setting.add("设置目标点");
                setting.add("设置初始区域");
                setting.add("设置自由路径");
                setting.add("设置清扫区域");
            }

            if(WizRoboNpu.isTrack)
            {
                setting.add("设置初始位");
                setting.add("设置自由路径");
                setting.add("设置清扫区域");
            }
            expandableListDetail.put("设置", setting);
            return expandableListDetail;
        }

        if(name=="VirtualWall")
        {
            List<String> virtualWall = new ArrayList<String>();
            virtualWall.add("添加虚拟墙");
            //virtualWall.add("修改虚拟墙");
            virtualWall.add("删除虚拟墙");



            expandableListDetail.put("虚拟墙管理", virtualWall);

            expandableListDetail=sortMapByKey(expandableListDetail);//可解决在5.0以下版本显示不正常的问题
            return expandableListDetail;
        }

        if(name == "TaskManage")
        {
            List<String> task = new ArrayList<String>();
            task.add("执行任务");
            task.add("编辑任务");
            expandableListDetail.put("任务管理", task);
            return expandableListDetail;
        }

        return null;

    }

    public static Map<String, List<String>> sortMapByKey(Map<String, List<String>> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, List<String>> sortedMap = new TreeMap<String, List<String>>(
                new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return lhs.compareToIgnoreCase(rhs);
                    }
                });
        sortedMap.putAll(oriMap);
        return sortedMap;
    }

}
