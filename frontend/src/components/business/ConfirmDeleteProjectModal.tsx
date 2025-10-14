import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { deleteProject } from "../../api/projects.ts";
import { SmallModal } from "../ui/Modal.tsx";
import { Input } from "../ui/Input.tsx";
import { PrimaryButton } from "../ui/Button.tsx";
import { ErrorText, Text } from "../ui/Text.tsx";

type ConfirmDeleteProjectProps = {
    projectName: string;
    username: string;
    onClose: () => void;
};

export function ConfirmDeleteProjectModal({ projectName, username, onClose }: ConfirmDeleteProjectProps) {
    const [typedName, setTypedName] = useState<string>("");
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();

    const handleSubmit = (e: any) => {
        e.preventDefault();
        setError(null);

        deleteProject(username, projectName)
            .then(() => {
                nav("/projects")
            })
            .catch(err => setError(err.message));
    };

    return (
        <SmallModal content={
            (
                <div>
                    <Text content="Confirm the name of the project to delete." />
                    <form onSubmit={handleSubmit}>
                        <div className="mb-2">
                            <Input label="Project name" type="text" value={typedName} setValue={setTypedName} />
                        </div>
                        {error && <ErrorText className="mb-4" content={error} />}
                        <PrimaryButton disabled={typedName !== projectName} type="submit" text="Delete" />
                    </form>
                </div>
            )
        } onClose={onClose} />
    )
}
