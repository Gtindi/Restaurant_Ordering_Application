// To be deleted
package crabfood;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class MyGoogleMap {

    private ArrayList<ArrayList<Character>> map = new ArrayList<>();
    private ArrayList<Position> allRestaurantPositions = new ArrayList<>();
    /**
     * Positions of restaurants in "partner-restaurant.txt" always overweigh the
     * their positions previously recorded in this class.
     */
    private int width = 0;
    private int height = 0;

    public MyGoogleMap() {
        readMap();
    }

    /**
     * load previously saved "map.txt"
     */
    public void readMap() {
        try {
            Scanner s = new Scanner(new FileInputStream("crabfood-io/map.txt"));

            while (s.hasNextLine()) {
                String nextLine = s.nextLine();
                nextLine = nextLine.replaceFirst("0*$", "");

                if (!nextLine.isEmpty()) {
                    height++;

                    if (width < nextLine.length()) {
                        width = nextLine.length();
                    }

                    ArrayList<Character> charList = new ArrayList<>();
                    for (Character ch : nextLine.toCharArray()) {
                        charList.add(ch);
                    }

                    map.add(charList);
                }
            }

            if (!map.isEmpty()) {
                for (ArrayList<Character> charList : map) {
                    while (charList.size() < width) {
                        charList.add('0');
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("\"map.txt\" not found.");
        }
    }

    /**
     * read all restaurant positions again & reprint map into "map.txt"
     */
    public void updateMap() {
        PrintWriter pw = null;

        // take a list of all restaurant positions taken from "partner-restaurant.txt"
        if (!hasOverlappedPositions()) {
            for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                allRestaurantPositions.add(restaurant.getPosition());
            }

            // update width & height to fit the positions of restaurants
            int max_posX = 0;
            int max_posY = 0;
            for (Position p : allRestaurantPositions) {
                if (max_posX < p.getPosX()) {
                    max_posX = p.getPosX();
                }
                if (max_posY < p.getPosY()) {
                    max_posY = p.getPosY();
                }
            }
            width = max_posX + 1;
            height = max_posY + 1;

            // make new map
            map = new ArrayList<>();
            Character[] arr = new Character[width];
            ArrayList<Character> myList = new ArrayList<>(Arrays.asList(arr));
            Collections.fill(myList, '0');
            for (int i = 0; i < height; i++) {
                map.add((ArrayList<Character>) myList.clone());
            }

            for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
                map.get(restaurant.getPosition().getPosY()).set(restaurant.getPosition().getPosX(), restaurant.getMapSymbol());
            }

            // rewrite "map.txt"
            try {
                pw = new PrintWriter(new FileOutputStream("crabfood-io/map.txt"));
                pw.print(this.toString());
            } catch (FileNotFoundException ex) {
                System.out.println("\"map.txt\" not found.");
            } finally {
                if (pw != null) {
                    pw.close();
                }
            }
        } else {
            System.out.println("Positions overlap. Unable to update map.");
        }

    }

    public static int getDistance(Position pos1, Position pos2) {
        return Math.abs(pos1.getPosX() - pos2.getPosX()) + Math.abs(pos1.getPosY() - pos2.getPosY());
    }

    public static int getTravelDuration(Position pos1, Position pos2) {
        return getDistance(pos1, pos2);
    }

    @Override
    public String toString() {
        String mapStr = "";
        for (ArrayList<Character> charList : map) {
            mapStr += charList.toString()
                    .substring(1, charList.toString().length() - 1)
                    .replaceAll(", ", "");
            mapStr += "\n";
        }
        return mapStr;
    }

    public boolean hasOverlappedPositions() {
        ArrayBag<String> allPositions = new ArrayBag<>();
        for (Restaurant restaurant : CrabFoodOperator.getPartnerRestaurants()) {
            allPositions.add(restaurant.getPosition().toString());
        }

        if (!allPositions.isEmpty()) {
            for (Object p : (Object[]) allPositions.toArray()) {
                if (allPositions.getFrequencyOf((String) p) > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public Character getSymbolAt(int posX, int posY) {
        return map.get(posY).get(posX);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList<ArrayList<Character>> getMap() {
        return map;
    }

    public void setMap(ArrayList<ArrayList<Character>> map) {
        this.map = map;
    }

    public ArrayList<Position> getAllRestaurantPositions() {
        return allRestaurantPositions;
    }

    public void setAllRestaurantPositions(ArrayList<Position> allRestaurantPositions) {
        this.allRestaurantPositions = allRestaurantPositions;
    }

    static class Position {

        private int posX;
        private int posY;

        public Position() {
            this.posX = -1;
            this.posY = -1;
        }

        public Position(int posX, int posY) {
            this.posX = posX;
            this.posY = posY;
        }

        public void setPosition(int posX, int posY) {
            this.posX = posX;
            this.posY = posY;
        }

        public int getPosX() {
            return posX;
        }

        public void setPosX(int posX) {
            this.posX = posX;
        }

        public int getPosY() {
            return posY;
        }

        public void setPosY(int posY) {
            this.posY = posY;
        }

        @Override
        public String toString() {
            return String.format("(%d, %d)", posX, posY);
        }
    }
}
