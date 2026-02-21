package com.mygdx.game;

import com.mygdx.game.Delaunay.DEdge;

import java.util.ArrayList;
import java.util.Random;

public class Path {
    private Block[][] boardCopy;
    private ArrayList<Block> blocksToConsider;
    private int currBlockInd;
    private int xCurr, yCurr, x, y;
    private int btXCurr, btYCurr;
    private int moveX, moveY;
    private Random rand;
    public Path(int startX, int startY, int endX, int endY, int boardLength, int[][] board){
        //with the board changes may not be reflected because passed in as parameter by value
        boardCopy = new Block[boardLength][boardLength];//the parameter being is not in the design stage
        boardCopy[startX][startY] = new Block(0, getDistPoint(startX, startY, endX, endY), 0, 0, startX, startY);//block takes in different number of parameters
        blocksToConsider = new ArrayList<>();
        blocksToConsider.add(boardCopy[startX][startY]);
        currBlockInd = 0;
        rand = new Random();
        //A* algorithm is implemented below
        while(blocksToConsider.get(currBlockInd).getDistEnd() != 0){
            xCurr = blocksToConsider.get(currBlockInd).getX();
            yCurr = blocksToConsider.get(currBlockInd).getY();
            for(int i = -1; i <= 1; i++){//the two for loops here are to get left,right, bottom, top squares
                for(int j = -1; j <= 1; j++){
                    x = xCurr + i;
                    y = yCurr + j;//possible coordinates of left,right, bottom, top squares
                    if(x >= 0 && y >= 0 && x < boardLength && y < boardLength){
                        if(boardCopy[y][x] == null && (board[y][x] == 1 || board[y][x] == 2 )){//path only goes through brick tiles and doesn't check tiles already occupied
                            boardCopy[y][x] = new Block(getDistPoint(x, y, startX, startY), getDistPoint(x, y, endX, endY), -i, -j, x, y);
                            blocksToConsider.add(boardCopy[y][x]);//adds node because it could be visited next
                        }
                    }
                }
            }
            blocksToConsider.remove(currBlockInd);//removes nodes because it's been visited
            currBlockInd = getShortestDistBlock(blocksToConsider);
        }
        //We want to now place tiles along the route
        btXCurr = endX;
        btYCurr = endY;
        moveX = boardCopy[btYCurr][btXCurr].getBacktrackX();
        moveY = boardCopy[btYCurr][btXCurr].getBacktrackY();
        board[btYCurr][btXCurr] = 2;
        while(moveX != 0 || moveY != 0){
            btXCurr += moveX;
            btYCurr += moveY;
            board[btYCurr][btXCurr] = 2;
            if(moveX != 0 && moveY != 0){//sometimes the paths are placed diagonally
                //There is no movement to move diagonally(you can only move sideways or up/down) so this code places it sideways or vertically
                if(board[btYCurr-moveY][btXCurr] != 2 && board[btYCurr-moveY][btXCurr] != 1){//makes sure vertical tile isn't part of a level
                    board[btYCurr][btXCurr - moveX] = 2;
                }
                else if(board[btYCurr][btXCurr - moveX] != 2 && board[btYCurr][btXCurr - moveX] != 1){//makes sure horizontal tile isn't part of a level
                    board[btYCurr-moveY][btXCurr] = 2;
                }
                else{//horizontal and vertical are vacant
                    if(rand.nextBoolean()){//generates randomly to place ladder tile horizontally or vertically
                        board[btYCurr-moveY][btXCurr] = 2;//places vertically to current cell
                    }
                    else{
                        board[btYCurr][btXCurr - moveX] = 2;//places horizontally to current cell
                    }
                }
            }
            moveX = boardCopy[btYCurr][btXCurr].getBacktrackX();
            moveY = boardCopy[btYCurr][btXCurr].getBacktrackY();
        }
    }
    private double getDistPoint(int x1, int y1, int x2, int y2){//function that returns Euclidean distance
        double dist = (Math.sqrt((Math.pow(y2-y1, 2) + Math.pow(x2 - x1, 2))));//applies pythagoras theorem
        return dist;
    }
    private int getShortestDistBlock(ArrayList<Block> blocksToConsider){
        int shortestDistBlockInd = 0;
        double shortestDist = blocksToConsider.get(0).getTotalDist();
        for(int i = 1; i < blocksToConsider.size(); i++){
            if(blocksToConsider.get(i).getTotalDist() < shortestDist) {
                shortestDistBlockInd = i;
                shortestDist = blocksToConsider.get(i).getTotalDist();
            }
        }
        return shortestDistBlockInd;

    }
    private void dispBoard(int[][] board, int x, int y){
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        for(int i = 0; i < board.length; i++){
            StringBuilder s = new StringBuilder();
            for(int j = 0; j < board.length; j++){
                if(i == y && j == x){
                    s.append(9);
                }else{
                    s.append(board[i][j]);
                }
            }
            System.out.println(s);
        }
    }
}
