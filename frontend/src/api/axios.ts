import axios from "axios";
import { auth } from "./auth/firebase";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json"
    },
})

// Automatically attach token to every request
api.interceptors.request.use(async (config) => {
  const currentUser = auth.currentUser;

  if (currentUser) {
    try {
      const idToken = await currentUser.getIdToken();
      config.headers.Authorization = `Bearer ${idToken}`;
    } catch (error) {
      console.error("Error getting Firebase ID token for Axios interceptor:", error);
      auth.updateCurrentUser(null);
    }
  }

  return config;
}, (error) => {
  // Handle request errors here
  return Promise.reject(error);
});

export default api;

