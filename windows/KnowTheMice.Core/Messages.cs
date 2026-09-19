using System.Text.Json.Serialization;

namespace KnowTheMice.Core;

public class BaseMessage
{
    [JsonPropertyName("type")]
    public string Type { get; set; } = string.Empty;

    [JsonPropertyName("seq")]
    public long Seq { get; set; }

    [JsonPropertyName("timestamp")]
    public long Timestamp { get; set; } = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
}

public class DiscoveryBeacon
{
    [JsonPropertyName("service")]
    public string Service { get; set; } = "know-the-mice";

    [JsonPropertyName("version")]
    public string Version { get; set; } = "1.0.0";

    [JsonPropertyName("id")]
    public string Id { get; set; } = Guid.NewGuid().ToString();

    [JsonPropertyName("name")]
    public string Name { get; set; } = Environment.MachineName;

    [JsonPropertyName("os")]
    public string Os { get; set; } = Environment.OSVersion.ToString();

    [JsonPropertyName("port")]
    public int Port { get; set; } = 52841;

    [JsonPropertyName("state")]
    public string State { get; set; } = "READY";
}

public class PairRequestMessage : BaseMessage
{
    public PairRequestMessage() { Type = "PAIR_REQUEST"; }

    [JsonPropertyName("clientName")]
    public string ClientName { get; set; } = string.Empty;

    [JsonPropertyName("clientId")]
    public string ClientId { get; set; } = string.Empty;

    [JsonPropertyName("clientPub")]
    public string ClientPub { get; set; } = string.Empty;
}

public class PairVerifyMessage : BaseMessage
{
    public PairVerifyMessage() { Type = "PAIR_VERIFY"; }

    [JsonPropertyName("clientId")]
    public string ClientId { get; set; } = string.Empty;

    [JsonPropertyName("authProof")]
    public string AuthProof { get; set; } = string.Empty;

    [JsonPropertyName("pin")]
    public string Pin { get; set; } = string.Empty;
}

public class PairResponseMessage : BaseMessage
{
    public PairResponseMessage() { Type = "PAIR_RESPONSE"; }

    [JsonPropertyName("success")]
    public bool Success { get; set; }

    [JsonPropertyName("authToken")]
    public string AuthToken { get; set; } = string.Empty;

    [JsonPropertyName("hostPub")]
    public string HostPub { get; set; } = string.Empty;

    [JsonPropertyName("message")]
    public string Message { get; set; } = string.Empty;
}

public class AuthMessage : BaseMessage
{
    public AuthMessage() { Type = "AUTH"; }

    [JsonPropertyName("clientId")]
    public string ClientId { get; set; } = string.Empty;

    [JsonPropertyName("authToken")]
    public string AuthToken { get; set; } = string.Empty;
}

public class MouseClickMessage : BaseMessage
{
    public MouseClickMessage() { Type = "MOUSE_CLICK"; }

    [JsonPropertyName("button")]
    public string Button { get; set; } = "LEFT";

    [JsonPropertyName("action")]
    public string Action { get; set; } = "CLICK";
}

public class MouseScrollMessage : BaseMessage
{
    public MouseScrollMessage() { Type = "MOUSE_SCROLL"; }

    [JsonPropertyName("dx")]
    public float Dx { get; set; }

    [JsonPropertyName("dy")]
    public float Dy { get; set; }
}

public class KeyInputMessage : BaseMessage
{
    public KeyInputMessage() { Type = "KEY_INPUT"; }

    [JsonPropertyName("key")]
    public string Key { get; set; } = string.Empty;

    [JsonPropertyName("code")]
    public int Code { get; set; }

    [JsonPropertyName("action")]
    public string Action { get; set; } = "PRESS";

    [JsonPropertyName("modifiers")]
    public List<string> Modifiers { get; set; } = new();
}

public class MediaMessage : BaseMessage
{
    public MediaMessage() { Type = "MEDIA_CMD"; }

    [JsonPropertyName("action")]
    public string Action { get; set; } = "PLAY_PAUSE";
}

public class PresentationMessage : BaseMessage
{
    public PresentationMessage() { Type = "PRESENTATION_CMD"; }

    [JsonPropertyName("action")]
    public string Action { get; set; } = "NEXT_SLIDE";
}

public class PowerMessage : BaseMessage
{
    public PowerMessage() { Type = "POWER_CMD"; }

    [JsonPropertyName("action")]
    public string Action { get; set; } = "LOCK";

    [JsonPropertyName("confirmed")]
    public bool Confirmed { get; set; }

    [JsonPropertyName("targetPc")]
    public string TargetPc { get; set; } = string.Empty;
}

public class AppLaunchMessage : BaseMessage
{
    public AppLaunchMessage() { Type = "APP_LAUNCH"; }

    [JsonPropertyName("appId")]
    public string AppId { get; set; } = string.Empty;
}

public class PingMessage : BaseMessage
{
    public PingMessage() { Type = "PING"; }
}

public class PongMessage : BaseMessage
{
    public PongMessage() { Type = "PONG"; }

    [JsonPropertyName("hostTime")]
    public long HostTime { get; set; } = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
}
