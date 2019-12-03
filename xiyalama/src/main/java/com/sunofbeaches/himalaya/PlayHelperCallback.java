package com.sunofbeaches.himalaya;

public interface PlayHelperCallback {
    public void refreshPlayStatus(boolean isPlaying);
    public void refreshPlayTitle(String title);
    public void callbackHideHUD();
}
