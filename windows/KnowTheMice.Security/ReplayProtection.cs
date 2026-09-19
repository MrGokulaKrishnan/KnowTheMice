namespace KnowTheMice.Security;

public class ReplayProtection
{
    private long _highestSequence = -1;
    private readonly HashSet<long> _recentSequences = new();
    private readonly int _windowSize;
    private readonly object _lock = new();

    public ReplayProtection(int windowSize = 128)
    {
        _windowSize = windowSize;
    }

    public bool IsValidAndAdvance(long sequence)
    {
        lock (_lock)
        {
            if (sequence <= 0)
                return false;

            // Brand new connection initialization
            if (_highestSequence == -1)
            {
                _highestSequence = sequence;
                _recentSequences.Add(sequence);
                return true;
            }

            // Packet is far too old (behind sliding window)
            if (sequence < _highestSequence - _windowSize)
                return false;

            // Packet already seen (replay attempt)
            if (_recentSequences.Contains(sequence))
                return false;

            // Valid packet: record it
            _recentSequences.Add(sequence);
            if (sequence > _highestSequence)
            {
                _highestSequence = sequence;
                // Prune sequences older than sliding window
                _recentSequences.RemoveWhere(s => s < _highestSequence - _windowSize);
            }

            return true;
        }
    }

    public void Reset()
    {
        lock (_lock)
        {
            _highestSequence = -1;
            _recentSequences.Clear();
        }
    }
}
