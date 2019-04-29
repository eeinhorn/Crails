package com.sevencrayons.compass;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Trail {

    public ArrayList<Node> points = new ArrayList<Node>();

    public void addNode(double lon, double lat, double alt) {
        Node n = new Node(lon, lat, alt);
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
            str += "lat"+i+": "+n.latitude+"\n";
            str += "long"+i+": "+n.longitude+"\n";
            str += "alt"+i+": "+n.altitude+"\n";
        }
        return str;
    }


    public static class Node {

        Timestamp time;
        double longitude;
        double latitude;
        double altitude;
        boolean flagged;
        String message;

        public Node(double lon, double lat, double alt) {
            this.longitude = lon;
            this.latitude = lat;
            this.altitude = alt;
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
