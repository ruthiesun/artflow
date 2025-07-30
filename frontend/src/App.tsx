import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import {LoginPage} from "./pages/LoginPage.tsx";
import {HomePage} from "./pages/HomePage.tsx";
import {RegisterPage} from "./pages/RegisterPage.tsx";
import {ProjectPage} from "./pages/ProjectPage.tsx";
import {NewProjectPage} from "./pages/NewProjectPage.tsx";
import {EditProjectPage} from "./pages/EditProjectPage.tsx";
import {ErrorPage} from "./pages/ErrorPage.tsx";
import {Root} from "./pages/Root.tsx";
import {PublicRoute} from "./PublicRoute.tsx"
import {ProtectedRoute} from "./ProtectedRoute.tsx"

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
                <Route path="/register" element={<PublicRoute><RegisterPage /></PublicRoute>} />
                <Route path="/projects" element={<ProtectedRoute><HomePage /></ProtectedRoute>} />
                <Route path="/projects/new" element={<ProtectedRoute><NewProjectPage /></ProtectedRoute>} />
                <Route path="/projects/:projectName" element={<ProtectedRoute><ProjectPage /></ProtectedRoute>} />
                <Route path="/projects/:projectName/edit" element={<ProtectedRoute><EditProjectPage /></ProtectedRoute>} />
                <Route path="/error" element={<ErrorPage />} />
                <Route path="/*" element={<Root />} />
            </Routes>
        </Router>
    );
}

export default App;
