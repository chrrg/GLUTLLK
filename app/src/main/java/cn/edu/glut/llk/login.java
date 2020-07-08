package cn.edu.glut.llk;

import java.util.ArrayList;


class login {
    ArrayList<User> users = new ArrayList();

    public void main(String[] args) {
        // TODO Auto-generated method stub
        long testid = 123456;
        String testcode = "123";



    }
        public int login ( long id, String code){

            for (Object user : users) {
                User  u=(User)user;

                if(u.getId()!=id){
                    return  4;//4代表("您输入的账号不存在！请先注册!");
                }
                if(!(u.getCode()).equals (code)){


                    return 5;//5代表("对不起，您输入的密码错误！");
                }
            }


                return 6;//6代表("恭喜您登录成功！")

        }
    }



