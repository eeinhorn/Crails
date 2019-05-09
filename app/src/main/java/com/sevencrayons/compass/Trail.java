package com.sevencrayons.compass;

import android.location.Location;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Trail {

    public ArrayList<Node> points = new ArrayList<Node>();

    public void addNode(Location location) {
        Node n = new Node(location);
        points.add(n);
    }

    public Node getNode(int index) {
        return points.get(index);
    }

    public ArrayList<Node> getFlags() {
        ArrayList<Node> list = new ArrayList<Node>();
        Node current;
        for(int i=0; i<points.size(); i++) {
            current = points.get(i);
            if(current.isFlagged())
                list.add(current);
        }
        return list;
    }

    public String toString() {
        String str = "";
        Node n;
        for(int i=0; i<points.size(); i++) {
            n = points.get(i);
            str += "loc" + i + ": " + n.location.toString() + "\n";
        }
        return str;
    }


    public static class Node {

        Timestamp time;
        Location location;
        boolean flagged;
        String message;

        public Node(Location loc) {
            location = loc;
            flagged = false;
            time = new Timestamp(System.currentTimeMillis());
            message = "";
        }

        public void flagNode(String message) {
            flagged = true;
            this.message = message;
        }

        public boolean isFlagged() {
            return this.flagged;
        }
    }
}
