import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createProject } from "../api/projects.ts";
import { navToErrorPage } from "./ErrorPage.tsx";
import { ProjectNameInput } from "../components/business/ProjectNameInput.tsx";
import { ProjectDescriptionInput } from "../components/business/ProjectDescriptionInput.tsx";
import { ProjectVisibilityRadio } from "../components/business/ProjectVisibilityRadio.tsx";
import { ProjectTagInput } from "../components/business/ProjectTagInput.tsx";
import { Background, BackgroundBorder, EdgePadding } from "../components/ui/Background.tsx";
import { ErrorText, H1 } from "../components/ui/Text.tsx";
import { PrimaryButton } from "../components/ui/Button.tsx";
import { Validator } from "../Validator.ts";


export function NewProjectPage() {
    const { username } = useParams<{ username: string }>()
    const [name, setName] = useState<string>("")
    const [description, setDescription] = useState<string>("")
    const [visibility, setVisibility] = useState<"public" | "private">("private");
    const [tags, setTags] = useState<string[]>([])
    const [error, setError] = useState<string | null>(null);
    const [validator, setValidator] = useState<Validator>();
    const nav = useNavigate();

    useEffect(() => {
        Validator.getInstance()
            .then((res) => {
                setValidator(res);
            })
            .catch((err) => {
                navToErrorPage({ nav, err });
            });
    }, []);

    const handleSubmit = (e: any) => {
        e.preventDefault();
        setError(null);

        if (!username) {
            return;
        }

        if (!name || !visibility) {
            setError("Please fill in all mandatory fields.");
        }

        if (validator === undefined) {
            return;
        }

        const trimmedName = name.trim();
        setName(trimmedName);

        if (!(new RegExp(validator.getProjectNameRegex()).test(trimmedName))) {
            setError(validator.getProjectNameMessage());
            return;
        }

        if (!(new RegExp(validator.getProjectDescriptionRegex()).test(description))) {
            setError(validator.getProjectDescriptionMessage());
            return;
        }

        for (let i: number = 0; i < tags.length; i++) {
            const tag: string = tags[i];
            if (!(new RegExp(validator.getTagRegex()).test(tag))) {
                setError(validator.getTagMessage());
                return;
            }
        }

        createProject(username, trimmedName, description, visibility, tags)
            .then((createdProject) => {
                nav("/" + username + "/projects/" + createdProject.projectName, { replace: true })
            })
            .catch(err => {
                navToErrorPage({ nav, err });
            });
    }

    return (
        <Background>
            <BackgroundBorder>
                <EdgePadding>
                    <H1 content="New Project" />
                    <form onSubmit={handleSubmit}>
                        <div className="mb-2">
                            <ProjectNameInput name={name} setName={setName} />
                        </div>
                        <div className="mb-2">
                            <ProjectDescriptionInput description={description} setDescription={setDescription} />
                        </div>
                        <div className="mb-2">
                            <ProjectVisibilityRadio visibility={visibility} setVisibility={setVisibility} />
                        </div>
                        <div className="mb-2">
                            <ProjectTagInput tags={tags} setTags={setTags} />
                        </div>
                        {error && <ErrorText className="mb-4" content={error} />}
                        <PrimaryButton type="submit" text="Save" disabled={(name.trim() === "" || visibility.trim() === "") && validator !== undefined} />
                    </form>
                </EdgePadding>
            </BackgroundBorder>
        </Background>
    )
}
