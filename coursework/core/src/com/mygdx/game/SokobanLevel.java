package com.mygdx.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class SokobanLevel extends Level{
    private int levelLength;
    private int levelWidth;
    private int[][] sokLevelArr;
    private String sokLevelPath;
    private int playerPosGridX;
    private int playerPosGridY;
    public SokobanLevel(int numOfLevels){
        super("Sokoban Level");
        Random rand = new Random();
        int randNum = rand.nextInt(numOfLevels) + 1;
        sokLevelPath = "./coursework/assets/sokobanLevels";
        //sokLevelPath = "C://Users//a.nachiappan23//courseworkRepo//coursework//coursework//assets//sokobanLevels";
        //"C:/Users/User/CourseworkRepo/courseworkAN/coursework/assets/sokobanLevels"
        //"C://Users//a.nachiappan23//courseworkRepo//coursework//coursework//assets//sokobanLevels"
        int[] vals = genStart(sokLevelPath, randNum);
        int startNum = vals[0];
        int lineNum = startNum;
        levelLength = vals[1];
        levelWidth = vals[2];
        setWindowWidth(levelWidth);
        setWindowHeight(levelLength);
        maxStarCount = 0;
        levelInstructions = "Move all the boxes(white squares onto stars)\n" +
                "To move a box, move to the position the box is on\n" +
                "Press E to exit level\n";
        sokLevelArr = new int[levelLength][levelWidth];
        for(int i = 0; i <= levelLength - 1; i++){
            for(int j = 0; j <= levelWidth - 1; j++){
                sokLevelArr[i][j] = 1;//empty background tile
            }
        }
        System.out.println(lineNum);
        String line = readLineFrom(sokLevelPath, lineNum);
        boolean done = false;
        //code below builds the Sokoban Level board array
        while(true){
            assert line != null;
            if (line.isEmpty()) break;
            ;
            StringBuilder spaces = new StringBuilder();
            while(line.length() + spaces.length() < levelWidth- 1){
                spaces.append(" ");
            }
            line += spaces.toString();
            System.out.println(lineNum);
            System.out.println(line);
            for(int j = 0; j < line.length(); j++){
                if(line.charAt(j) == '#'){
                    sokLevelArr[lineNum - startNum][j] = 7;
                    if(!done && j == 1){//the code inside this if statement will just execute once, it accessed second tile if it hasn't set an entrance tile yet
                        done = true;
                        setEntranceLevelX(j);//if entranceLevelX has a value it will just append the current value which is what happens here
                        setEntranceLevelY(lineNum - startNum);//sets the y coordinate the amount we've moved in the y direction
                    }
                }
                else if(line.charAt(j) == '.'){
                    sokLevelArr[lineNum - startNum][j] = 3;
                    maxStarCount += 1;
                }
                else if(line.charAt(j) == '$'){
                    sokLevelArr[lineNum - startNum][j] = 5;
                }
                else if(line.charAt(j) == '@'){
                    sokLevelArr[lineNum - startNum][j] = 2;
                    playerPosGridY = lineNum - startNum;
                    playerPosGridX = j;
                    setInitPlayerPosGridX(playerPosGridX);
                    setInitPlayerPosGridY(levelLength - playerPosGridY - 1);//because length starts from 1 while the position starts from 0
                } else if (line.charAt(j) == '*') {
                    sokLevelArr[lineNum - startNum][j] = 9;
                    maxStarCount += 1;
                } else {
                    sokLevelArr[lineNum - startNum][j] = 2;
                }
            }
            lineNum += 1;
            line = readLineFrom(sokLevelPath, lineNum);
        }
        super.setBoardArr(sokLevelArr);

    }

    private static String readLineFrom(String filePath, int lineNumber){
        String line = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for (int i = 1; i <= lineNumber; i++) {
                line = br.readLine();
                if (line == null) return null; // Return null if the line doesn't exist
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
    private static int[] genStart(String filePath, int levelNumber){
        String line = "";
        int lineNum = 0;
        int startNum = 0;
        int levelWidth = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while(!(line.equals("Level " + levelNumber))) {
                line = br.readLine();//keeps reading the next line in the file that's not been read yet
                lineNum += 1;
                assert line != null;//think the line.equals() has a problem with null values
            }
            startNum = lineNum + 2;//first two lines are just random strings
            while(!line.isEmpty()){//There is an empty line after each Sokoban level
                line = br.readLine();
                lineNum += 1;
                if(!line.isEmpty()){
                    if(lineNum >= startNum && line.length() >= levelWidth){//This calculates the largest width of level because sometimes levels are uneven
                        levelWidth = line.length();//sets it to larger level width
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[]{startNum, lineNum - startNum, levelWidth};//returns line to start from, level height and level width
    }


    public int getLevelLength() {
        return levelLength;
    }

    public int getLevelWidth() {
        return levelWidth;
    }

    public int[][] getSokLevelArr() {
        return sokLevelArr;
    }

    public int getPlayerPosGridX() {
        return playerPosGridX;
    }

    public int getPlayerPosGridY() {
        return playerPosGridY;
    }

}
