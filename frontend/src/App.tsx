import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import {LoginPage} from "./pages/LoginPage.tsx";
import {HomePage} from "./pages/HomePage.tsx";
import {RegisterPage} from "./pages/RegisterPage.tsx";
import {ProjectPage} from "./pages/ProjectPage.tsx";
import {NewProjectPage} from "./pages/NewProjectPage.tsx";
import {EditProjectPage} from "./pages/EditProjectPage.tsx";
import {ErrorPage} from "./pages/ErrorPage.tsx";

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/projects" element={<HomePage />} />
                <Route path="/projects/new" element={<NewProjectPage />} />
                <Route path="/projects/:projectName" element={<ProjectPage />} />
                <Route path="/projects/:projectName/edit" element={<EditProjectPage />} />
                <Route path="/error" element={<ErrorPage />} />
            </Routes>
        </Router>
    );
}

export default App;
