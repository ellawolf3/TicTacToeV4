package clarkson.ee408.tictactoev4.socket;

/**
 * Clients request that is sent to the server
 */
public class Request {
    /**
     * Enumeration type for different request types.
     */
    public enum RequestType {
        LOGIN,
        REGISTER,
        UPDATE_PAIRING,
        SEND_INVITATION,
        ACCEPT_INVITATION,
        DECLINE_INVITATION,
        ACKNOWLEDGE_RESPONSE,
        REQUEST_MOVE,
        SEND_MOVE,
        ABORT_GAME,
        COMPLETE_GAME
    }

    private RequestType type;
    private String data;

    /**
     * Default constructor for the Request class.
     * Initializes the request type to LOGIN and data to an empty string.
     */
    public Request(RequestType requestType) {
        this.type = RequestType.LOGIN;
        this.data = null;
    }

    /**
     * Constructor for creating a Request with specified attributes.
     *
     * @param type The request type.
     * @param data The data associated with the request.
     */
    public Request(RequestType type, String data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Gets the request type.
     *
     * @return The request type.
     */
    public RequestType getType() {
        return type;
    }

    /**
     * Sets the request type.
     *
     * @param type The new request type.
     */
    public void setType(RequestType type) {
        this.type = type;
    }

    /**
     * Gets the data associated with the request.
     *
     * @return The data associated with the request.
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the data associated with the request.
     *
     * @param data The new data associated with the request.
     */
    public void setData(String data) {
        this.data = data;
    }
}
