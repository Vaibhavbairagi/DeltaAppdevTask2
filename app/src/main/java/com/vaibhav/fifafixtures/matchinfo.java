package com.vaibhav.fifafixtures;
public class matchinfo {
    String groupname;
    String team1;
    String team2;
    String cdate;
    String ctime;
    String cvenue;
    int place;
    byte[] logo1;
    byte[] logo2;

    public matchinfo(byte[] bmp1,byte[] bmp2,String grp,String t1,String t2,String tm,String d,String v,int p){
        logo1=bmp1;
        logo2=bmp2;
        groupname=grp;
        team1=t1;
        team2=t2;
        ctime=tm;
        cdate=d;
        cvenue=v;
        place=p;
    }

}
