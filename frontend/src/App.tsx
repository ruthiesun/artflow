import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { LoginPage } from "./pages/LoginPage.tsx";
import { HomePage } from "./pages/HomePage.tsx";
import { RegisterPage } from "./pages/RegisterPage.tsx";
import { RegisterSuccessPage } from "./pages/RegisterSuccessPage.tsx";
import { VerifySuccessPage } from "./pages/VerifySuccessPage.tsx";
import { ProjectPage } from "./pages/ProjectPage.tsx";
import { NewProjectPage } from "./pages/NewProjectPage.tsx";
import { EditProjectPage } from "./pages/EditProjectPage.tsx";
import { ErrorPage } from "./pages/ErrorPage.tsx";
// import { Root } from "./pages/Root.tsx";
import { PublicRoute } from "./PublicRoute.tsx"
import { ProtectedRoute } from "./ProtectedRoute.tsx"
import { PrivateRoute } from "./PrivateRoute.tsx"
import { ResetPasswordRequestPage } from "./pages/ResetPasswordRequestPage.tsx";
import { ResetPasswordPage } from "./pages/ResetPasswordPage.tsx";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
                <Route path="/register" element={<PublicRoute><RegisterPage /></PublicRoute>} />
                <Route path="/reset" element={<PublicRoute><ResetPasswordPage /></PublicRoute>} />
                <Route path="/register-success" element={<PublicRoute><RegisterSuccessPage /></PublicRoute>} />
                <Route path="/reset-request" element={<PublicRoute><ResetPasswordRequestPage /></PublicRoute>} />
                <Route path="/verify" element={<PublicRoute><VerifySuccessPage /></PublicRoute>} />
                <Route path="/:username/projects" element={<ProtectedRoute><HomePage /></ProtectedRoute>} />
                <Route path="/:username/projects/new" element={<PrivateRoute><NewProjectPage /></PrivateRoute>} />
                <Route path="/:username/projects/:projectName" element={<ProtectedRoute><ProjectPage /></ProtectedRoute>} />
                <Route path="/:username/projects/:projectName/edit" element={<PrivateRoute><EditProjectPage /></PrivateRoute>} />
                <Route path="/error" element={<ErrorPage />} />
                {/* <Route path="/*" element={<Root />} /> */}
            </Routes>
        </Router>
    );
}

export default App;
