import {useEffect, useState} from "react";
import {Link, useNavigate, useParams} from "react-router-dom";
import {createProject, getProject} from "../api/projects.ts";
import {ProjectNameInput} from "../components/ProjectNameInput.tsx";
import {ProjectDescriptionInput} from "../components/ProjectDescriptionInput.tsx";
import {ProjectVisibilityRadio} from "../components/ProjectVisibilityRadio.tsx";
import {ProjectTagInput} from "../components/ProjectTagInput.tsx";
import {ImageEditor} from "../components/ImageEditor.tsx";
import type {ProjectImage} from "../types/image";

export function NewProjectPage() {
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();
    const [name, setName] = useState<string>("")
    const [description, setDescription] = useState<string>("")
    const [visibility, setVisibility] =  useState<'public' | 'private'>('private');
    const [tags, setTags] = useState<string[]>([])

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (!name || !visibility) {
            setError("Please fill in all fields.");
            return;
        }

        console.log("clicked submit")
        createProject(name, description, visibility, tags)
                .then((createdProject) => {
                    nav("/projects/" + createdProject.projectName)
                }).catch((err) => {
            console.log(err)
        });
    };

    return (
        <div>
                <form onSubmit={handleSubmit}>
                    <ProjectNameInput name={name} setName={setName} />
                    <ProjectDescriptionInput description={description} setDescription={setDescription} />
                    <ProjectVisibilityRadio visibility={visibility} setVisibility={setVisibility} />
                    <ProjectTagInput tags={tags} setTags={setTags} />
                    <button type="submit">
                        Save
                    </button>
                </form>
        </div>
    )
}
