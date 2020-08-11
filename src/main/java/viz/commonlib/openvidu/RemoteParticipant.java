package viz.commonlib.openvidu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.webrtc.EglBase;
import org.webrtc.SurfaceViewRenderer;

public class RemoteParticipant extends Participant {

    private View view;
    private SurfaceViewRenderer videoView;
    private TextView participantNameText;
    @SuppressLint("ResourceType")
    @IdRes
    int remoteViewId = 10000;

    public RemoteParticipant(String connectionId, String participantName, Session session) {
        super(connectionId, participantName, session);
        this.session.addRemoteParticipant(this);
    }

    public View getView() {
        return this.view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public SurfaceViewRenderer getVideoView() {
        return this.videoView;
    }

    public void setVideoView(SurfaceViewRenderer videoView) {
        this.videoView = videoView;
    }

    public TextView getParticipantNameText() {
        return this.participantNameText;
    }

    public void setParticipantNameText(TextView participantNameText) {
        this.participantNameText = participantNameText;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void createRemoteParticipantVideo(Activity activity, ConstraintLayout views_container) {
        activity.runOnUiThread(() -> {
            Handler mainHandler = new Handler(activity.getMainLooper());
            Runnable myRunnable = () -> {
                View rowView = activity.getLayoutInflater().inflate(R.layout.peer_video, null);
                rowView.setId(remoteViewId);
                views_container.addView(rowView, 0);
                toggleLayout(true, remoteViewId, activity, views_container);
                SurfaceViewRenderer videoView = (SurfaceViewRenderer) ((ViewGroup) rowView).getChildAt(0);
                setVideoView(videoView);
                videoView.setMirror(false);
                EglBase rootEglBase = EglBase.create();
                videoView.init(rootEglBase.getEglBaseContext(), null);
                View textView = ((ViewGroup) rowView).getChildAt(1);
                setParticipantNameText((TextView) textView);
                setView(rowView);
                getParticipantNameText().setText(getParticipantName());
                getParticipantNameText().setPadding(20, 3, 20, 3);
            };
            mainHandler.post(myRunnable);
        });
    }
}
