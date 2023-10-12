package dev.ehyeon.moveapplication.data.remote.visited_move_stop;

public class SearchVisitedMoveStopResponse {

    private String name;
    private double latitude;
    private double longitude;
    private boolean visited;

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isVisited() {
        return visited;
    }
}
