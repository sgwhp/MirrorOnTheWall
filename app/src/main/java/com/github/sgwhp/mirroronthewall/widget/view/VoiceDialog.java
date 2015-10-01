package com.github.sgwhp.mirroronthewall.widget.view;

import java.util.ArrayList;

import com.baidu.speech.VoiceRecognitionService;
import com.github.sgwhp.mirroronthewall.R;
import com.github.sgwhp.mirroronthewall.model.Constant;
import com.github.sgwhp.mirroronthewall.util.LogUtil;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.widget.TextView;

public class VoiceDialog extends Dialog implements RecognitionListener {
    private static final int EVENT_ERROR = 11;
    private static final int START_RECOGNIZING_DELAY = 2000;
    private TextView tvMsg;
    private String msg;
    private int msgId;
    private SpeechRecognizer speechRecognizer;
    private OnVoiceResultListener listener;
    private boolean nluEnable;
	
	public VoiceDialog(Context context){
		super(context);
	}

	public VoiceDialog(Context context, int theme) {
		super(context, theme);
	}
	
	protected VoiceDialog(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_voice_layout);
        tvMsg = (TextView) findViewById(R.id.msg);
        if(msgId != 0){
            tvMsg.setText(msgId);
        } else if(msg != null){
            tvMsg.setText(msg);
        }
        setTitle("提示");
        setCancelable(false);
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext()
        		, new ComponentName(getContext(), VoiceRecognitionService.class));

        speechRecognizer.setRecognitionListener(this);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	tvMsg.postDelayed(new Runnable() {

            @Override
            public void run() {
                start();
            }
        }, START_RECOGNIZING_DELAY);
    }

    public void destroy(){
        if(speechRecognizer != null){
            speechRecognizer.destroy();
        }
    }

    @Override
    public void show() {
        show(false);
    }

    public void show(boolean nluEnable){
        show(nluEnable, "");
    }

    public void show(boolean nluEnable, String msg){
        setMsg(msg);
        this.nluEnable = nluEnable;
        super.show();
    }
    
    public void setMsg(String msg){
        this.msg = msg;
        msgId = 0;
        if(tvMsg != null){
            tvMsg.setText(msg);
        }
    }

    public void setMsg(int resId){
        msgId = resId;
        msg = null;
        if(tvMsg != null){
            tvMsg.setText(resId);
        }
    }

	private void start(){
		Intent intent = new Intent();
		Context context = getContext();
		intent.putExtra(Constant.EXTRA_APP_ID, context.getString(R.string.appId));
		intent.putExtra(Constant.EXTRA_KEY, context.getString(R.string.appKey));
		intent.putExtra(Constant.EXTRA_SECRET, context.getString(R.string.secretKey));
		if(nluEnable){
            intent.putExtra(Constant.EXTRA_NLU, "enable");
        } else {
            intent.putExtra(Constant.EXTRA_NLU, "disable");
        }
		speechRecognizer.startListening(intent);
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		tvMsg.setText("可以开始说话了");
	}

	@Override
	public void onBeginningOfSpeech() {
		
	}

	@Override
	public void onRmsChanged(float rmsdB) {
		
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		
	}

	@Override
	public void onEndOfSpeech() {
		
	}

	@Override
	public void onError(int error) {
		StringBuilder sb = new StringBuilder("识别失败：");
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":").append(error);
        tvMsg.setText(sb.toString());
        listener.onFailed();
        dismiss();
	}

	@Override
	public void onResults(Bundle results) {
		ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest != null && nbest.size() > 0) {
            String result = nbest.get(0);
            tvMsg.setText(result);
        }
		if (listener != null) {
            listener.onResults(results);
		}
        dismiss();
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		if(nbest != null && nbest.size() > 0){
			tvMsg.setText(nbest.get(0));
		}
	}

	@Override
	public void onEvent(int eventType, Bundle params) {
		switch (eventType) {
		case EVENT_ERROR:
			String reason = params.get("reason") + "";
			tvMsg.setText("EVENT_ERROR, " + reason);
            LogUtil.d("EVENT_ERROR, " + reason);
			break;
		case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
//			int type = params.getInt("engine_type");
//			tvMsg.setText("*引擎切换至" + (type == 0 ? "在线" : "离线"));
			break;
		}
	}
	
	public void setOnVoiceResultListener(OnVoiceResultListener listener){
		this.listener = listener;
	}

	public interface OnVoiceResultListener {
		void onResults(Bundle results);

        void onFailed();
	}
}
