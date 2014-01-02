package com.pplive.liveplatform.core.service.live;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.core.exception.LiveHttpException;
import com.pplive.liveplatform.core.service.live.resp.MessageResp;
import com.pplive.liveplatform.util.URL;
import com.pplive.liveplatform.util.URL.Protocol;

public class FileUploadService extends RestService {

    private static final String TAG = FileUploadService.class.getSimpleName();

    private static final String TEMPLATE_UPLOAD_FILE = new URL(Protocol.HTTP, Constants.GROCERY_API_HOST, "/upload_file.php?app=lpic&tk={token}")
            .toString();

    private static final FileUploadService sInstance = new FileUploadService();

    public static FileUploadService getInstance() {
        return sInstance;
    }

    private FileUploadService() {

    }
    
    public String uploadFile(String coToken, String username, String path) throws LiveHttpException {
        Log.d(TAG, "coToken: " + coToken + "; username: " + username);
        
        String token = TokenService.getInstance().getPicUploadToken(coToken, username);
        
        return uploadFileByPicUploadToken(token, path);
    }

    private String uploadFileByPicUploadToken(String token, String path) throws LiveHttpException {
        Log.d(TAG, "token: " + token);
        
        RestTemplate template = new RestTemplate();
        
        template.getMessageConverters().add(new FormHttpMessageConverter());
        template.getMessageConverters().add(new GsonHttpMessageConverter());
        
        MultiValueMap<String, Object> forms = new LinkedMultiValueMap<String, Object>();
        
        forms.add("key", "34234");
        forms.add("upload", new FileSystemResource(path));
        forms.add("tk", token);
        
        MessageResp resp = null;
        try {
            resp = template.postForObject(TEMPLATE_UPLOAD_FILE, forms, MessageResp.class, token);
            
            if (0 != resp.getError()) {
                return resp.getData();
            }
        } catch (Exception e ) {
            Log.w(TAG, e.toString());
        }
        
        if (null != resp) {
            throw new LiveHttpException(resp.getError());
        } else {
            throw new LiveHttpException();
        }
        
    }
}
