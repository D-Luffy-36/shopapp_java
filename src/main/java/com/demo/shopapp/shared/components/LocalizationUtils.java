package com.demo.shopapp.shared.components;

import com.demo.shopapp.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;


@Component
@RequiredArgsConstructor
public class LocalizationUtils {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public String getLocalizationMessage(String messageKey,
                                         Object ... params) {

        HttpServletRequest request = WebUtils.getRequest();
        return messageSource.getMessage(
                messageKey,
                params,
                localeResolver.resolveLocale(request));
    }
}
