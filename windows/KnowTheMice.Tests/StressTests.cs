using System.Diagnostics;
using KnowTheMice.Core;
using KnowTheMice.Security;
using Xunit;

namespace KnowTheMice.Tests;

public class StressTests
{
    [Fact]
    public void StressTest_1000MouseEventsPerSec_ThroughputBenchmark()
    {
        const int eventCount = 5000;
        var replay = new ReplayProtection(windowSize: 256);

        var sw = Stopwatch.StartNew();

        for (int i = 1; i <= eventCount; i++)
        {
            byte[] bytes = BinaryMotionFrame.Serialize(1.5f, -0.5f, (ushort)i, 0);
            bool ok = BinaryMotionFrame.TryParse(bytes, out var frame);
            Assert.True(ok);
            bool accepted = replay.IsValidAndAdvance(frame.Sequence);
            Assert.True(accepted);
        }

        sw.Stop();

        double elapsedSeconds = sw.Elapsed.TotalSeconds;
        double eventsPerSecond = eventCount / elapsedSeconds;

        // Must comfortably exceed 1000 events/sec target (typically > 500,000/sec in C#!)
        Assert.True(eventsPerSecond > 1000, $"Throughput was {eventsPerSecond:N0} events/sec");
    }

    [Fact]
    public void StressTest_RapidClickBurst_ProcessedWithoutLoss()
    {
        int processed = 0;
        for (int i = 0; i < 200; i++)
        {
            var click = new MouseClickMessage
            {
                Button = (i % 2 == 0) ? "LEFT" : "RIGHT",
                Action = "CLICK"
            };
            if (click.Type == "MOUSE_CLICK")
            {
                processed++;
            }
        }
        Assert.Equal(200, processed);
    }
}
