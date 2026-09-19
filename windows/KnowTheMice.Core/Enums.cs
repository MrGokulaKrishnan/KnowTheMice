namespace KnowTheMice.Core;

public enum ConnectionState
{
    DISCOVERING,
    PAIRING,
    CONNECTING,
    CONNECTED,
    RECONNECTING,
    DISCONNECTED,
    BLOCKED,
    ERROR
}

public enum MouseButton
{
    LEFT,
    RIGHT,
    MIDDLE,
    DOUBLE
}

public enum MouseAction
{
    DOWN,
    UP,
    CLICK
}

public enum KeyAction
{
    DOWN,
    UP,
    PRESS
}

public enum MediaAction
{
    PLAY_PAUSE,
    NEXT,
    PREV,
    STOP,
    VOL_UP,
    VOL_DOWN,
    MUTE
}

public enum PresentationAction
{
    NEXT_SLIDE,
    PREV_SLIDE,
    START,
    END,
    BLACK_SCREEN,
    WHITE_SCREEN
}

public enum PowerAction
{
    LOCK,
    SLEEP,
    HIBERNATE,
    RESTART,
    SHUTDOWN
}
