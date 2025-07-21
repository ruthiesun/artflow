import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {deleteProject} from "../api/projects.ts";
import {Modal} from "./Modal.tsx";

type ConfirmDeleteProjectProps = {
    projectName: string;
    onClose: () => void;
};

export function ConfirmDeleteProjectModal({ projectName, onClose }: ConfirmDeleteProjectProps) {
    const [typedName, setTypedName] = useState<string>("")
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate()

    const handleSubmit = (e) => {
        e.preventDefault();
        setError(null);

        if (typedName !== projectName) {
            setError("Entered name does not match project name");
            return;
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
        <Modal content={
            (
                <form onSubmit={handleSubmit}>
                    <p>Confirm that you want to delete {projectName} by typing in the project name below:</p>
                    {error && <p>{error}</p>}
                    <input
                        type="text"
                        value={typedName}
                        onChange={e => setTypedName(e.target.value)}
                        className="invalid:border-pink-500 invalid:text-pink-600 focus:border-sky-500 focus:outline focus:outline-sky-500 focus:invalid:border-pink-500 focus:invalid:outline-pink-500 disabled:border-gray-200 disabled:bg-gray-50 disabled:text-gray-500 disabled:shadow-none dark:disabled:border-gray-700 dark:disabled:bg-gray-800/20 ..."
                    />
                    <button type='submit'>
                        Delete
                    </button>
                </form>
            )
        } onClose={onClose}/>
    )
}
