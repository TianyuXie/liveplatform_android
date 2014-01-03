package com.pplive.liveplatform.update;

import java.util.ArrayList;
/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  javonewang
 * @version  [版本号, 2013-5-22]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public abstract class UpdateListener
{
    public abstract void onCompleted(ArrayList<UpdateInfo> updateInfos);

    public void onFailure()
    {
    }
}
