using System.Buffers.Binary;

namespace KnowTheMice.Core;

public struct BinaryMotionFrame
{
    public const byte MAGIC_BYTE = 0x01;
    public const int FRAME_SIZE = 13;

    public float Dx { get; set; }
    public float Dy { get; set; }
    public ushort Sequence { get; set; }
    public byte Flags { get; set; }

    public bool LeftDown => (Flags & 0x01) != 0;
    public bool RightDown => (Flags & 0x02) != 0;
    public bool MiddleDown => (Flags & 0x04) != 0;
    public bool DragMode => (Flags & 0x08) != 0;

    public static bool TryParse(ReadOnlySpan<byte> buffer, out BinaryMotionFrame frame)
    {
        frame = default;
        if (buffer.Length < FRAME_SIZE || buffer[0] != MAGIC_BYTE)
            return false;

        // Verify XOR Checksum
        byte checksum = 0;
        for (int i = 0; i < 12; i++)
        {
            checksum ^= buffer[i];
        }

        if (checksum != buffer[12])
            return false;

        frame.Dx = BinaryPrimitives.ReadSingleLittleEndian(buffer.Slice(1, 4));
        frame.Dy = BinaryPrimitives.ReadSingleLittleEndian(buffer.Slice(5, 4));
        frame.Sequence = BinaryPrimitives.ReadUInt16LittleEndian(buffer.Slice(9, 2));
        frame.Flags = buffer[11];
        return true;
    }

    public static byte[] Serialize(float dx, float dy, ushort seq, byte flags)
    {
        byte[] buffer = new byte[FRAME_SIZE];
        buffer[0] = MAGIC_BYTE;
        BinaryPrimitives.WriteSingleLittleEndian(buffer.AsSpan(1, 4), dx);
        BinaryPrimitives.WriteSingleLittleEndian(buffer.AsSpan(5, 4), dy);
        BinaryPrimitives.WriteUInt16LittleEndian(buffer.AsSpan(9, 2), seq);
        buffer[11] = flags;

        byte checksum = 0;
        for (int i = 0; i < 12; i++)
        {
            checksum ^= buffer[i];
        }
        buffer[12] = checksum;

        return buffer;
    }
}
