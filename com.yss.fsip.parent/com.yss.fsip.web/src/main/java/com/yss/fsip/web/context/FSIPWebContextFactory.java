package com.yss.fsip.web.context;

import com.yss.fsip.context.FSIPContext;
import com.yss.fsip.context.FSIPContextFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Description: Context 创建工具类
 * @Author gumpLiu
 * @Date 2020-02-19
 * @Version V1.0
 **/
public abstract class FSIPWebContextFactory extends FSIPContextFactory {

    private ServletRequest request;

    @Override
    public final FSIPContext createContext() {
        return createContext(request);
    }

    /**
     * web 维护context
     *
     * @param request
     * @param context
     */
    public abstract void setContext(ServletRequest request, FSIPContext context);


    private final FSIPContext createContext(ServletRequest request){

        FSIPContext fsipContext = new FSIPContext();

        contexts.set(fsipContext);

        setContext(request, fsipContext);

        return fsipContext;
    }


    public void setRequest(ServletRequest request) {
        this.request = request;
    }
}
