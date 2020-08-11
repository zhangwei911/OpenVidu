package viz.commonlib.openvidu;

import android.app.Activity;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import org.webrtc.AudioTrack;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public abstract class Participant {

    protected String connectionId;
    protected String participantName;
    protected Session session;
    protected List<IceCandidate> iceCandidateList = new ArrayList<>();
    protected PeerConnection peerConnection;
    protected AudioTrack audioTrack;
    protected VideoTrack videoTrack;
    protected MediaStream mediaStream;

    public Participant(String participantName, Session session) {
        this.participantName = participantName;
        this.session = session;
    }

    public Participant(String connectionId, String participantName, Session session) {
        this.connectionId = connectionId;
        this.participantName = participantName;
        this.session = session;
    }

    public String getConnectionId() {
        return this.connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getParticipantName() {
        return this.participantName;
    }

    public List<IceCandidate> getIceCandidateList() {
        return this.iceCandidateList;
    }

    public PeerConnection getPeerConnection() {
        return peerConnection;
    }

    public void setPeerConnection(PeerConnection peerConnection) {
        this.peerConnection = peerConnection;
    }

    public AudioTrack getAudioTrack() {
        return this.audioTrack;
    }

    public void setAudioTrack(AudioTrack audioTrack) {
        this.audioTrack = audioTrack;
    }

    public VideoTrack getVideoTrack() {
        return this.videoTrack;
    }

    public void setVideoTrack(VideoTrack videoTrack) {
        this.videoTrack = videoTrack;
    }

    public MediaStream getMediaStream() {
        return this.mediaStream;
    }

    public void setMediaStream(MediaStream mediaStream) {
        this.mediaStream = mediaStream;
    }

    public void dispose() {
        if (this.peerConnection != null) {
            try {
                this.peerConnection.close();
            } catch (IllegalStateException e) {
                Log.e("Dispose PeerConnection", e.getMessage());
            }
        }
    }

    /**
     * 切换大小布局
     * @param bigIndex        大布局索引
     * @param viewIdList      视图list
     * @param activity        活动
     * @param views_container 视图容器
     */
    public void toggleLayout(int bigIndex, ArrayList<Integer> viewIdList, Activity activity, ConstraintLayout views_container) {
        int size = viewIdList.size();
        for (int i = 0; i < size; i++) {
            boolean isBig = i == bigIndex;
            int viewId = viewIdList.get(i);
            View rowView = views_container.getViewById(viewId);
            SurfaceViewRenderer videoView = (SurfaceViewRenderer) ((ViewGroup) rowView).getChildAt(0);
            videoView.setZOrderMediaOverlay(!isBig);
            toggleLayout(i == bigIndex, viewIdList.get(i), activity, views_container);
        }
    }

    /**
     * 生成大/小布局
     * @param isBig           是否大布局
     * @param viewId          视图id
     * @param activity        活动
     * @param views_container 视图容器
     */
    public void toggleLayout(Boolean isBig, int viewId, Activity activity, ConstraintLayout views_container) {
        activity.runOnUiThread(() -> {
            Handler mainHandler = new Handler(activity.getMainLooper());
            Runnable myRunnable = () -> {
                ConstraintSet set = new ConstraintSet();
                set.clone(views_container);
                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                set.clear(viewId);
                if (isBig) {
                    set.constrainWidth(viewId, dm.widthPixels);
                    set.constrainHeight(viewId, dm.heightPixels);
                    set.connect(
                            viewId, ConstraintSet.TOP, views_container.getId(), ConstraintSet.TOP
                    );
                    set.constrainHeight(viewId, dm.heightPixels);
                    set.connect(
                            viewId, ConstraintSet.END, views_container.getId(), ConstraintSet.END
                    );
                    set.connect(
                            viewId, ConstraintSet.START, views_container.getId(), ConstraintSet.START
                    );
                    set.connect(
                            viewId, ConstraintSet.BOTTOM, views_container.getId(), ConstraintSet.BOTTOM
                    );
                } else {
                    set.constrainWidth(viewId, (int) (dm.widthPixels * 0.3));
                    set.constrainHeight(viewId, (int) (dm.widthPixels * 0.4));
                    set.connect(
                            viewId, ConstraintSet.TOP, views_container.getId(), ConstraintSet.TOP, 10
                    );
                    set.connect(
                            viewId, ConstraintSet.END, views_container.getId(), ConstraintSet.END, 10
                    );
                }
                set.applyTo(views_container);
            };
            mainHandler.post(myRunnable);
        });
    }
}
