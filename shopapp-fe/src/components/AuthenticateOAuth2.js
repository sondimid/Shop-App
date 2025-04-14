import React from "react";
import { useEffect } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import axiosInstance from "../utils/RefreshToken";

function AuthenticateOAuth2() {
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");
    const provider = urlParams.get("state");

    console.log("Authorization code:", code);
    console.log(urlParams.get("state"));
    if (code) {
      axiosInstance
        .get(`/users/oauth2/${provider}`, {
          params: { code },
        })
        .then(async (response) => {
          Cookies.set("accessToken", response.data.accessToken);
          Cookies.set("refreshToken", response.data.refreshToken);
          const accessToken = response.data.accessToken;
          if (accessToken) {
            try {
              const response = await axiosInstance.get(
                "users/profiles",
                {
                  headers: {
                    Authorization: `Bearer ${accessToken}`,
                  },
                }
              );
              Cookies.set("user", JSON.stringify(response.data));
            } catch (error) {
              window.location.href = "/login";
            }
            window.location.href = "/";
          }
        })
        .catch((error) => {
          window.location.href = "/login";
        });
    }
  }, []);
}

export default AuthenticateOAuth2;
