package com.grace.app.injection.module;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grace.app.BuildConfig;
import com.grace.app.GraceApplication;
import com.grace.app.constants.Constants;
import com.grace.app.model.MealResponse;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by varsovski on 16-May-17.
 */

@Module
public class GraceApiModule {

    public interface GraceInterface {

        @Multipart
        @POST(Constants.IS_THIS_MEAL)
        Call<MealResponse> recogniseMeal(@Part(Constants.SOURCE) RequestBody requestBodyPic,
                                         @Part(Constants.TYPE) RequestBody typeValue,
                                         @Part(Constants.SHOW_RAW) RequestBody showRawValue);
    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(GraceApplication application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(90, TimeUnit.SECONDS);
        httpClient.connectTimeout(90, TimeUnit.SECONDS);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        //httpClient.addInterceptor(new Base64EncodeRequestInterceptor());
        /*httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String credentials = BuildConfig.USERNAME + ":" + BuildConfig.PASSWORD;

                String string = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", string)
                        .header("Accept", "Application/JSON")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });*/
        return BuildConfig.DEBUG ? httpClient.addInterceptor(logging).cache(cache).build() : httpClient.cache(cache).build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton // needs to be consistent with the component scope
    public GraceInterface providesGraceInterface(Retrofit retrofit) {
        return retrofit.create(GraceInterface.class);
    }

}
