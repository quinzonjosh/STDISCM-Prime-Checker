public class Request {

    private final String clientInfo;
    private final int startPoint;
    private final int endPoint;

    public Request(String clientInfo, int startPoint, int endPoint) {
        this.clientInfo = clientInfo;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public int getEndPoint() {
        return endPoint;
    }
}
