package com.first.wyatt.postcard;

public class postCard {
    String postCard_Image;

    public postCard(){
    }
    public postCard(String url){
        this.postCard_Image = url;
    }

    public void setPostcard(String url){
        this.postCard_Image = url;
    }
    public String getPostcard(){
        return postCard_Image;
    }
}
