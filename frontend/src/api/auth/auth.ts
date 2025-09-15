import axios from "axios";
import { auth } from "./firebase";
import { signInWithCustomToken } from "firebase/auth";


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

export async function register(email: string, username: string, password: string) {
    return axios.post("http://localhost:8080/api/auth/register", {email: email, username: username, password: password});
}

export async function verify(token: string) {
    return axios.get(`http://localhost:8080/api/auth/verify?token=${token}`);
}
