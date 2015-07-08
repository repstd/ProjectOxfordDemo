package com.microsoft.projectoxforddemo.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.microsoft.ProjectOxford.SpeechRecognitionMode;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.FaceAttribute;
import com.microsoft.projectoxford.face.contract.GenderEnum;
import com.microsoft.projectoxforddemo.R;
import com.microsoft.projectoxforddemo.utils.FaceUtils;
import com.microsoft.projectoxforddemo.utils.FaceVerifying;
import com.microsoft.projectoxforddemo.utils.ImageUtils;
import com.microsoft.projectoxforddemo.utils.OxfordRecognitionManager;
import com.microsoft.projectoxforddemo.utils.PersonUtils;
import com.microsoft.projectoxforddemo.utils.SpeechRecognition;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by admin on 7/2/2015.
 */
public class FaceDemoFragment extends BaseFragment implements SubFragment {
    //enum FaceViewSrc {FACE_VIEW_FACE,FACE_VIEW_HOME_IMAGE,FACE_VIEW_COUNTER};
    public static final int FACE_VIEW_CLEAR = 0;
    public static final int FACE_VIEW_FACE = 1;
    public static final int FACE_VIEW_HOME_IMAGE = 2;
    public static final int FACE_VIEW_CONTOUR = 4;
    private final String TAG = "FaceDemoFragment";
    ProgressDialog m_progressDialog;
    AlertDialog waiting;
    private Container m_container = null;
    private SurfaceView m_surf = null;
    private Camera m_camera = null;
    private FloatingActionsMenu m_fabMenu = null;
    private FloatingActionButton m_fabSetting = null;
    private FloatingActionButton m_fabCapture = null;
    private FloatingActionButton m_fabReset = null;
    private FloatingActionButton m_fabDetection = null;
    private FaceVisualizationView m_faceView;
    private Rect m_cameraPreviewBound;
    private AlertDialog m_settingAlertDialog;
    private View m_settingAlertDialogView;
    private FaceServiceClient m_faceCli = null;
    private String m_finalSpeechResult = null;
    //Index for the cameras in the devices.0 for back-camera and 1 for front-camera;
    private int m_camera_index = ImageUtils.CAMERA_FRONT;
    private boolean m_cameraStatus = false;

    public void setContainer(Container c) {
        m_container = c;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_progressDialog = new ProgressDialog(getActivity().getApplicationContext());
        m_progressDialog.setTitle("FaceDemo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getActivity().getResources().getLayout(getLayoutId()), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadComponents();
        m_faceView.draw(FACE_VIEW_HOME_IMAGE);
        /*
        initCamera();
        startPreview();
        m_cameraStatus = true;
        */
    }

    @Override
    void loadComponents() {
        RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.fragment_face_demo_layout);
        m_surf = (SurfaceView) getView().findViewById(R.id.fragment_face_surfaceView);
        m_cameraPreviewBound = new Rect(m_surf.getLeft(), m_surf.getTop(), m_surf.getRight(), m_surf.getBottom());
        m_fabMenu = (FloatingActionsMenu) getView().findViewById(R.id.fragment_face_fab_menu);
        m_fabMenu.setHorizontalScrollBarEnabled(true);
        m_fabSetting = (FloatingActionButton) getView().findViewById(R.id.fragment_face_fab_setting);
        m_fabCapture = (FloatingActionButton) getView().findViewById(R.id.fragment_face_capture);
        m_fabReset = (FloatingActionButton) getView().findViewById(R.id.fragment_face_fab_reset);
        m_fabDetection = (FloatingActionButton) getView().findViewById(R.id.fragment_face_fab_detection);

        //make sure the floating action button is in front of other views
        layout.removeView(m_fabMenu);
        m_faceView = new FaceVisualizationView(getActivity().getApplicationContext());
        layout.addView(m_faceView);
        layout.addView(m_fabMenu);

        AlertDialog.Builder builder = new AlertDialog.Builder(FaceDemoFragment.this.getActivity());
        m_settingAlertDialogView = FaceDemoFragment.this.getActivity().getLayoutInflater().inflate(R.layout.fragment_face_demo_setting, null);
        builder.setView(m_settingAlertDialogView);
        m_settingAlertDialog = builder.create();
        m_settingAlertDialog.setTitle("Choose Camera Device");
        addListeners();
    }

    void addListeners() {
        m_settingAlertDialogView.findViewById(R.id.fragment_face_demo_setting_radio_button_front_camera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_settingAlertDialog.cancel();
                        m_camera_index = ImageUtils.CAMERA_FRONT;
                        if (m_cameraStatus)
                            restartCamera();
                        else {
                            initCamera();
                            startPreview();
                            m_cameraStatus = true;
                        }
                    }
                });

        m_settingAlertDialogView.findViewById(R.id.fragment_face_demo_setting_radio_button_back_camera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_settingAlertDialog.cancel();
                        m_camera_index = ImageUtils.CAMERA_BACK;
                        if (m_cameraStatus)
                            restartCamera();
                        else {
                            initCamera();
                            startPreview();
                            m_cameraStatus = true;
                        }
                    }
                });
        m_fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_fabMenu.collapse();
                m_settingAlertDialog.show();
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
        m_fabDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_fabMenu.collapse();
                if (!OxfordRecognitionManager.instance().isNetworkAvailable(getActivity()))
                    return;
                if (!m_cameraStatus) {
                    m_settingAlertDialog.show();
                    return;
                }
                Toast.makeText(getActivity().getApplicationContext(), "ReadyForFacesDetectionUsingMicrosoftAI", Toast.LENGTH_LONG).show();
                final AlertDialog waiting = newAlertDialog("requesting the server...", null, null, "cancel", null);
                m_camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {

                        FaceUtils.detectFace(data, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                int result = -1;
                                result = msg.getData().getInt("faces");
                                Log.d(TAG, "BackgroundFaceRecognitionTaskEnded.Result: " + Integer.toString(result));
                                if (result == 1) {
                                    onFaceDetected();
                                    Toast.makeText(FaceDemoFragment.this.getActivity().getApplicationContext(), "FaceFound", Toast.LENGTH_LONG).show();
                                } else
                                    Toast.makeText(FaceDemoFragment.this.getActivity().getApplicationContext(), "FaceNotFound", Toast.LENGTH_LONG).show();
                                waiting.cancel();
                            }
                        });
                    }
                });
                waiting.show();
            }
        });
        m_fabReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonUtils.removeData();
                m_fabMenu.collapse();
            }
        });
    }

    public AlertDialog newAlertDialog(String title,
                                      String positiveButtonText, DialogInterface.OnClickListener positiveButtonListener,
                                      String negativeButtonText, DialogInterface.OnClickListener negativeButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FaceDemoFragment.this.getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        if (positiveButtonText != null && !positiveButtonText.isEmpty())
            builder.setPositiveButton(positiveButtonText, positiveButtonListener);
        if (negativeButtonText != null && !negativeButtonText.isEmpty())
            builder.setPositiveButton(negativeButtonText, negativeButtonListener);
        builder.setMessage(title);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
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
            if (m_cameraStatus) {
                stopPreview();
                closeCamera();
                m_faceView.draw(FACE_VIEW_HOME_IMAGE);
            } else {
                m_faceView.draw(FACE_VIEW_CLEAR);
                initCamera();
                startPreview();
                m_camera.startPreview();
            }
            m_cameraStatus = !m_cameraStatus;
        }
        if (m_fabMenu.isExpanded())
            m_fabMenu.collapse();
    }

    void queryCameraInfo() {
        if (m_camera == null)
            return;
        Camera.Parameters para = m_camera.getParameters();
        List<Integer> supportedPreviewFormats = para.getSupportedPreviewFormats();
        logList("Supported Preview Format:", supportedPreviewFormats);
        List<Integer> supportedPicutreFormats = para.getSupportedPictureFormats();
        logList("Supported Picture Format:", supportedPicutreFormats);
    }

    void logList(String desc, List<Integer> arr) {
        for (Integer i : arr) {
            Log.d(TAG, "#" + desc + " : " + Integer.toString(i));
        }
    }

    void initCamera() {
        m_camera = Camera.open(m_camera_index);
        m_camera.setDisplayOrientation(90);
        ImageUtils.setCamera(m_camera_index);
        Camera.Parameters para = m_camera.getParameters();
        if (m_camera.getParameters().getSupportedPictureFormats().contains(ImageFormat.JPEG)) {
            Log.d(TAG, "save the image format to " + ImageFormat.JPEG);
            para.setPictureFormat(ImageFormat.JPEG);
        } else
            Log.d(TAG, "JPEG not supported.");
        para.setPreviewFormat(ImageFormat.YUY2);
    }

    void startPreview() {
        try {
            m_camera.setPreviewDisplay(m_surf.getHolder());
            m_camera.startPreview();
            m_faceView.draw(FACE_VIEW_CLEAR);
        } catch (IOException e) {

        }
    }

    void stopPreview() {
        if (m_camera == null) {
            Log.d(TAG, "trying to undertake stopPreview for a closed camera.");
            return;
        }
        m_camera.stopPreview();
        m_faceView.draw(FACE_VIEW_HOME_IMAGE);
    }

    void closeCamera() {
        if (m_camera == null) {
            Log.d(TAG, "trying to close a closed camera.");
            return;
        }
        m_camera.release();
        m_camera = null;
    }

    void restartCamera() {
        stopPreview();
        closeCamera();
        initCamera();
        startPreview();
    }

    void onFaceDetected() {
        m_faceView.draw(FACE_VIEW_FACE);
        SharedPreferences setting = getActivity().getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        if (setting.getBoolean("ShowingFaceAttr", false))
            showFaceAttr();
        else {
            if (PersonUtils.isNewPerson())
                promptForChoice();
            else
                verify();
        }
        //Just For test
    }

    void showFaceAttr() {
        FaceAttribute attr = FaceUtils.FACES.getResult()[0].attributes;
        if (attr == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(FaceDemoFragment.this.getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        final View layout = FaceDemoFragment.this.getActivity().getLayoutInflater().inflate(R.layout.fragment_face_demo_show_face_attributes, null);
        builder.setView(layout);
        builder.setTitle("FaceAttributes");
        //show name
        EditText nameEditText = (EditText) layout.findViewById(R.id.fragment_face_demo_face_attr_name);
        nameEditText.setText(PersonUtils.getUserName());

        //show age
        EditText ageEditText = (EditText) layout.findViewById(R.id.fragment_face_demo_face_attr_age);
        ageEditText.setText(Double.toString(attr.age));
        //show Gender;
        EditText genderEditText = (EditText) layout.findViewById(R.id.fragment_face_demo_face_attr_gender);
        if (attr.gender.equals(GenderEnum.male))
            genderEditText.setText("Male");
        else if (attr.gender.equals(GenderEnum.female))
            genderEditText.setText("Female");
        else
            genderEditText.setText("Unknown");
        final AlertDialog dialog = builder.create();
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (PersonUtils.isNewPerson())
                        promptForChoice();
                    else
                        verify();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }

    void promptForChoice() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(FaceDemoFragment.this.getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setTitle("Ready for saving your face?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences inputTypeDetail = getActivity().getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
                String type = inputTypeDetail.getString("InputType", "");
                if (type.equals("Speech"))
                    promptForInfoBySpeech();
                else
                    promptForInfoByText();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    void promptForInfoByText() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(FaceDemoFragment.this.getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        final View promptLayout = FaceDemoFragment.this.getActivity().getLayoutInflater().inflate(R.layout.fragment_face_demo_input_id, null);
        builder.setView(promptLayout);
        builder.setTitle("Input Person Info");
        final AlertDialog dialog = builder.create();
        builder.setPositiveButton("Ready", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String personId = ((EditText) (promptLayout.findViewById(R.id.fragment_face_demo_input_person_id))).getText().toString();
                    String groupId = ((EditText) (promptLayout.findViewById(R.id.fragment_face_demo_input_group_id))).getText().toString();
                    PersonUtils.newUser(personId, groupId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("cancel", null);
        builder.show();
        m_faceView.draw(FACE_VIEW_HOME_IMAGE);
    }

    void promptForInfoBySpeech() {
        if (!OxfordRecognitionManager.instance().isNetworkAvailable(getActivity()))
            return;
        //trying to using SpeechToText instead
        final AlertDialog.Builder builderSpeech = new AlertDialog.Builder(FaceDemoFragment.this.getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builderSpeech.setTitle("OxfordSpeech");
        builderSpeech.setMessage("Listening...");
        final AlertDialog dialog = builderSpeech.create();
        final SpeechRecognition speechThread = new SpeechRecognition(FaceDemoFragment.this.getActivity(), null, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //builderSpeech.setMessage(msg.toString());
                String partialResult = msg.getData().getString(SpeechRecognition.HandlerKeyPartialResult);
                String finalResult = msg.getData().getString(SpeechRecognition.HandlerKeyHighestConfidenceResult);
                m_finalSpeechResult = finalResult;
                if (partialResult != null) {
                    Log.d(TAG, partialResult);
                    Toast.makeText(FaceDemoFragment.this.getActivity().getApplicationContext(), "Partial: " + partialResult, Toast.LENGTH_SHORT).show();
                } else if (finalResult != null) {
                    Log.d(TAG, finalResult);
                    Toast.makeText(FaceDemoFragment.this.getActivity().getApplicationContext(), "Final:" + finalResult, Toast.LENGTH_LONG).show();
                }
            }
        }, SpeechRecognitionMode.ShortPhrase);
        builderSpeech.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { builderSpeech.setMessage("Closing...");
                speechThread.closeClient();
                //now add the user using recognized speech input
                if (m_finalSpeechResult != null && !m_finalSpeechResult.isEmpty()) {
                    PersonUtils.newUser(m_finalSpeechResult, "Speech");
                    Toast.makeText(FaceDemoFragment.this.getActivity(), "SpeechInputSaved:[personId=" + m_finalSpeechResult + "]", Toast.LENGTH_LONG).show();
                }
            }
        });
        builderSpeech.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builderSpeech.setMessage("Closing...");
                speechThread.closeClient();
            }
        });
        builderSpeech.show();
        speechThread.start();
    }

    void verify() {
        //save the current faces
        final UUID[] currentCapturedFaceId = FaceUtils.FACES.getFacesIds();
        Bitmap masterMap = PersonUtils.getMasterBitMap();
        waiting = newAlertDialog("verifying...", null, null, null, null);
        waiting.show();
        FaceUtils.detectFace(masterMap, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int result = msg.getData().getInt("faces");
                if (result == 1) {
                    UUID[] oldCapture = FaceUtils.FACES.getFacesIds();
                    waiting.setMessage("comparing...");
                    compare(currentCapturedFaceId[0], oldCapture[0]);
                }
            }
        });
    }

    void compare(UUID face1, UUID face2) {
        new FaceVerifying(face1, face2, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                boolean isIdentical = msg.getData().getBoolean("isIdentical");
                double confidence = msg.getData().getDouble("confidence");
                waiting.setCancelable(true);
                if (isIdentical) {
                    waiting.setMessage("Authorized." + "Confidence: " + Double.toString(confidence));
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory( Intent.CATEGORY_HOME );
                    startActivity(homeIntent);
                }
                else
                    waiting.setMessage("UnAuthorized." + "Confidence: " + Double.toString(confidence));
            }
        }).start();
    }

    class FaceVisualizationView extends View {
        int m_src = FACE_VIEW_CONTOUR;

        public FaceVisualizationView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Log.d(TAG, "Updating Canvas.[ " + Integer.toString(canvas.getWidth()) + " " + Integer.toString(canvas.getHeight()) + " ]");
            switch (m_src) {
                case FACE_VIEW_CLEAR:
                    ImageUtils.drawClear(canvas);
                    break;
                case FACE_VIEW_FACE:
                    ImageUtils.drawFaces(canvas, FaceUtils.FACES.getResult(),
                            (int) getActivity().getResources().getDimension(R.dimen.face_camera_preview_margin_left),
                            (int) getActivity().getResources().getDimension(R.dimen.face_camera_preview_margin_top));
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

        public void draw(int type) {
            m_src = type;
            invalidate();
        }
    }
}
