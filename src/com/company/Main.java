package com.company;

import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static String[][] filledBoard;
    static String[][] blankBoard;
    static Random rng = new Random();

    public static void main(String[] args) {
        boolean dead = false;
        Scanner sc = new Scanner(System.in);
        initializeFilledBoard();
        initializeBlankBoard();
        System.out.println("Welcome to Minesweeper, please input a command: ");
        System.out.println("\"dig x y\" will reveal the square at x, y.");
        System.out.println("\"flag x y\" will flag the square at x, y.");
        printBoard(blankBoard);
        while (!dead) {
            try{
                String[] parts = sc.nextLine().split(" ");
                String command = parts[0];
                int cx = Integer.parseInt(parts[1], 16);
                int cy = Integer.parseInt(parts[2], 16);
                boolean changed = false;
                switch (command) {
                    case "dig":
                        changed = dig(cx, cy);
                        break;
                    case "flag":
                        changed = flag(cx, cy);
                        break;
                }
                for (String[] row : blankBoard) {
                    for (String square : row) {
                        if (square == "*") dead = true;
                    }
                }
                if (changed) printBoard(blankBoard);
            } catch (Exception e) {
                System.out.println("Invalid command");
                System.out.println("\"dig x y\" will reveal the square at x, y.");
                System.out.println("\"flag x y\" will flag the square at x, y.");
            }
        }
        printBoard(filledBoard);
        System.out.println("You died!");
        sc.close();
    }

    private static boolean flag(int cx, int cy) {
        String cell = blankBoard[cx][cy];
        if (!(cell.equals("#") ||  cell.equals("F"))) {
            System.out.println("You cannot flag that cell");
            return false;
        }
        blankBoard[cx][cy] = cell.equals("F") ? "#" : "F";
        return true;
    }

    private static boolean dig(int cx, int cy) {
        if (blankBoard[cx][cy].equals("F")) {
            System.out.println("You must unflag before digging by calling flag on that cell");
            return false;
        }
        blankBoard[cx][cy] = filledBoard[cx][cy];
        if (blankBoard[cx][cy].equals(" ")) autoDig(cx, cy);
        return true;
    }

    public static void printBoard(String[][] board) {
        System.out.println("  0123456789abcdef");
        for (int ii = 0 ; ii < 16; ii += 1) {
            System.out.print(Integer.toHexString(ii) + " ");
            for (int jj = 0 ; jj < 16 ; jj += 1) {
                System.out.print(board[ii][jj]);
            }
            System.out.println("");
        }
    }
    public static void initializeFilledBoard() {
        filledBoard = new String[16][16];
        for (int ii = 0; ii < 40; ii += 1) {
            int x = rng.nextInt(16);
            int y = rng.nextInt(16);
            if (!Objects.equals(filledBoard[x][y], "*")) filledBoard[x][y] = "*";
        }
        for (int ii = 0; ii < 16; ii += 1) {
            for (int jj = 0; jj < 16; jj += 1) {
                if(!Objects.equals(filledBoard[ii][jj], "*")) {
                    int n = adjacentMines(ii, jj);
                    filledBoard[ii][jj] = n == 0 ? " " : String.valueOf(n);
                }
            }
        }
    }

    public static void initializeBlankBoard() {
        blankBoard = new String[16][16];
        for (String[] row : blankBoard) {
            for (int ii = 0 ; ii < 16 ; ii += 1) {
                row[ii] = "#";
            }
        }
    }

    public interface BoardOp {
        void apply (int x, int y, String cell);
    }

    public static void onNeighbors(int x, int y, BoardOp op) {
        for (int dy = -1; dy <= 1; dy += 1) {
            for (int dx = -1; dx <= 1; dx += 1) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx, ny = y + dy;
                if (nx < 0 || ny < 0 || nx >= filledBoard.length || ny >= filledBoard[0].length) continue;
                op.apply(nx, ny, filledBoard[nx][ny]);
            }
        }
    }

    public static int adjacentMines(int x, int y) {
        int[] total = new int[1];
        onNeighbors(x, y, (nx, ny, cell) -> {
            if ("*".equals(cell)) total[0] += 1;
        });
        return total[0];

    }
    public static void autoDig(int x, int y) {
        onNeighbors(x, y, (nx, ny, cell) -> {
            if ("#".equals(blankBoard[nx][ny])) dig(nx, ny);
        });

    }
}