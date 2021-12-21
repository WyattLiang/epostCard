package com.first.wyatt.postcard;


import java.util.ArrayList;

public class PostcardList {
    public static ArrayList<postCard>postCards;

    public PostcardList(){

    }
    public static Boolean isEmpty(){
        return postCards.isEmpty();
    }

    public static void setList(){
        postCards = new ArrayList<>();
    }
}