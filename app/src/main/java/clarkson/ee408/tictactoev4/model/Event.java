package clarkson.ee408.tictactoev4.model;

/**
 * Models a game lifecycle, from initiating a request to being declined, completed,
 * or aborted after being accepted.
 */
public class Event {
    /**
     * Enumeration type for different game status.
     */
    public enum EventStatus {
        PENDING,
        DECLINED,
        ACCEPTED,
        PLAYING,
        COMPLETED,
        ABORTED
    }

    private int eventId;
    private String sender;
    private String opponent;
    private EventStatus status;
    private String turn;
    private int move;

    /**
     * Default constructor for the Event class.
     * Initializes eventId, sender, opponent, status, turn, and move to default values.
     */
    public Event() {
        this.eventId = 0;
        this.sender = null;
        this.opponent = null;
        this.status = EventStatus.PENDING;
        this.turn = null;
        this.move = 0;
    }

    /**
     * Constructor for creating an Event with specified attributes.
     *
     * @param eventId   A global unique integer to represent the event.
     * @param sender    Represents the username of the user that sends the game invitation.
     * @param opponent  Represents the username of the user that the game invitation was sent to.
     * @param status    Represents the status of the game. It is of type EventStatus.
     * @param turn      The username of the player that made the last move.
     * @param move      An integer storing the last move of the game.
     */
    public Event(int eventId, String sender, String opponent, EventStatus status, String turn, int move) {
        this.eventId = eventId;
        this.sender = sender;
        this.opponent = opponent;
        this.status = status;
        this.turn = turn;
        this.move = move;
    }

    /**
     * Gets the event ID.
     *
     * @return The event ID.
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID.
     *
     * @param eventId The new event ID.
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the sender's username.
     *
     * @return The sender's username.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets the sender's username.
     *
     * @param sender The new sender's username.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gets the opponent's username.
     *
     * @return The opponent's username.
     */
    public String getOpponent() {
        return opponent;
    }

    /**
     * Sets the opponent's username.
     *
     * @param opponent The new opponent's username.
     */
    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    /**
     * Gets the status of the game.
     *
     * @return The status of the game.
     */
    public EventStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the game.
     *
     * @param status The new status of the game.
     */
    public void setStatus(EventStatus status) {
        this.status = status;
    }

    /**
     * Gets the username of the player who made the last move.
     *
     * @return The username of the player who made the last move.
     */
    public String getTurn() {
        return turn;
    }

    /**
     * Sets the username of the player who made the last move.
     *
     * @param turn The new username of the player who made the last move.
     */
    public void setTurn(String turn) {
        this.turn = turn;
    }

    /**
     * Gets the last move of the game.
     *
     * @return The last move of the game.
     */
    public int getMove() {
        return move;
    }

    /**
     * Sets the last move of the game.
     *
     * @param move The new last move of the game.
     */
    public void setMove(int move) {
        this.move = move;
    }

    /**
     * Compares this event to another event based on their event IDs.
     *
     * @param obj The object to compare to.
     * @return true if the event IDs are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Event)) {
            return false;
        }
        Event otherEvent = (Event) obj;

        return this.eventId == otherEvent.eventId;
    }
}
