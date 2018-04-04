package com.lynnsion.lmnpuht;

public class TaskAction {
    private int action_id;
    private String action_name;
    private String action_args;
    private int duration;

    public TaskAction() {
        super();
    }

    public TaskAction(int id, String action_name, String action_args,
                      int duration) {
        super();  
        this.action_id = id;
        this.action_name = action_name;
        this.action_args = action_args;
        this.duration = duration;
    }
  
    public int getId() {
        return action_id;
    }
    public void setId(int id) {
        this.action_id = id;
    }  

    public String getActionName() {
        return action_name;
    }
    public void setActionName(String name) {
        this.action_name=name;
    }

    public String getActionArgs() {
        return action_args;
    }

    public void setActionArgs(String args) {
        this.action_args=args;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int time) {
        this.duration=time;
    }
  

              
}  