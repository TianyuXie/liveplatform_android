package com.pplive.liveplatform.core.api.live;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.api.RestTemplateFactory;
import com.pplive.liveplatform.core.api.exception.LiveHttpException;
import com.pplive.liveplatform.core.api.live.resp.MessageResp;
import com.pplive.liveplatform.util.URL;
import com.pplive.liveplatform.util.URL.Protocol;

public class FileUploadAPI extends RESTfulAPI {

    private static final String TAG = FileUploadAPI.class.getSimpleName();

    private static final String TEMPLATE_UPLOAD_FILE = new URL(Protocol.HTTP, Constants.GROCERY_API_HOST, "/upload_file.php?app=lpic&tk={token}").toString();

    private static final FileUploadAPI sInstance = new FileUploadAPI();

    public static FileUploadAPI getInstance() {
        return sInstance;
    }

    private RestTemplate mRestTemplate;

    private FileUploadAPI() {
        mRestTemplate = RestTemplateFactory.newInstance();
        mRestTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        mRestTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
    }

    public String uploadFile(String coToken, String username, String path) throws LiveHttpException {
        Log.d(TAG, "coToken: " + coToken + "; username: " + username);

        String token = TokenAPI.getInstance().getPicUploadToken(coToken, username);

        return uploadFileByPicUploadToken(token, new File(path));
    }

    private String uploadFileByPicUploadToken(String token, File file) throws LiveHttpException {
        Log.d(TAG, "token: " + token);

        MultiValueMap<String, Object> forms = new LinkedMultiValueMap<String, Object>();

        forms.add("key", "34234");
        forms.add("upload", new FileSystemResource(file));
        forms.add("tk", token);

        MessageResp resp = null;
        try {
            resp = mRestTemplate.postForObject(TEMPLATE_UPLOAD_FILE, forms, MessageResp.class, token);

            if (0 == resp.getError()) {
                return resp.getData();
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }

    }
}
