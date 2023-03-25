package com.example.pieona.apple.controller;

import com.example.pieona.apple.service.AppleService;
import com.example.pieona.apple.model.AppsResponse;
import com.example.pieona.apple.model.ServicesResponse;
import com.example.pieona.apple.model.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AppleController {

    private Logger logger = LoggerFactory.getLogger(AppleController.class);

    private final AppleService appleService;

    @GetMapping(value = "/apple")
    public String appleLoginPage(ModelMap model) {

        Map<String, String> metaInfo = appleService.getLoginMetaInfo();

        model.addAttribute("client_id", metaInfo.get("CLIENT_ID"));
        model.addAttribute("redirect_uri", metaInfo.get("REDIRECT_URI"));
        model.addAttribute("nonce", metaInfo.get("NONCE"));

        return "index";
    }

    @GetMapping(value = "/apple/login")
    public String appleLogin(ModelMap model) {

        Map<String, String> metaInfo = appleService.getLoginMetaInfo();

        model.addAttribute("client_id", metaInfo.get("CLIENT_ID"));
        model.addAttribute("redirect_uri", metaInfo.get("REDIRECT_URI"));
        model.addAttribute("nonce", metaInfo.get("NONCE"));
        model.addAttribute("response_type", "code id_token");
        model.addAttribute("scope", "name email");
        model.addAttribute("response_mode", "form_post");

        return "redirect:https://appleid.apple.com/auth/authorize";
    }

    @PostMapping(value = "/redirect")
    @ResponseBody
    public TokenResponse servicesRedirect(ServicesResponse serviceResponse) {

        if (serviceResponse == null) {
            return null;
        }

        String code = serviceResponse.getCode();
        String client_secret = appleService.getAppleClientSecret(serviceResponse.getId_token());

        logger.debug("================================");
        logger.debug("id_token ‣ " + serviceResponse.getId_token());
        logger.debug("payload ‣ " + appleService.getPayload(serviceResponse.getId_token()));
        logger.debug("client_secret ‣ " + client_secret);
        logger.debug("================================");

        return appleService.requestCodeValidations(client_secret, code, null);
    }

    @PostMapping(value = "/apple/refresh")
    @ResponseBody
    public TokenResponse refreshRedirect(@RequestParam String client_secret, @RequestParam String refresh_token) {
        return appleService.requestCodeValidations(client_secret, null, refresh_token);
    }

    @PostMapping(value = "/apps/to/endpoint")
    @ResponseBody
    public void appsToEndpoint(@RequestBody AppsResponse appsResponse) {
        logger.debug("[/path/to/endpoint] RequestBody ‣ " + appsResponse.getPayload());
    }

}

