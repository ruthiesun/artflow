import axios from "axios";

export async function login(email: string, password: string) {
    return axios.post("http://localhost:8080/api/auth/login", {email: email, password: password});
}

export async function register(email: string, username: string, password: string) {
    return axios.post("http://localhost:8080/api/auth/register", {email: email, username: username, password: password});
}
