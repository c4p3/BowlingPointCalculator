public class BowlingData {
    private String token;
    private int[][] points;
    private int[][] calculatedFramePoints;

    public String getToken() {
        return token;
    }

    public int[][] getPoints() {
        return points;
    }

    public int[][] getCalculatedFramePoints() {
        return calculatedFramePoints;
    }

    public void setCalculatedFramePoints(int[][] calculatedFramePoints) {
        this.calculatedFramePoints = calculatedFramePoints;
    }

}