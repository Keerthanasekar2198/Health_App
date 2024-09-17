package com.android.health.security;

import com.android.health.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class Security {


    private int n = 0;
    private ArrayList<Character> alphabet,rearranged;

    private Security(){}

    public Security(String number,int type){
        int key=0;
        alphabet=new ArrayList(
                Arrays.asList('M','0','-','e','q','Z','.','2','B','~','U','v','\\','N','g','8'
                        ,'b','*','A',';','u','r',')','O','f','E','a','}','t','D',' ','7','Y','s','+',
                        'C','m','1','T','!','l','P','@','X','L','&','S','>','K','F',
                        '^','6','z',',','W','/','k','j','%','o','H','d','#','R','5','x',']',
                        'p','I','"','n','`','y','(','J','h','3','w','Q','i','<','G','V','[',
                        'c','4','$','{','9',':'));
        for(int i=0;i<number.length();i++){
            key += Integer.parseInt(String.valueOf(number.charAt(i)));
        }

        if(type != 2){
            MainActivity.securityPreferences.edit().putInt("sec_key",key).apply();
        }else {
            n = key;
        }
    }

    public String encrypt(String string){
        String str = new String();
        rearranged=new ArrayList();
        int key = (n==0)?MainActivity.securityPreferences.getInt("sec_key",15):n;
        for(int i=0;i<90;i++){
            rearranged.add(alphabet.get(key-1));
            if(key==90){
                key=0;
            }
            key++;
        }

        for(int i=0;i<string.length();i++){
            if(alphabet.indexOf(string.charAt(i))!=-1){
                str += rearranged.get(alphabet.indexOf(string.charAt(i)));
            }else{
                str += string.charAt(i);
            }
        }
        return str;
    }

    public String decrypt(String string){
        String str = new String();
        rearranged=new ArrayList();
        int key = (n==0)? MainActivity.securityPreferences.getInt("sec_key",15) : n;
        for(int i=0;i<90;i++){
            rearranged.add(alphabet.get(key-1));
            if(key == 90){
                key = 0;
            }
            key++;
        }

        for(int i=0;i<string.length();i++){
            if(rearranged.indexOf(string.charAt(i))!=-1){
                str += alphabet.get(rearranged.indexOf(string.charAt(i)));
            }else{
                str += string.charAt(i);
            }
        }
        return str;
    }


}
