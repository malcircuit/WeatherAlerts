package net.theneophyte.weatheralerts.graphics;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class PixelBuffer {
	
	private static final String LOG_TAG = "PixelBuffer";
	
	private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
	private static final int GL_VERSION = 2;

	private final String mThreadName;
	private final int mWidth, mHeight;
	
	private final EGL10 mEgl;
	private final EGLDisplay mEglDisplay;
	private final EGLConfig mEglConfig;
	private final EGLContext mEglContext;
	private final EGLSurface mEglSurface;
	private final GL10 mGl;
	
	public PixelBuffer(int width, int height){
		mWidth = width;
		mHeight = height;
		
		mEgl = (EGL10) EGLContext.getEGL();
		
		mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
		if (mEglDisplay.equals(EGL10.EGL_NO_DISPLAY)){
			throw new RuntimeException("eglGetDisplay failed");
		}

		if (!mEgl.eglInitialize(mEglDisplay, null)){
			logEglError("EGL init failed");
			throw new RuntimeException("EGL init failed");
		}

		mEglConfig = chooseConfig();
		
		if (mEglConfig == null){
			Log.e(LOG_TAG, "Requested height and width exceed this platform's limits.");
			throw new RuntimeException("Pbuffer exceeds h x w limit");
		}

		mEglContext = createContext();
		
		if (mEglContext == null || mEglContext == EGL10.EGL_NO_CONTEXT){
			logEglError("EGL context creation failed");
			throw new RuntimeException("EGL init failed");
		}
		
		mEglSurface = createPbuffer();
		
		if (mEglSurface == EGL10.EGL_NO_SURFACE){
			logEglError("EGL Pbuffer creation failed");
			throw new RuntimeException("EGL pbuf init failed");
		}
		
		mGl = (GL10) mEglContext.getGL();
		
		mThreadName = Thread.currentThread().getName();
	}
	
	private EGLSurface createPbuffer(){
		final int[] pbuf_attr_list = {
				EGL10.EGL_WIDTH, mWidth,
				EGL10.EGL_HEIGHT, mHeight,
				EGL10.EGL_NONE
		};
		
		return mEgl.eglCreatePbufferSurface(mEglDisplay, mEglConfig, pbuf_attr_list);
	}
	
	private EGLConfig chooseConfig(){

		final int[] config_attr_list = {
				EGL10.EGL_DEPTH_SIZE, 0,
				EGL10.EGL_STENCIL_SIZE, 0,
				EGL10.EGL_RED_SIZE, 8,
				EGL10.EGL_GREEN_SIZE, 8,
				EGL10.EGL_BLUE_SIZE, 8,
				EGL10.EGL_ALPHA_SIZE, 8,
				EGL10.EGL_RENDERABLE_TYPE, 4,
				EGL10.EGL_NONE
		};
		
		final int[] num_configs = new int[1];
		
		if(!mEgl.eglChooseConfig(mEglDisplay, config_attr_list, null, 0, num_configs)){
			logEglError("No matching EGL configs");
			throw new RuntimeException("No EGL configs");
		}
		
		final int config_size = num_configs[0];
		final EGLConfig[] configs = new EGLConfig[config_size];
		
		if(!mEgl.eglChooseConfig(mEglDisplay, config_attr_list, configs, config_size, num_configs)){
			logEglError("No matching EGL configs");
			throw new RuntimeException("No EGL configs");
		}
		
		int max_h = 0;
		int max_w = 0;
		int[] value = new int[1];
		for (EGLConfig config : configs){
			if(mEgl.eglGetConfigAttrib(mEglDisplay, config, EGL10.EGL_MAX_PBUFFER_HEIGHT, value)){
				max_h = value[0];
			}
			if(mEgl.eglGetConfigAttrib(mEglDisplay, config, EGL10.EGL_MAX_PBUFFER_WIDTH, value)){
				max_w = value[0];
			}
			
			if ((max_h >= mHeight) && (max_w >= mWidth)){
				return config;
			}
		}
		
		return null;
	}
	
	private EGLContext createContext(){
		final int[] context_attr_list = {
				EGL_CONTEXT_CLIENT_VERSION,
				GL_VERSION,
				EGL10.EGL_NONE
		};

		return mEgl.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT, context_attr_list);
	}
	
	private boolean isGlThread(){
		if (Thread.currentThread().getName().equals(mThreadName)){
			return true;
		}
		return false;
	}
	
	private void logEglError(String message){
		Log.e(LOG_TAG, message + ", EGL error: " + getEglError(mEgl));
	}
	
	protected static String getEglError(EGL10 egl){
		final int error = egl.eglGetError();
		
		switch (error){
		case EGL10.EGL_SUCCESS:
			return "EGL_SUCCESS";
		case EGL10.EGL_NOT_INITIALIZED:
			return "EGL_NOT_INITIALIZED";
		case EGL10.EGL_BAD_ACCESS:
			return "EGL_BAD_ACCESS";
		case EGL10.EGL_BAD_ALLOC:
			return "EGL_BAD_ALLOC";
		case EGL10.EGL_BAD_ATTRIBUTE:
			return "EGL_BAD_ATTRIBUTE";
		case EGL10.EGL_BAD_CONFIG:
			return "EGL_BAD_CONFIG";
		case EGL10.EGL_BAD_CONTEXT:
			return "EGL_BAD_CONTEXT";
		case EGL10.EGL_BAD_CURRENT_SURFACE:
			return "EGL_BAD_CURRENT_SURFACE";
		case EGL10.EGL_BAD_DISPLAY:
			return "EGL_BAD_DISPLAY";
		case EGL10.EGL_BAD_MATCH:
			return "EGL_BAD_MATCH";
		case EGL10.EGL_BAD_NATIVE_PIXMAP:
			return "EGL_BAD_NATIVE_PIXMAP";
		case EGL10.EGL_BAD_NATIVE_WINDOW:
			return "EGL_BAD_NATIVE_WINDOW";
		case EGL10.EGL_BAD_PARAMETER:
			return "EGL_BAD_PARAMETER";
		case EGL10.EGL_BAD_SURFACE:
			return "EGL_BAD_SURFACE";
		case EGL11.EGL_CONTEXT_LOST:
			return "EGL_CONTEXT_LOST";
		default:
			return "Unknown error " + Integer.toString(error);
		}
	}
}
