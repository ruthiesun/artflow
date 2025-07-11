import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {createProject} from "../api/projects.ts";
import {ProjectNameInput} from "../components/ProjectNameInput.tsx";
import {ProjectDescriptionInput} from "../components/ProjectDescriptionInput.tsx";
import {ProjectVisibilityRadio} from "../components/ProjectVisibilityRadio.tsx";
import {ProjectTagInput} from "../components/ProjectTagInput.tsx";

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
                setError(err.message)
            });
    }

    if (error) return <div>{error}</div>;

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <ProjectNameInput name={name} setName={setName}/>
                <ProjectDescriptionInput description={description} setDescription={setDescription}/>
                <ProjectVisibilityRadio visibility={visibility} setVisibility={setVisibility}/>
                <ProjectTagInput tags={tags} setTags={setTags}/>
                <button type="submit">
                    Save
                </button>
            </form>
        </div>
    )
}
