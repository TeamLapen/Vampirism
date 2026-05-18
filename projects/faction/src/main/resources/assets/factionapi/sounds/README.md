# Sounds

## Format

Make sure to not only use the correct file format (`.ogg`) but also the correct sampling rate and channel count.  
The sample rate should be `44100 Hz`.  
Any positional audio **must be** `Mono`, ambient sounds may be `Stereo` but unless really necessary convert them to
`Mono` as well.

You can down-mix and resample audio files with the included script `scripts/convert_audio.sh` or use e.g. *Audacity*
or https://github.com/crabsatellite/MineTrack

## License

If adding sound files, make sure we have a license to use them (ideally *Creative Commons*).
Add them to the respective directory folder and add an **attribution** statement **both** to the `cc/LICENSE.txt` file
and main `README.md`.