package com.example.horsey;

import java.io.Serializable;

public class GameType implements Serializable {

    private String gameName;
    private String gameOption;
    private String chooseOne;
    private String shouldChoose;
    private String needToChoose;

    public GameType(String gameName,String gameOption,String chooseOne,String shouldChoose,String needToChoose){
        this.gameName=gameName;
        this.gameOption=gameOption;
        this.chooseOne=chooseOne;
        this.shouldChoose=shouldChoose;
        this.needToChoose=needToChoose;
    }

    public String getGameName(){
        return gameName;
    }

    public String getGameOption(){
        return gameOption;
    }

    public String getChooseOne(){
        return chooseOne;
    }

    public String getShouldChoose(){
        return shouldChoose;
    }

    public String getNeedToChoose() {
        return needToChoose;
    }
}
