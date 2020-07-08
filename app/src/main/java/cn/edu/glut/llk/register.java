package cn.edu.glut.llk;

import java.util.ArrayList;



import java.util.Scanner;


 class User{

    long id;
    String code;
    public long getId() {
        return id;
    }
    public String getCode() {
        return code;
    }


    public User(long id, String code) {
        // TODO Auto-generated constructor stub
        this.id = id;
        this.code = code;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "{账号：" + this.id + "  密码：" + this.code+ "}";
    }

    @Override
    public boolean equals(Object obj) {//重写equals方法，匹配对象的id
        // TODO Auto-generated method stub
        User u = (User) obj;
        return this.id == u.id;
    }


    @Override
    public int hashCode() {//hashCode()返回类型是int，返回此集合的哈希码值
        // TODO Auto-generated method stub
        return (int) this.id;
    }
}
 class Demo26 {
    ArrayList<User> users=new ArrayList();
    public  void main(String[] args) {
        // TODO Auto-generated method stub
        long testid = 123456;
        String testcode = "123";
        String testrecode = "123";
        register(testid, testcode,testrecode);

        users.add(new User(123, "pyy"));
        users.add(new User(234, "jyj"));
        users.add(new User(345, "ydq"));
        users.add(new User(456, "xyz"));

    }
    //注册
    public  int register(long id,String code,String recode){



            for (Object user : users) {
             User  u=(User)user;

               if(u.getId()==id){
                   return  1;//1代表("您输入的账号已存在！请重新输入!");
               }

            }





                //账号不存在   就可以继续输入密码


                if(!code.equals(recode)){


                    return 2;//2代表("对不起，您输入的密码不一致！");
            }


                //账号 密码都有了  就可以将该用户添加到集合中了
                else {
                    users.add(new User(id, code));


                    return 3;//3代表("恭喜您注册成功！")

                    //使用toArray 打印集合元素
                }

            }


    }
