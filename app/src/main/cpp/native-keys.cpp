#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_frontend_1mobileapptraffic_KeyProvider_getOpenCageApiKey(
        JNIEnv* env,
        jobject /* this */) {
    // API key của bạn
    std::string apiKey = "9080a832b1b447a5a3ab3a0751ea9469";
    return env->NewStringUTF(apiKey.c_str());
}

