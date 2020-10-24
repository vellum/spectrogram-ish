# spectrogram-ish

uses [Processing](https://processing.org) to visualize sound, perhaps for live performance. As sound is picked up on your computer's microphone, this sketch produces two rows of [spectrogram](https://en.wikipedia.org/wiki/Spectrogram)-like marks before the slate is cleared to start again. You can think of this as a paint brush that uses different bands of the audio spectrum as hairs or bristles.

![Screen capture](./screencapture.png)


# Installation

There's a folder in this repo called `spectrogram`. It contains the code used to make the visualization above.

1. Copy `spectrogram` into your Processing sketches folder. On a mac, this is typically `~/Documents/Processing`
2. Open `spectrogram` in Processing.
3. Now make some sounds (like talking, yelling, singing) and see things appear and change on screen.

# Notes
- This is tuned for input from a MacBookPro microphone. 
- This visualization likes a noisy mic.
- You may need to tinker with the code to get results you like. If I were using this with a quiet mic like a SM7B, I'd probably amplify the found signal by multiplying the value of `spectrum[i]` by some constant.

Thanks for your kind attention!

