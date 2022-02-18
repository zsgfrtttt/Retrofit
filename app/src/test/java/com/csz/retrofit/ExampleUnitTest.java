package com.csz.retrofit;

import com.csz.retrofit.anno.Field;
import com.csz.retrofit.anno.GET;
import com.csz.retrofit.anno.POST;
import com.csz.retrofit.anno.Query;
import com.csz.retrofit.code.Retrofit;

import org.junit.Test;

import java.net.MalformedURLException;

import okhttp3.Call;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private String baseUrl = "https://test.looyu.vip/";
    private String realUrl = "shop/open-app/store/addressList";

    interface Host{
        @GET("shop/open-app/store/addressList")
        Call get(@Query("ip") String ip,@Query("key") String key);


        @POST("shop/open-app/store/addressList")
        Call post(@Field("ip") String ip, @Field("key") String key);
    }
    @Test
    public void test_Retrofit() throws Exception {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).build();
        Host host = retrofit.create(Host.class);

        {
            Call call = host.get("", "");
            Response response = call.execute();
            if (response != null && response.body() != null) {
                System.out.println("get :" + response.body().string());
            }
        }

        {
            Call call = host.post("", "");
            Response response = call.execute();
            if (response != null && response.body() != null) {
                System.out.println("post :" + response.body().string());
            }
        }
    }
}