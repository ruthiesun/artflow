import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {createProject} from "../api/projects.ts";
import {navToErrorPage} from "./ErrorPage.tsx";
import {ProjectNameInput} from "../components/business/ProjectNameInput.tsx";
import {ProjectDescriptionInput} from "../components/business/ProjectDescriptionInput.tsx";
import {ProjectVisibilityRadio} from "../components/business/ProjectVisibilityRadio.tsx";
import {ProjectTagInput} from "../components/business/ProjectTagInput.tsx";
import {Background, BackgroundBorder} from "../components/ui/Background.tsx";
import {H1} from "../components/ui/Text.tsx";
import {PrimaryButton} from "../components/ui/Button.tsx";


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
                        <PrimaryButton type="submit" text="Save" disabled={name.trim() === "" | visibility.trim() === ""} />
                    </form>
                </div>
            } />
        } />
    )
}
