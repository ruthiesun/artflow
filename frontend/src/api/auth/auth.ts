import axios from "axios";
import { auth } from "./firebase";
import { signInWithCustomToken, signOut } from "firebase/auth";
import api from "../axios";


export async function login(email: string, password: string) {
    let username : string;

    return axios.post("http://localhost:8080/api/auth/login", {email: email, password: password})
        .then((firebaseTokenAndUsername) => {
            username = firebaseTokenAndUsername.data.username;
            return signInWithCustomToken(auth, firebaseTokenAndUsername.data.token);
        }).then((userCredential) => {
            // Signed in successfully.
            const user = userCredential.user;

            // Now, from this 'user' object, you can get the ID token
            return user.getIdToken(/* forceRefresh */ true);
        }).then((idToken) => {
            return {username, idToken};
        });
}

export async function logout() {
    const client : Promise<void> = signOut(auth);
    const server : Promise<void> = api.post("http://localhost:8080/api/auth/logout", {});

    return Promise.all([client, server]);
}

export async function register(email: string, username: string, password: string) {
    return axios.post("http://localhost:8080/api/auth/register", {email: email, username: username, password: password});
}

export async function verify(token: string) {
    return axios.get(`http://localhost:8080/api/auth/verify?token=${token}`);
}

export async function requestReset(email: string) {
    return axios.post("http://localhost:8080/api/auth/resetRequest", {email: email});
}

export async function reset(password: string, token: string) {
    return axios.post(`http://localhost:8080/api/auth/reset?token=${token}`, {password: password});
}
