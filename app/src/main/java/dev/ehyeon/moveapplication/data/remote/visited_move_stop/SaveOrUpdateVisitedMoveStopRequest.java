package dev.ehyeon.moveapplication.data.remote.visited_move_stop;

public class SaveOrUpdateVisitedMoveStopRequest {

    private final double memberLatitude;
    private final double memberLongitude;

    // 남서쪽
    private final double latitude1;

    private final double longitude1;

    // 북동쪽
    private final double latitude2;
    private final double longitude2;

    public SaveOrUpdateVisitedMoveStopRequest(double memberLatitude, double memberLongitude, double latitude1, double longitude1, double latitude2, double longitude2) {
        this.memberLatitude = memberLatitude;
        this.memberLongitude = memberLongitude;
        this.latitude1 = latitude1;
        this.longitude1 = longitude1;
        this.latitude2 = latitude2;
        this.longitude2 = longitude2;
    }

    public double getMemberLatitude() {
        return memberLatitude;
    }

    public double getMemberLongitude() {
        return memberLongitude;
    }

    public double getLatitude1() {
        return latitude1;
    }

    public double getLongitude1() {
        return longitude1;
    }

    public double getLatitude2() {
        return latitude2;
    }

    public double getLongitude2() {
        return longitude2;
    }
}
