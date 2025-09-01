import {useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {createProject} from "../api/projects.ts";
import {navToErrorPage} from "./ErrorPage.tsx";
import {ProjectNameInput} from "../components/business/ProjectNameInput.tsx";
import {ProjectDescriptionInput} from "../components/business/ProjectDescriptionInput.tsx";
import {ProjectVisibilityRadio} from "../components/business/ProjectVisibilityRadio.tsx";
import {ProjectTagInput} from "../components/business/ProjectTagInput.tsx";
import {Background, BackgroundBorder, EdgePadding} from "../components/ui/Background.tsx";
import {H1} from "../components/ui/Text.tsx";
import {PrimaryButton} from "../components/ui/Button.tsx";


export function NewProjectPage() {
    const {username} = useParams<{ username: string }>()
    const [name, setName] = useState<string>("")
    const [description, setDescription] = useState<string>("")
    const [visibility, setVisibility] =  useState<"public" | "private">("private");
    const [tags, setTags] = useState<string[]>([])
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (!username) {
            return;
        }

        if (!name || !visibility) {
            setError("Please fill in all fields.");
        }

        createProject(username, name, description, visibility, tags)
            .then((createdProject) => {
                nav("/" + username + "/projects/" + createdProject.projectName)
            })
            .catch(err => {
                // todo handle unavailable names without nav
                navToErrorPage(nav, err);
            });
    }

    return (
        <Background>
            <BackgroundBorder>
                <EdgePadding>
                    <H1 content="New Project" />
                    <form onSubmit={handleSubmit}>
                        <div className="mb-2">
                            <ProjectNameInput name={name} setName={setName}/>
                        </div>
                        <div className="mb-2">
                            <ProjectDescriptionInput description={description} setDescription={setDescription}/>
                        </div>
                        <div className="mb-2">
                            <ProjectVisibilityRadio visibility={visibility} setVisibility={setVisibility}/>
                        </div>
                        <div className="mb-2">
                            <ProjectTagInput tags={tags} setTags={setTags}/>
                        </div>
                        
                        <PrimaryButton type="submit" text="Save" disabled={name.trim() === "" | visibility.trim() === ""} />
                    </form>
                </EdgePadding>
            </BackgroundBorder>
        </Background>
    )
}
