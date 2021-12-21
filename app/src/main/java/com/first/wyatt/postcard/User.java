package com.first.wyatt.postcard;


import java.io.Serializable;

public class User implements Serializable {
    private String profile_Url = null;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public User(){
    }
    public User(String profile_Url,String email,String userName,String firstName,String lastName,String phone){
        this.profile_Url = profile_Url;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.userName = userName;
    }
    public User(String email,String userName,String firstName,String lastName,String phone){
        profile_Url = "noProfile";
        this.email = email;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
    public User(String userName,String firstName,String lastName,String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.userName = userName;
    }


    public void setprofile_Url(String profile_Url){
        this.profile_Url = profile_Url;
    }
    public void setemail(String email){
        this.email = email;
    }
    public void setfirstName(String firstName){this.firstName = firstName;}
    public void setlastName(String lastName){this.lastName = lastName;}
    public void setphone(String phone){this.phone = phone;}
    public void setuserName(String userName){this.userName = userName;}

    public String getprofile_Url(){
        return profile_Url;
    }
    public String getuserName(){
        return userName;
    }
    public String getemail(){
        return email;
    }
    public String getfirstName(){
        return firstName;
    }
    public String getlastName(){
        return lastName;
    }
    public String getphone(){
        return phone;
    }
}
