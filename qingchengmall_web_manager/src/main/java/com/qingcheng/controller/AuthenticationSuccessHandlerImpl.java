package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import com.qingcheng.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    @Reference
    private WebUtil webUtil;
    @Reference
    private LoginLogService loginLogService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        LoginLog loginLog = new LoginLog();
        String remoteAddr = httpServletRequest.getRemoteAddr();
        loginLog.setIp(remoteAddr);
        String city = webUtil.getCityByIP(remoteAddr);
        loginLog.setLoginName(city);
        loginLog.setLoginTime(new Date());
        String agent = httpServletRequest.getHeader("user-agent");
        String browserName = webUtil.getBrowserName(agent);
        loginLog.setBrowserName(browserName);
        loginLogService.add(loginLog);
    }
}
