package com.example.ShopApp_BE.Service.Impl;

import com.example.ShopApp_BE.Model.Entity.CartEntity;
import com.example.ShopApp_BE.Model.Entity.RoleEntity;
import com.example.ShopApp_BE.Model.Entity.TokenEntity;
import com.example.ShopApp_BE.Model.Entity.UserEntity;
import com.example.ShopApp_BE.Model.Response.TokenResponse;
import com.example.ShopApp_BE.Repository.RoleRepository;
import com.example.ShopApp_BE.Repository.TokenRepository;
import com.example.ShopApp_BE.Repository.UserRepository;
import com.example.ShopApp_BE.Service.OAuth2Service;
import com.example.ShopApp_BE.Utils.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2ServiceImpl implements OAuth2Service {
    private final GoogleProperties googleProperties;
    private final FacebookProperties facebookProperties;
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;

    @Override
    public TokenResponse getUserInfoFromSocial(String code, OAuth2Provider provider) {
        String requestBody;
        if(provider == OAuth2Provider.GOOGLE){
            requestBody = "code=" + code
                    + "&client_id=" + googleProperties.getClientId()
                    + "&client_secret=" + googleProperties.getClientSecret()
                    + "&redirect_uri=" + googleProperties.getRedirectUri()
                    + "&grant_type=authorization_code";
        }
        else{
            requestBody = "code=" + code
                    + "&client_id=" + facebookProperties.getClientId()
                    + "&client_secret=" + facebookProperties.getClientSecret()
                    + "&redirect_uri=" + facebookProperties.getRedirectUri()
                    + "&grant_type=authorization_code";
        }
        Map userMap = getUser(requestBody, provider);

        UserEntity userEntity;
        if(!userRepository.existsByEmail(userMap.get("email").toString())){
            if(provider == OAuth2Provider.GOOGLE){
                userEntity = UserEntity.builder()
                        .email(userMap.get("email").toString())
                        .fullName(userMap.get("family_name").toString() + " " + userMap.get("given_name"))
                        .isActive(Boolean.TRUE)
                        .avatarUrl(userMap.get("picture").toString())
                        .googleAccountId(userMap.get("sub").toString())
                        .build();
            }

            else{
                Map<String, Object> picture = (Map<String, Object>) userMap.get("picture");
                Map<String, Object> data = (Map<String, Object>) picture.get("data");
                String avatarUrl = data.get("url").toString();

                userEntity = UserEntity.builder()
                        .fullName(userMap.get("name").toString())
                        .email(userMap.get("email").toString())
                        .facebookAccountId(userMap.get("id").toString())
                        .avatarUrl(avatarUrl)
                        .build();
            }
            RoleEntity roleEntity = roleRepository.findByRole("USER");
            CartEntity cartEntity = CartEntity.builder()
                    .userEntity(userEntity)
                    .cartDetailEntities(new ArrayList<>()).build();
            userEntity.setRoleEntity(roleEntity);
            userEntity.setCartEntity(cartEntity);
            userRepository.save(userEntity);
        }
        else{
            userEntity = userRepository.findByEmail(userMap.get("email").toString()).get();
            if(userEntity.getGoogleAccountId() == null || userEntity.getGoogleAccountId().isEmpty()){
                userEntity.setGoogleAccountId(userMap.getOrDefault("sub", "").toString());
            }
            if(userEntity.getFacebookAccountId() == null || userEntity.getFacebookAccountId().isEmpty()){
                userEntity.setFacebookAccountId(userMap.getOrDefault("id", "").toString());
            }
        }
        String token = jwtTokenUtils.generateToken(userEntity, TokenType.ACCESS);
        TokenEntity tokenEntity = TokenEntity.builder()
                .userEntity(userEntity)
                .refreshToken(jwtTokenUtils.generateToken(userEntity,TokenType.REFRESH))
                .revoked(Boolean.FALSE).build();
        if(userEntity.getTokenEntities() == null || userEntity.getTokenEntities().isEmpty()){
            userEntity.setTokenEntities(new ArrayList<>(List.of(tokenEntity)));
        }
        else userEntity.getTokenEntities().add(tokenEntity);
        return TokenResponse.fromTokenEntity(tokenRepository.save(tokenEntity), token);
    }



    private String getToken(String requestBody, OAuth2Provider provider) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.set("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> tokenRequest = new HttpEntity<>(requestBody, tokenHeaders);
        String tokenUri = provider.equals(OAuth2Provider.GOOGLE) ?
                googleProperties.getTokenUri() : facebookProperties.getTokenUri();
        return restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                tokenRequest,
                Map.class
        ).getBody().get("access_token").toString();
    }
    private Map getUser(String requestBody, OAuth2Provider provider) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(requestBody, provider));
        HttpEntity<String> userRequest = new HttpEntity<>(requestBody, headers);
        String userUri = provider.equals(OAuth2Provider.GOOGLE) ?
                googleProperties.getUserInfoUri() : facebookProperties.getUserInfoUri();
        return restTemplate.exchange(
                userUri,
                HttpMethod.GET,
                userRequest,
                Map.class
        ).getBody();
    }


}


