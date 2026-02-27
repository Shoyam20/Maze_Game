// // Source - https://stackoverflow.com/q/12648898
// // Posted by Batteries, modified by community. See post 'Timeline' for change history
// // Retrieved 2026-02-27, License - CC BY-SA 3.0

// import java.util.*;
// import java.io.File;
// public class MazeGame {

// public static void main(String[] args) throws Exception {
//     Scanner scan = new Scanner(new File("maze.txt"));
//     Scanner user = new Scanner(System.in);
//     int rows = scan.nextInt();
//     int columns = scan.nextInt();
//     int px = 0;
//     int py = 0;
//     String [][] maze = new String[rows][columns];
//     String junk = scan.nextLine();

//     for (int i = 0; i < rows; i++){
//         String temp = scan.nextLine();
//         String[] arrayPasser = temp.split("");
//         for (int j = 0; j < columns; j++){
//             maze[i][j] = arrayPasser[i];
//         }
//     }

//     boolean gotTreasure = false;

//     while (gotTreasure = false){
//         for (int i = 0; i < rows; i++){
//             for (int j = 0; j < columns; j++){
//                 System.out.print(maze[i][j]);
//                 System.out.print(" ");
//         }
//             System.out.print("\n");
//       }


//         System.out.printf("\n");
//         System.out.println("You may:");
//         System.out.println("1) Move up");
//         System.out.println("2) Move down");
//         System.out.println("3) Move left");
//         System.out.println("4) Move right");
//         System.out.println("0) Quit");
//         int choice = user.nextInt();
//         int i = 0;

//         if (choice == 1 && i >= 0 && i < columns){
//             for (int k = 0; k < rows; k++){
//                 for (int l = 0; l < columns; l++){
//                     if (maze[k][l].equals(maze[px][py]) && maze[px][py-1].equals("X") == false){
//                         maze[px][py] = ".";
//                         maze[k][l-1] = "P";
//                         maze[px][py] = maze[k][l-1];
//                     }else if (maze[px][py-1] == "X"){
//                         System.out.println("Cannot move into a cave-in! Try something else.");
//                     }else {
//                     continue;}


//                     }
//                 }
//             }
//         else if (choice == 2 && i >= 0 && i < columns){
//             for (int k = 0; k < rows; k++){
//                 for (int l = 0; l < columns; l++){
//                     if (maze[k][l].equals(maze[px][py]) && maze[px][py+1].equals("X") == false){
//                         maze[px][py] = ".";
//                         maze[k][l+1] = "P";
//                         maze[px][py] = maze[k][l+1];
//                     }else if (maze[px][py+1] == "X"){
//                         System.out.println("Cannot move into a cave-in! Try something else.");
//                     }else {
//                     continue;}

//                }
//              }
//             }
//         else if (choice == 3 && i >= 0 && i < columns){
//             for (int k = 0; k < rows; k++){
//                 for (int l = 0; l < columns; l++){
//                     if (maze[k][l].equals(maze[px][py]) && maze[px-1][py].equals("X") == false){
//                         maze[px][py] = ".";
//                         maze[k-1][l] = "P";
//                         maze[px][py] = maze[k-1][l];
//                     }else if (maze[px-1][py] == "X"){
//                         System.out.println("Cannot move into a cave-in! Try something else.");
//                     }else {
//                     continue;}
//                 }
//             }
//         }
//         else if (choice == 4 && i >= 0 && i < columns){
//             for (int k = 0; k < rows; k++){
//                 for (int l = 0; l < columns; l++){
//                     if (maze[k][l].equals(maze[px][py]) && maze[px+1][py].equals("X") == false){
//                         maze[px][py] = ".";
//                         maze[k+1][l] = "P";
//                         maze[px][py] = maze[k+1][l];
//                     }else if (maze[px+1][py] == "X"){
//                         System.out.println("Cannot move into a cave-in! Try something else.");
//                     }else {
//                     continue;}
//                 }
//             }
//         }
//         else if (choice == 0){
//             System.exit(0);
//         }
//     }

//     System.out.println("Congratulations, you found the treasure!");

//     scan.close();
//     user.close();
//         }

//     }









import java.io.File;
import java.util.*;

public class MazeGame {

    public static void main(String[] args) throws Exception {

        Scanner scan = new Scanner(new File("maze.txt"));
        Scanner user = new Scanner(System.in);

        int rows = scan.nextInt();
        int columns = scan.nextInt();
        scan.nextLine(); // consume leftover line

        String[][] maze = new String[rows][columns];

        int px = 0;
        int py = 0;

        // Load maze from file
        for (int i = 0; i < rows; i++) {
            String line = scan.nextLine();
            for (int j = 0; j < columns; j++) {
                maze[i][j] = String.valueOf(line.charAt(j));

                if (maze[i][j].equals("P")) {
                    px = i;
                    py = j;
                }
            }
        }

        boolean gotTreasure = false;

        while (!gotTreasure) {

            printMaze(maze, rows, columns);

            System.out.println("You may:");
            System.out.println("1) Move Up");
            System.out.println("2) Move Down");
            System.out.println("3) Move Left");
            System.out.println("4) Move Right");
            System.out.println("0) Quit");

            int choice = user.nextInt();

            int newX = px;
            int newY = py;

            if (choice == 1) newX--;        // Up
            else if (choice == 2) newX++;   // Down
            else if (choice == 3) newY--;   // Left
            else if (choice == 4) newY++;   // Right
            else if (choice == 0) System.exit(0);

            // Check boundaries
            if (newX >= 0 && newX < rows && newY >= 0 && newY < columns) {

                if (maze[newX][newY].equals("X")) {
                    System.out.println("Cannot move into a wall! Try again.");
                }
                else {
                    if (maze[newX][newY].equals("T")) {
                        gotTreasure = true;
                    }

                    maze[px][py] = ".";
                    px = newX;
                    py = newY;
                    maze[px][py] = "P";
                }
            }
            else {
                System.out.println("Out of bounds! Try again.");
            }
        }

        printMaze(maze, rows, columns);
        System.out.println("🎉 Congratulations! You found the treasure!");

        scan.close();
        user.close();
    }

    // Terminal Box Representation
    public static void printMaze(String[][] maze, int rows, int columns) {

        for (int i = 0; i < columns; i++) {
            System.out.print("+---");
        }
        System.out.println("+");

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < columns; j++) {

                System.out.print("| ");

                if (maze[i][j].equals(".")) {
                    System.out.print(" ");
                }
                else {
                    System.out.print(maze[i][j]);
                }

                System.out.print(" ");
            }

            System.out.println("|");

            for (int k = 0; k < columns; k++) {
                System.out.print("+---");
            }
            System.out.println("+");
        }
    }
}