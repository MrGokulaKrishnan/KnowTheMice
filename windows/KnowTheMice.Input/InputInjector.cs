using System.Runtime.InteropServices;
using KnowTheMice.Core;

namespace KnowTheMice.Input;

public class InputInjector
{
    private float _sensitivity = 1.0f;
    private bool _naturalScrolling = true;
    private readonly HashSet<ushort> _pressedKeys = new();
    private readonly object _keyLock = new();

    public float Sensitivity
    {
        get => _sensitivity;
        set => _sensitivity = Math.Clamp(value, 0.1f, 5.0f);
    }

    public bool NaturalScrolling
    {
        get => _naturalScrolling;
        set => _naturalScrolling = value;
    }

    public void MoveMouseRelative(float dx, float dy)
    {
        int scaledDx = (int)Math.Round(dx * _sensitivity);
        int scaledDy = (int)Math.Round(dy * _sensitivity);

        if (scaledDx == 0 && scaledDy == 0 && (dx != 0 || dy != 0))
        {
            // Preserve minimum 1px movement for micro-gestures
            scaledDx = dx > 0 ? 1 : (dx < 0 ? -1 : 0);
            scaledDy = dy > 0 ? 1 : (dy < 0 ? -1 : 0);
        }

        var input = new NativeMethods.INPUT
        {
            type = NativeMethods.INPUT_MOUSE,
            u = new NativeMethods.INPUT_UNION
            {
                mi = new NativeMethods.MOUSEINPUT
                {
                    dx = scaledDx,
                    dy = scaledDy,
                    dwFlags = NativeMethods.MOUSEEVENTF_MOVE
                }
            }
        };

        NativeMethods.SendInput(1, new[] { input }, Marshal.SizeOf<NativeMethods.INPUT>());
    }

    public void MouseClick(MouseButton button, MouseAction action)
    {
        uint downFlag = button switch
        {
            MouseButton.LEFT => NativeMethods.MOUSEEVENTF_LEFTDOWN,
            MouseButton.RIGHT => NativeMethods.MOUSEEVENTF_RIGHTDOWN,
            MouseButton.MIDDLE => NativeMethods.MOUSEEVENTF_MIDDLEDOWN,
            MouseButton.DOUBLE => NativeMethods.MOUSEEVENTF_LEFTDOWN,
            _ => NativeMethods.MOUSEEVENTF_LEFTDOWN
        };

        uint upFlag = button switch
        {
            MouseButton.LEFT => NativeMethods.MOUSEEVENTF_LEFTUP,
            MouseButton.RIGHT => NativeMethods.MOUSEEVENTF_RIGHTUP,
            MouseButton.MIDDLE => NativeMethods.MOUSEEVENTF_MIDDLEUP,
            MouseButton.DOUBLE => NativeMethods.MOUSEEVENTF_LEFTUP,
            _ => NativeMethods.MOUSEEVENTF_LEFTUP
        };

        if (button == MouseButton.DOUBLE)
        {
            // Double-click sequence with clean separation
            SendMouseButton(downFlag);
            Thread.Sleep(15);
            SendMouseButton(upFlag);
            Thread.Sleep(40);
            SendMouseButton(downFlag);
            Thread.Sleep(15);
            SendMouseButton(upFlag);
            return;
        }

        if (action == MouseAction.DOWN)
        {
            SendMouseButton(downFlag);
        }
        else if (action == MouseAction.UP)
        {
            SendMouseButton(upFlag);
        }
        else // CLICK
        {
            SendMouseButton(downFlag);
            Thread.Sleep(20); // Vital 20ms hold so Windows and apps detect the click reliably
            SendMouseButton(upFlag);
        }
    }

    public void Scroll(float dx, float dy)
    {
        float multiplier = _naturalScrolling ? -1.0f : 1.0f;
        int wheelDeltaY = (int)(dy * 120.0f * multiplier);
        int wheelDeltaX = (int)(dx * 120.0f);

        var inputs = new List<NativeMethods.INPUT>();

        if (wheelDeltaY != 0)
        {
            inputs.Add(new NativeMethods.INPUT
            {
                type = NativeMethods.INPUT_MOUSE,
                u = new NativeMethods.INPUT_UNION
                {
                    mi = new NativeMethods.MOUSEINPUT
                    {
                        dwFlags = NativeMethods.MOUSEEVENTF_WHEEL,
                        mouseData = (uint)wheelDeltaY
                    }
                }
            });
        }

        if (wheelDeltaX != 0)
        {
            inputs.Add(new NativeMethods.INPUT
            {
                type = NativeMethods.INPUT_MOUSE,
                u = new NativeMethods.INPUT_UNION
                {
                    mi = new NativeMethods.MOUSEINPUT
                    {
                        dwFlags = NativeMethods.MOUSEEVENTF_HWHEEL,
                        mouseData = (uint)wheelDeltaX
                    }
                }
            });
        }

        if (inputs.Count > 0)
        {
            NativeMethods.SendInput((uint)inputs.Count, inputs.ToArray(), Marshal.SizeOf<NativeMethods.INPUT>());
        }
    }

    public void KeyDown(ushort virtualKey)
    {
        if (virtualKey == 0) return;
        lock (_keyLock)
        {
            if (_pressedKeys.Add(virtualKey))
            {
                SendKey(virtualKey, 0);
            }
        }
    }

    public void KeyUp(ushort virtualKey)
    {
        if (virtualKey == 0) return;
        lock (_keyLock)
        {
            _pressedKeys.Remove(virtualKey);
            SendKey(virtualKey, NativeMethods.KEYEVENTF_KEYUP);
        }
    }

    public void KeyPress(ushort virtualKey)
    {
        if (virtualKey == 0) return;
        KeyDown(virtualKey);
        Thread.Sleep(15);
        KeyUp(virtualKey);
    }

    public void ReleaseAllKeys()
    {
        lock (_keyLock)
        {
            foreach (var vk in _pressedKeys)
            {
                SendKey(vk, NativeMethods.KEYEVENTF_KEYUP);
            }
            _pressedKeys.Clear();
        }
    }

    public void KeyStroke(ushort virtualKey, KeyAction action)
    {
        if (action == KeyAction.DOWN)
        {
            KeyDown(virtualKey);
        }
        else if (action == KeyAction.UP)
        {
            KeyUp(virtualKey);
        }
        else // PRESS
        {
            KeyPress(virtualKey);
        }
    }

    public void TypeText(string text)
    {
        if (string.IsNullOrEmpty(text)) return;
        foreach (char c in text)
        {
            TypeUnicodeChar(c);
        }
    }

    public void TypeUnicodeChar(char c)
    {
        var inputDown = new NativeMethods.INPUT
        {
            type = NativeMethods.INPUT_KEYBOARD,
            u = new NativeMethods.INPUT_UNION
            {
                ki = new NativeMethods.KEYBDINPUT
                {
                    wScan = c,
                    dwFlags = NativeMethods.KEYEVENTF_UNICODE
                }
            }
        };

        var inputUp = new NativeMethods.INPUT
        {
            type = NativeMethods.INPUT_KEYBOARD,
            u = new NativeMethods.INPUT_UNION
            {
                ki = new NativeMethods.KEYBDINPUT
                {
                    wScan = c,
                    dwFlags = NativeMethods.KEYEVENTF_UNICODE | NativeMethods.KEYEVENTF_KEYUP
                }
            }
        };

        NativeMethods.SendInput(2, new[] { inputDown, inputUp }, Marshal.SizeOf<NativeMethods.INPUT>());
    }

    public void MediaControl(MediaAction action)
    {
        ushort vk = action switch
        {
            MediaAction.PLAY_PAUSE => NativeMethods.VK_MEDIA_PLAY_PAUSE,
            MediaAction.NEXT => NativeMethods.VK_MEDIA_NEXT_TRACK,
            MediaAction.PREV => NativeMethods.VK_MEDIA_PREV_TRACK,
            MediaAction.STOP => NativeMethods.VK_MEDIA_STOP,
            MediaAction.VOL_UP => NativeMethods.VK_VOLUME_UP,
            MediaAction.VOL_DOWN => NativeMethods.VK_VOLUME_DOWN,
            MediaAction.MUTE => NativeMethods.VK_VOLUME_MUTE,
            _ => 0
        };

        if (vk != 0)
        {
            KeyStroke(vk, KeyAction.PRESS);
        }
    }

    public void PresentationControl(PresentationAction action)
    {
        switch (action)
        {
            case PresentationAction.NEXT_SLIDE:
                KeyStroke(NativeMethods.VK_NEXT, KeyAction.PRESS); // Page Down
                break;
            case PresentationAction.PREV_SLIDE:
                KeyStroke(NativeMethods.VK_PRIOR, KeyAction.PRESS); // Page Up
                break;
            case PresentationAction.START:
                KeyStroke(NativeMethods.VK_F5, KeyAction.PRESS); // F5
                break;
            case PresentationAction.END:
                KeyStroke(NativeMethods.VK_ESCAPE, KeyAction.PRESS); // Esc
                break;
            case PresentationAction.BLACK_SCREEN:
                TypeUnicodeChar('B'); // PowerPoint 'B' key for black screen
                break;
            case PresentationAction.WHITE_SCREEN:
                TypeUnicodeChar('W'); // PowerPoint 'W' key for white screen
                break;
        }
    }

    private void SendMouseButton(uint flags)
    {
        var input = new NativeMethods.INPUT
        {
            type = NativeMethods.INPUT_MOUSE,
            u = new NativeMethods.INPUT_UNION
            {
                mi = new NativeMethods.MOUSEINPUT
                {
                    dwFlags = flags
                }
            }
        };

        NativeMethods.SendInput(1, new[] { input }, Marshal.SizeOf<NativeMethods.INPUT>());
    }

    private void SendKey(ushort vk, uint flags)
    {
        var input = new NativeMethods.INPUT
        {
            type = NativeMethods.INPUT_KEYBOARD,
            u = new NativeMethods.INPUT_UNION
            {
                ki = new NativeMethods.KEYBDINPUT
                {
                    wVk = vk,
                    dwFlags = flags
                }
            }
        };

        NativeMethods.SendInput(1, new[] { input }, Marshal.SizeOf<NativeMethods.INPUT>());
    }
}
