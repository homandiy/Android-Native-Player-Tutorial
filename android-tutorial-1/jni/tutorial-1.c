#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <gst/gst.h>

#include <android/log.h>
#define TAG "MTAG"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG,  __VA_ARGS__)
#define LOGE(...) __android_log_print (ANDROID_LOG_ERROR, TAG,  __VA_ARGS__)

/*
 * Java Bindings
 */
static jstring
gst_native_get_gstreamer_info (JNIEnv * env, jobject thiz)
{
  char msg[128];
  char welcome[] = "Welcome to try \n";
  strcpy(msg, welcome);
  LOGI("%s", msg);

  char *version_utf8 = gst_version_string ();
  strcat(msg, version_utf8);
  LOGI("%s", msg);

  jstring *version_jstring = (*env)->NewStringUTF (env, msg);
  g_free (version_utf8);

  return version_jstring;
}

static JNINativeMethod native_methods[] = {
  {
    "nativeGetGStreamerInfo",
    "()Ljava/lang/String;",
    (void *) gst_native_get_gstreamer_info
  }
};

jint
JNI_OnLoad (JavaVM * vm, void *reserved)
{
  JNIEnv *env = NULL;

  if ((*vm)->GetEnv (vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
    LOGE("%s",  "Could not retrieve JNIEnv");
    return 0;
  }
  jclass klass = (*env)->FindClass (env,
      "org/freedesktop/gstreamer/tutorials/tutorial_1/Tutorial1");
  (*env)->RegisterNatives (env, klass, native_methods,
      G_N_ELEMENTS (native_methods));

  return JNI_VERSION_1_4;
}
