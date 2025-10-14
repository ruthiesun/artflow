import axios from "axios";
import { auth } from "./auth/firebase";

export const base: string = "https://artflow-761n.onrender.com/api";

const api = axios.create({
    baseURL: `${base}`,
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
}, (error: any) => {
    // Handle request errors here
    return Promise.reject(error);
});

export default api;

