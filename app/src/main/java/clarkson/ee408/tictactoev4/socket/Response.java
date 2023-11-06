package clarkson.ee408.tictactoev4.socket;

/**
 * Server response to a client request
 */
public class Response {
    /**
     * Enumeration type for different response statuses.
     */
    public enum ResponseStatus {
        SUCCESS,
        FAILURE
    }

    private ResponseStatus status;
    private String message;

    /**
     * Default constructor for the Response class.
     * Initializes the response status to FAILURE and the message to an empty string.
     */
    public Response() {
        this.status = ResponseStatus.FAILURE;
        this.message = null;
    }

    /**
     * Constructor for creating a Response with specified attributes.
     *
     * @param status  The response status.
     * @param message The message associated with the response.
     */
    public Response(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Gets the response status.
     *
     * @return The response status.
     */
    public ResponseStatus getStatus() {
        return status;
    }

    /**
     * Sets the response status.
     *
     * @param status The new response status.
     */
    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    /**
     * Gets the message associated with the response.
     *
     * @return The message associated with the response.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the response.
     *
     * @param message The new message associated with the response.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
