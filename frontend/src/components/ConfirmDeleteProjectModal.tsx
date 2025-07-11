import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {deleteProject} from "../api/projects.ts";

type ConfirmDeleteProjectProps = {
    projectName: string;
};

export function ConfirmDeleteProjectModal({ projectName }: ConfirmDeleteProjectProps) {
    const [typedName, setTypedName] = useState<string>("")
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate()

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (typedName !== projectName) {
            setError("Entered name does not match project name");
        }

        deleteProject(projectName)
            .then(() => {
                nav("/projects")
            })
            .catch((err) => {
                setError(err)
            });
    };

    return (
        <form onSubmit={handleSubmit}>
            <p>Confirm that you want to delete {projectName} by typing in the project name below:</p>
            {error && <p>{error}</p>}
            <input
                type="text"
                value={typedName}
                onChange={e => setTypedName(e.target.value)}
            />
            <button type='submit'>
                Delete
            </button>
        </form>
    )
}
