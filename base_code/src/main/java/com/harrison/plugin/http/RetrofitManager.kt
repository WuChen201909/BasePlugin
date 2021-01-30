package com.harrison.plugin.http


import com.harrison.plugin.http.convert.GsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 *
 *
添加请求头
public class HeaderInterceptor implements Interceptor {
private String TAG = "HeaderInterceptor_http";
@Override
public Response intercept(Chain chain) throws IOException {
Request.Builder builder = chain.request().newBuilder();
builder.addHeader("x-client", "3");
builder.addHeader("x-version", UIUtils.getVersionName(BaseApplication.getmContext()));
builder.addHeader("x-source", "1");
String uid = SPUtil.getString(BaseApplication.getmContext(), ConstantConfig.USER_ID);
if (!TextUtils.isEmpty(uid)) {
builder.addHeader("x-uid", uid);
}
return chain.proceed(builder.build());
}
}
 */
abstract class RetrofitManager<T> {

    private var apiService: T? = null

    /**
     * OKHttp 配置
     */
    public open fun configOkHttp(build: OkHttpClient.Builder) {
        build.connectTimeout(15, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(LogInterceptor())
    }

    /**
     * Retrofit 配置
     *  .baseUrl(DEFAULT_HOST)  配置默认主机
     */
    public open fun configRetrofit(builder: Retrofit.Builder) {
        builder
            .validateEagerly(true)
            .addConverterFactory(GsonConverterFactory.create())
    }

    abstract fun getAPIClass(): Class<T>;

    /**
     * 获取ApiInterface
     */
    open fun instance(): T {
        if (apiService == null) {
            //OKHttp
            val okHttpBuilder = OkHttpClient.Builder()
            configOkHttp(okHttpBuilder)

            //retrofit
            var builder = Retrofit.Builder()
            configRetrofit(builder)
            builder.client(okHttpBuilder.build())

            apiService = builder.build().create(getAPIClass())
        }
        return apiService!!
    }


}