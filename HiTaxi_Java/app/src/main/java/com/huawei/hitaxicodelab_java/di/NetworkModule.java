/*
**********************************************************************************
|                                                                                |
| Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.             |
|                                                                                |
| Licensed under the Apache License, Version 2.0 (the "License");                |
| you may not use this file except in compliance with the License.               |
| You may obtain a copy of the License at                                        |
|                                                                                |
| http://www.apache.org/licenses/LICENSE-2.0                                     |
|                                                                                |
| Unless required by applicable law or agreed to in writing, software            |
| distributed under the License is distributed on an "AS IS" BASIS,              |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.       |
| See the License for the specific language governing permissions and            |
| limitations under the License.                                                 |
|                                                                                |
**********************************************************************************
*/

package com.huawei.hitaxicodelab_java.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.hitaxicodelab_java.BuildConfig;
import com.huawei.hitaxicodelab_java.data.remote.RemoteService;
import com.huawei.hitaxicodelab_java.utils.NetworkConstants;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(){

       return new OkHttpClient.Builder()
               .readTimeout(60, TimeUnit.SECONDS)
               .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor( chain -> {
                    HttpUrl url = chain.request().url().newBuilder()
                        .addQueryParameter("key", BuildConfig.API_KEY)
                            .build();
                    Request request = chain.request().newBuilder()
                            .header("Content-Type", "application/json")
                            .url(url)
                            .build();
                    return chain.proceed(request);
                })
               .retryOnConnectionFailure(true)
               .build();

    }

    @Provides
    @Singleton
    public Retrofit getRetrofit(OkHttpClient okHttpClient){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(NetworkConstants.MAP_BASE_URL)
                .build();
    }

    @Provides
    @Singleton
    public RemoteService getRemoteService(Retrofit retrofit){
        return retrofit.create(RemoteService.class);
    }
}
