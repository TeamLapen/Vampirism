package de.teamlapen.lib.util;

public interface ISoundReference {
    boolean isPlaying();

    void startPlaying();

    void stopPlaying();

    class Dummy implements ISoundReference {

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public void startPlaying() {

        }

        @Override
        public void stopPlaying() {

        }
    }
}
