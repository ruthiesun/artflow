import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {createProject} from "../api/projects.ts";
import {ProjectNameInput} from "../components/ProjectNameInput.tsx";
import {ProjectDescriptionInput} from "../components/ProjectDescriptionInput.tsx";
import {ProjectVisibilityRadio} from "../components/ProjectVisibilityRadio.tsx";
import {ProjectTagInput} from "../components/ProjectTagInput.tsx";
import {Background, BackgroundBorder} from "../components/Background.tsx";
import {H1} from "../components/Text.tsx";
import {SubmitButton} from "../components/Button.tsx";
import {navToErrorPage} from "./ErrorPage.tsx";

export function NewProjectPage() {
    const [name, setName] = useState<string>("")
    const [description, setDescription] = useState<string>("")
    const [visibility, setVisibility] =  useState<"public" | "private">("private");
    const [tags, setTags] = useState<string[]>([])
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (!name || !visibility) {
            setError("Please fill in all fields.");
        }

        createProject(name, description, visibility, tags)
            .then((createdProject) => {
                nav("/projects/" + createdProject.projectName)
            })
            .catch(err => {
                // todo handle unavailable names without nav
                navToErrorPage(nav, err);
            });
    }

    return (
        <Background className="px-10 py-5" content={
            <BackgroundBorder content={
                <div>
                    <H1 content="New Project" />
                    <form onSubmit={handleSubmit}>
                        <ProjectNameInput name={name} setName={setName}/>
                        <ProjectDescriptionInput description={description} setDescription={setDescription}/>
                        <ProjectVisibilityRadio visibility={visibility} setVisibility={setVisibility}/>
                        <ProjectTagInput tags={tags} setTags={setTags}/>
                        <SubmitButton type="submit" text="Save" disabled={name.trim() === "" | visibility.trim() === ""} />
                    </form>
                </div>
            } />
        } />
    )
}
