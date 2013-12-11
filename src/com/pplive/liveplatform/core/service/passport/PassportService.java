package com.pplive.liveplatform.core.service.passport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.Random;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.util.Base64;
import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.service.BaseURL;
import com.pplive.liveplatform.core.service.URL;
import com.pplive.liveplatform.core.service.URL.Protocol;
import com.pplive.liveplatform.core.service.passport.model.LoginResult;
import com.pplive.liveplatform.core.service.passport.resp.LoginResultResp;
import com.pplive.liveplatform.util.ThreeDESUtil;

public class PassportService {

    private static final String TAG = PassportService.class.getSimpleName();

    private static final String TEMPLATE_PASSPORT_LOGIN = new BaseURL(Protocol.HTTPS, Constants.PASSPORT_API_HOST,
            "/v3/login/login.do?username={usr}&password={pwd}&format=json").toString();
    
    private static final String THIRDPARTY_PASSPORT_LOGIN = new URL(Protocol.HTTP, Constants.PASSPORT_API_HOST,
            "/v3/register/thirdparty_simple.do?infovalue={infovalue}&apptype={apptype}&index={index}&format=json").toString();

    private static final PassportService sInstance = new PassportService();

    public static PassportService getInstance() {
        return sInstance;
    }

    private HttpComponentsClientHttpRequestFactory mFactory;

    private PassportService() {
        
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
    
            SSLSocketFactory sslFactory = new SSLSocketFactory(trustStore) {
    
                @Override
                public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
                    return getSocketFactory().createSocket(socket, host, port, autoClose);
                }
            };
    
            sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    
            SchemeRegistry reg = new SchemeRegistry();
            reg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            reg.register(new Scheme("https", sslFactory, 443));
    
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
    
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, reg);
    
            HttpClient client = new DefaultHttpClient(ccm, params);
            mFactory = new HttpComponentsClientHttpRequestFactory();
            mFactory.setHttpClient(client);
        
        } catch (IOException e) {
            
        } catch (CertificateException e) {
            
        } catch (NoSuchAlgorithmException e) {
            
        } catch (KeyStoreException e) {
            
        } catch (KeyManagementException e) {
            
        } catch (UnrecoverableKeyException e) {
            
        }
    }

    public String login(String usr, String pwd) {
        Log.d(TAG, "user: " + usr + "; password: " + pwd);
        
        RestTemplate template = new RestTemplate(false);
        template.setRequestFactory(mFactory);
        template.getMessageConverters().add(new GsonHttpMessageConverter() {
            @Override
            public boolean canRead(Class<?> clazz, MediaType mediaType) {
                return true;
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<String>(headers);

        HttpEntity<LoginResultResp> rep = template.exchange(TEMPLATE_PASSPORT_LOGIN, HttpMethod.GET, entity, LoginResultResp.class, usr, pwd);

        rep.getBody().getResult().getToken();

        Log.d(TAG, "token: " + rep.getBody().getResult().getToken());

        return rep.getBody().getResult().getToken();
    }
    
    public LoginResult thirdpartyRegister(String id, String faceUrl, String nickName, String apptype) {
        
        RestTemplate template = new RestTemplate(false);
        template.setRequestFactory(mFactory);
        template.getMessageConverters().add(new GsonHttpMessageConverter() {
            @Override
            public boolean canRead(Class<?> clazz, MediaType mediaType) {
                return true;
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        
        String infovalue = null;
        Random random = new Random();
        int keyIndex = random.nextInt(10) + 1;
        try {
            infovalue = String.format("%s&%s&%s", URLEncoder.encode(id, "UTF-8"),
                    URLEncoder.encode(faceUrl, "UTF-8"), URLEncoder.encode(nickName, "UTF-8"));

            infovalue = URLEncoder.encode(ThreeDESUtil.Encode(infovalue, keyIndex), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String index = keyIndex < 10 ? ("0" + keyIndex) : keyIndex + "";

        HttpEntity<LoginResultResp> rep = template.exchange(THIRDPARTY_PASSPORT_LOGIN, HttpMethod.GET, entity, LoginResultResp.class, infovalue, apptype, index);
        Log.d(TAG, "token: " + rep.getBody().getResult().getToken());

        return rep.getBody().getResult();
    }
}
