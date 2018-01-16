/**
 * Used for throwing the user an error upon a blank text field being submitted.
 */
class BlankStringException extends RuntimeException {

    /**
     * Constructs the basic exception
     */
    public BlankStringException() {
        super();
    }

    /**
     * Constructs a new exception with a specific message
     *
     * @param message
     */
    public BlankStringException(String message) {
        super(message);
    }
}
