package com.microsoft.projectoxforddemo.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxforddemo.R;
import com.microsoft.projectoxforddemo.utils.FaceUtils;
import com.microsoft.projectoxforddemo.utils.ImageUtils;

import java.io.IOException;
import java.util.List;
/**
 * Created by admin on 7/2/2015.
 */
public class FaceDemoFragment extends BaseFragment implements SubFragment
{
    private final String TAG="FaceDemoFragment";
    private Container m_container = null;
    private SurfaceView m_surf = null;
    private Callback m_callback = null;
    private Camera m_camera = null;
    private FloatingActionsMenu m_fabMenu = null;
    private FloatingActionButton m_fabSetting = null;
    private FloatingActionButton m_fabCapture = null;
    private FloatingActionButton m_fabIdentification = null;
    private FaceVisualizationView m_faceView;
    private Rect m_cameraPreviewBound;
    //Index for the cameras in the devices.0 for back-camera and 1 for front-camera;
    private int m_camera_index=1;
    private boolean m_cameraStatus=false;
    public void setContainer(Container c) {
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
        m_faceView.setType(FACE_VIEW_HOME_IMAGE);
        /*
        initCamera();
        startPreview();
        m_cameraStatus = true;
        */
    }

    @Override
    void loadComponents() {
        RelativeLayout layout=(RelativeLayout)getView().findViewById(R.id.fragment_face_demo_layout);
        m_surf = (SurfaceView) getView().findViewById(R.id.fragment_face_surfaceView);
        m_cameraPreviewBound=new Rect(m_surf.getLeft(),m_surf.getTop(),m_surf.getRight(),m_surf.getBottom());
        m_fabMenu = (FloatingActionsMenu) getView().findViewById(R.id.fragment_face_fab_menu);
        m_fabMenu.setHorizontalScrollBarEnabled(true);
        m_fabSetting = (FloatingActionButton) getView().findViewById(R.id.fragment_face_fab_setting);
        m_fabCapture = (FloatingActionButton) getView().findViewById(R.id.fragment_face_capture);
        m_fabIdentification = (FloatingActionButton) getView().findViewById(R.id.fragment_face_fab_identification);

        //make sure the floating action button is in front of other views
        layout.removeView(m_fabMenu);
        m_faceView=new FaceVisualizationView(getActivity().getApplicationContext());
        layout.addView(m_faceView);
        layout.addView(m_fabMenu);
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
                Toast.makeText(getActivity().getApplicationContext(),"ReadyForDetectFaces",Toast.LENGTH_LONG).show();
                m_camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bmp = ImageUtils.rotateBitmap(ImageUtils.getBitmap(data), -90);
                        ImageUtils.save(bmp, "capture");
                        Face[] faces = null;
                        faces=FaceUtils.detectFace(data);
                        if (faces!=null&&faces.length > 0) {
                            Toast.makeText(getActivity().getApplicationContext(), "FaceDetected.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, Integer.toString(faces.length)+" faces found.");
                        }
                        else {
                            Toast.makeText(getActivity().getApplicationContext(), "Error in detecting face.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "0 faces found");
                        }
                    }
                });
                startPreview();
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
        if (m_camera != null) {
            if(m_cameraStatus) {
                m_camera.stopPreview();
                m_faceView.setType(FACE_VIEW_HOME_IMAGE);
            }
            else {
                m_faceView.setType(FACE_VIEW_CLEAR);
                m_camera.startPreview();
            }
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
        m_camera = Camera.open(m_camera_index);
        m_camera.setDisplayOrientation(90);
        Camera.Parameters para=m_camera.getParameters();
        if(m_camera.getParameters().getSupportedPictureFormats().contains(ImageFormat.JPEG)) {
            Log.d(TAG, "save the image format to " + ImageFormat.JPEG);
            para.setPictureFormat(ImageFormat.JPEG);
        }
        else
            Log.d(TAG,"JPEG not supported.");
        para.setPreviewFormat(ImageFormat.YUY2);
    }
    void startPreview() {
        try {
            m_camera.setPreviewDisplay(m_surf.getHolder());
            m_camera.startPreview();
            m_faceView.setType(FACE_VIEW_CLEAR);
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
        m_faceView.setType(FACE_VIEW_HOME_IMAGE);
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
            Log.d(TAG, "camera view updating...");
        }
            @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder){

        }
    }
    //enum FaceViewSrc {FACE_VIEW_FACE,FACE_VIEW_HOME_IMAGE,FACE_VIEW_COUNTER};
    public static  final int FACE_VIEW_CLEAR=0;
    public static  final int FACE_VIEW_FACE=1;
    public static final int FACE_VIEW_HOME_IMAGE=2;
    public static final int FACE_VIEW_CONTOUR =4;
    class FaceVisualizationView extends View
    {
        int m_src= FACE_VIEW_CONTOUR;
        public FaceVisualizationView(Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Log.d(TAG, "refresh...");
            switch(m_src)
            {
                case FACE_VIEW_CLEAR:
                    ImageUtils.drawClear(canvas);
                    break;
                case FACE_VIEW_FACE:
                    break;
                case FACE_VIEW_CONTOUR:
                    ImageUtils.drawContour(canvas);
                    break;
                case FACE_VIEW_HOME_IMAGE:
                    ImageUtils.drawHomeImage(canvas, getActivity().getResources().getDrawable(R.drawable.faceapi_main),
                            (int) getActivity().getResources().getDimension(R.dimen.face_camera_preview_margin_left),
                            (int) getActivity().getResources().getDimension(R.dimen.face_camera_preview_margin_top));
                    break;
            }
        }
        public void setType(int type) {
            m_src=type;
            invalidate();
        }
    }
}
