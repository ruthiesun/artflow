
import { initializeApp, getApps, getApp, type FirebaseApp } from "firebase/app";
import { getAuth, type Auth } from "firebase/auth";

const firebaseConfig = {
    apiKey: "AIzaSyD1EZ_I48qbm30XqR2bYBCAlXgvScIZyAM",
    authDomain: "sept2025-939c7.firebaseapp.com",
    projectId: "sept2025-939c7",
    storageBucket: "sept2025-939c7.firebasestorage.app",
    messagingSenderId: "83875200033",
    appId: "1:83875200033:web:7abfe6c30ab10f72767760",
    measurementId: "G-6SYJZREJ0F"
};

// Initialize Firebase only once
const app : FirebaseApp = !getApps().length ? initializeApp(firebaseConfig) : getApp();

// Export the initialized auth instance
export const auth : Auth = getAuth(app);