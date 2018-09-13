package bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Repair_Bean extends BmobObject implements Serializable{

    String repairer_id;//报修者id

    String repairer_name;//报修者昵称

    String repair_machine;//报修设备

    String repair_status;//报修状态

    int repair_bianhao;//报修编号

    String repair_content;//报修详细描述

    String repair_urls;//报修图片

    String repair_adress;//报修地址

    String repair_type;//故障类型

    String evluate_status;//评价状态

    String evluate_content;//评价内容

    boolean handle;//故障是否已处理

    public String getEvluate_content() {
        return evluate_content;
    }

    public String getEvluate_status() {
        return evluate_status;
    }

    public void setEvluate_content(String evluate_content) {
        this.evluate_content = evluate_content;
    }

    public void setEvluate_status(String evluate_status) {
        this.evluate_status = evluate_status;
    }

    public String getRepair_type() {
        return repair_type;
    }

    public void setRepair_type(String repair_type) {
        this.repair_type = repair_type;
    }

    public String getRepair_adress() {
        return repair_adress;
    }

    public void setRepair_adress(String repair_adress) {
        this.repair_adress = repair_adress;
    }

    public int getRepair_bianhao() {
        return repair_bianhao;
    }

    public String getRepair_status() {
        return repair_status;
    }

    public String getRepair_content() {
        return repair_content;
    }

    public String getRepair_machine() {
        return repair_machine;
    }

    public String getRepair_urls() {
        return repair_urls;
    }

    public String getRepairer_id() {
        return repairer_id;
    }

    public String getRepairer_name() {
        return repairer_name;
    }

    public void setRepair_bianhao(int repair_bianhao) {
        this.repair_bianhao = repair_bianhao;
    }

    public void setRepair_content(String repair_content) {
        this.repair_content = repair_content;
    }

    public void setRepair_machine(String repair_machine) {
        this.repair_machine = repair_machine;
    }

    public void setRepair_status(String repair_status) {
        this.repair_status = repair_status;
    }

    public void setRepair_urls(String repair_urls) {
        this.repair_urls = repair_urls;
    }

    public void setRepairer_id(String repairer_id) {
        this.repairer_id = repairer_id;
    }

    public void setRepairer_name(String repairer_name) {
        this.repairer_name = repairer_name;
    }

    public void setHandle(boolean handle) {
        this.handle = handle;
    }

    public boolean getHandle() {
        return handle;
    }
}
