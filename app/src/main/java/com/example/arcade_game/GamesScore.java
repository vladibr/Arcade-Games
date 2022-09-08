package com.example.arcade_game;

public class GamesScore {

    private Integer sevenBoomGame;
    private Integer wordsGame;
    private Integer fingerMeGame;
    private Integer numberMemory;
    private Integer colorsGame;
    private Integer crackTheEgg;

    public GamesScore(Integer sevenBoomGame, Integer wordsGame,
                      Integer fingerMeGame, Integer colorsGame, Integer numberMemory ){

        setSevenBoomGame(sevenBoomGame);
        setWordsGame(wordsGame);
        setFingerMeGame(fingerMeGame);
        setColorsGame(colorsGame);
        setNumberMemory(numberMemory);
    }

    public Integer getNumberMemory() {
        return numberMemory;
    }

    public void setNumberMemory(Integer numberMemory) {
        this.numberMemory = numberMemory == null ? 0 : numberMemory;
    }

    public Integer getSevenBoomGame() {
        return sevenBoomGame;
    }

    public void setSevenBoomGame(Integer sevenBoomGame) {
        this.sevenBoomGame = sevenBoomGame == null ? 0 : sevenBoomGame;
    }

    public Integer getWordsGame() {
        return wordsGame;
    }

    public void setWordsGame(Integer wordsGame) {
        this.wordsGame = wordsGame == null ? 0 : wordsGame;
    }

    public Integer getFingerMeGame() {
        return fingerMeGame;
    }

    public void setFingerMeGame(Integer fingerMeGame) {
        this.fingerMeGame = fingerMeGame == null ? 0 : fingerMeGame;
    }

    public Integer getColorsGame() {
        return colorsGame;
    }

    public Integer getCrackTheEgg() {
        return crackTheEgg;
    }

    public void setCrackTheEgg(Integer crackTheEgg) {
        this.crackTheEgg = crackTheEgg == null ? 0 : crackTheEgg;
    }

    public void setColorsGame(Integer colorsGame) {
        this.colorsGame = colorsGame == null ? 0 : colorsGame;
    }
}
