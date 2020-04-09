package com.yss.fsip.config;

import com.yss.fsip.context.FSIPContext;
import com.yss.fsip.context.FSIPContextFactory;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class UserIDAuditorBean implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        FSIPContext ctx = FSIPContextFactory.getContext();
        if (ctx == null) {
            return null;
        }

        String userId = ctx.getUserId();
        if (userId!=null&&!"".equals(userId)) {
            return Optional.of(userId);
        } else {
            return null;
        }
    }
}
