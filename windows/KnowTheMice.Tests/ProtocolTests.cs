using KnowTheMice.Core;
using Xunit;

namespace KnowTheMice.Tests;

public class ProtocolTests
{
    [Fact]
    public void BinaryMotionFrame_SerializeAndDeserialize_MatchesValues()
    {
        float dx = 4.25f;
        float dy = -2.75f;
        ushort seq = 1042;
        byte flags = 0x05; // LeftDown + MiddleDown

        byte[] bytes = BinaryMotionFrame.Serialize(dx, dy, seq, flags);

        Assert.Equal(13, bytes.Length);
        Assert.Equal(BinaryMotionFrame.MAGIC_BYTE, bytes[0]);

        bool parsed = BinaryMotionFrame.TryParse(bytes, out var frame);
        Assert.True(parsed);
        Assert.Equal(dx, frame.Dx);
        Assert.Equal(dy, frame.Dy);
        Assert.Equal(seq, frame.Sequence);
        Assert.True(frame.LeftDown);
        Assert.False(frame.RightDown);
        Assert.True(frame.MiddleDown);
    }

    [Fact]
    public void BinaryMotionFrame_CorruptedChecksum_IsRejected()
    {
        byte[] bytes = BinaryMotionFrame.Serialize(1.0f, 2.0f, 10, 0);
        // Corrupt the checksum byte
        bytes[12] = (byte)(bytes[12] ^ 0xFF);

        bool parsed = BinaryMotionFrame.TryParse(bytes, out _);
        Assert.False(parsed);
    }

    [Fact]
    public void BinaryMotionFrame_WrongMagicByte_IsRejected()
    {
        byte[] bytes = BinaryMotionFrame.Serialize(1.0f, 2.0f, 10, 0);
        bytes[0] = 0x99;

        bool parsed = BinaryMotionFrame.TryParse(bytes, out _);
        Assert.False(parsed);
    }
}
