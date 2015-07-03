package com.microsoft.projectoxforddemo.fragment;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.projectoxforddemo.R;

import java.io.IOException;

/**
 * Created by admin on 7/2/2015.
 */
public class FaceDemoFragment extends BaseFragment implements SubFragment
{
    private Container m_container = null;
    private SurfaceView m_surf=null;
    private Callback m_callback=null;
    private Camera m_camera=null;
    void setContainer(Container c) {
        m_container = c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_callback=new CameraViewCallback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getActivity().getResources().getLayout(getLayoutId()), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadComponents();
        addCallback();
    }

    @Override
    void loadComponents() {
        m_surf=(SurfaceView)getView().findViewById(R.id.fragment_face_surfaceView);
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
    public void onPageChanged() {
        m_container.setToolbarIcon(R.drawable.ic_keyboard_voice_black_24dp);
    }
    void addCallback() {
        m_surf.getHolder().addCallback(m_callback);
    }
    void removeCallback() {
        m_surf.getHolder().removeCallback(m_callback);
    }
    class CameraViewCallback implements SurfaceHolder.Callback
    {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                m_camera.open();
                m_camera.setPreviewDisplay(surfaceHolder);
            }
            catch (NullPointerException e) {
                m_camera.release();
                m_camera=null;
            }
            catch (IOException e) {
                m_camera.release();
                m_camera=null;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            m_camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            m_camera.stopPreview();
            m_camera.release();
            m_camera=null;
        }
    }

}
