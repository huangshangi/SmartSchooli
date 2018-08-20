package bean;

public class Group_ImageBean {
    String path;//该组中第一张图片的位置，根据第一张图片的位置可以得到该组的组名及该组所处的位置
    int count;
    String name;
    String group_path;

    public Group_ImageBean(){

    }

    public Group_ImageBean(String path, int count){
        this.path=path;
        this.count=count;
    }

    public int getCount(){
        return count;
    }

    public String getGroup_path() {
        return group_path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private  void setGroup_path(String group_path) {
        this.group_path = group_path;
    }

    private  void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        //待测试
        this.path = path;
        int index=path.lastIndexOf("/");
        setGroup_path(this.path.substring(0,index));
        int index1=getGroup_path().lastIndexOf("/");
        setName(getGroup_path().substring(index1+1));
    }
}

