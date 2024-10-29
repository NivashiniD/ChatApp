package com.tbi.chatapplication.ui.chat.model;

public class MusicPlaybackState {
    private String musicUri;
    private boolean isPlaying;
    public MusicPlaybackState() {
    }
    public MusicPlaybackState(String musicUri, boolean isPlaying) {
        this.musicUri = musicUri;
        this.isPlaying = isPlaying;
    }

    public String getMusicUri() {
        return musicUri;
    }

    public void setMusicUri(String musicUri) {
        this.musicUri = musicUri;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
