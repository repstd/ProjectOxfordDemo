package com.microsoft.projectoxforddemo.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.microsoft.projectoxforddemo.R;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by admin on 7/2/2015.
 */
public class FaceDemoFragment extends BaseFragment implements SubFragment{
    private final String TAG="FaceDemoFragment";
    private Container m_container = null;
    private SurfaceView m_surf = null;
    private Callback m_callback = null;
    private Camera m_camera = null;
    private FloatingActionsMenu m_fabMenu = null;
    private FloatingActionButton m_fabSetting = null;
    private FloatingActionButton m_fabCapture = null;
    private FloatingActionButton m_fabIdentification = null;
    private boolean m_cameraStatus=false;
    void setContainer(Container c) {
        m_container = c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_callback = new CameraViewCallback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getActivity().getResources().getLayout(getLayoutId()), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadComponents();
        m_surf.getHolder().addCallback(m_callback);
    }

    @Override
    void loadComponents() {
        m_surf = (SurfaceView) getView().findViewById(R.id.fragment_face_surfaceView);
        m_fabMenu = (FloatingActionsMenu) getView().findViewById(R.id.fragment_face_fab_menu);
        m_fabSetting = (FloatingActionButton) getView().findViewById(R.id.fragment_face_fab_setting);
        m_fabCapture = (FloatingActionButton) getView().findViewById(R.id.fragment_face_capture);
        m_fabIdentification = (FloatingActionButton) getView().findViewById(R.id.fragment_face_fab_identification);
        addListeners();
    }

    void addListeners() {
        m_fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_fabMenu.collapse();
            }
        });
        m_fabCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_fabMenu.collapse();
                if (!m_cameraStatus) {
                    initCamera();
                    startPreview();
                    m_cameraStatus = true;
                } else {
                    stopPreview();
                    closeCamera();
                    m_cameraStatus = false;
                }
            }
        });
        m_fabIdentification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_fabMenu.collapse();
            }
        });
    }

    @Override
    public String getToolbarTitle() {
        return "FaceDemo";
    }

    @Override
    int getLayoutId() {
        return R.layout.fragment_face_demo;
    }

    @Override
    public void onPageShifted() {
        m_container.setToolbarIcon(R.drawable.ic_perm_identity_black_24dp);
        m_container.setToolbarTitle(getToolbarTitle());
        if(m_camera!=null) {
            if(m_cameraStatus)
                m_camera.stopPreview();
            else
                m_camera.startPreview();
            m_cameraStatus=!m_cameraStatus;
        }
    }
    void queryCameraInfo() {
        if(m_camera==null)
            return;
        Camera.Parameters para=m_camera.getParameters();
        List<Integer> supportedPreviewFormats=para.getSupportedPreviewFormats();
        logList("Supported Preview Format:", supportedPreviewFormats);
        List<Integer> supportedPicutreFormats=para.getSupportedPictureFormats();
        logList("Supported Picture Format:", supportedPicutreFormats);
    }
    void logList (String desc,List<Integer> arr) {
        for(Integer i:arr) {
            Log.d(TAG,"#"+desc+" : "+Integer.toString(i));
        }
    }
    void initCamera() {
        m_camera = Camera.open(1);
        queryCameraInfo();
        //Camera.Parameters paras=m_camera.getParameters();
    }
    void startPreview() {
        try {
            m_camera.setPreviewDisplay(m_surf.getHolder());
            m_camera.startPreview();
            //new FaceVisualizationThread(m_surf.getHolder()).start();
        }
        catch (IOException e){

        }
    }
    void stopPreview() {
        if(m_camera==null) {
            Log.d(TAG,"trying to undertake stopPreview for a closed camera.");
            return;
        }
        m_camera.stopPreview();
    }
    void closeCamera() {
        if(m_camera==null) {
            Log.d(TAG,"trying to close a closed camera.");
            return;
        }
        m_camera.release();
        m_camera=null;
    }
    class CameraViewCallback implements SurfaceHolder.Callback
    {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.d(TAG, "surfaceChanged...");
        }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder){
        }

    }
    private void tryDrawing(SurfaceHolder holder) {
        Log.i(TAG, "Trying to draw...");

        Canvas canvas = holder.lockCanvas(null);
        if (canvas == null) {
            Log.e(TAG, "Cannot draw onto the canvas as it's null");
        } else {
            drawMyStuff(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }
    private void drawMyStuff(final Canvas canvas) {
        Random random = new Random();
        Log.i(TAG, "Drawing...");
        canvas.drawRGB(255, 128, 128);
    }
    void drawContour(Canvas canvas) {
        int canvasWidth=canvas.getWidth();
        int canvasHeight=canvas.getHeight();
        int rectWidth=canvasWidth/2;
        int rectHeight=canvasHeight/2;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        canvas.drawRect(canvasWidth / 2 - rectWidth / 2, canvasHeight / 2 - rectHeight / 2, rectWidth, rectHeight, paint);
    }
    class FaceVisualizationView extends View
    {
        public FaceVisualizationView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawContour(canvas);
        }
    }
}
