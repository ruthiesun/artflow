import {useEffect, useState} from "react";
import type {Project} from "../types/project";
import {getProject} from "../api/projects.ts";
import {useNavigate, useParams} from "react-router-dom";
import type {ProjectTag} from "../types/tag";
import {getTagsForProject} from "../api/tags.ts";
import {ConfirmDeleteProjectModal} from "../components/ConfirmDeleteProjectModal.tsx";
import {ImageCarousel} from "../components/ImageCarousel.tsx";

export function ProjectPage() {
    const {projectName} = useParams<{ projectName: string }>();
    const [project, setProject] = useState<Project>(null);
    const [tags, setTags] = useState<ProjectTag[]>([]);
    const [showModal, setShowModal] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate();

    useEffect(() => {
        if (!projectName) {
            setError("Null project name");
            return;
        }

        getProject(projectName)
            .then((retrievedProject) => {
                setProject(retrievedProject);
            })
            .catch(err => {
                setError(err.message);
            });

        getTagsForProject(projectName)
            .then((projectTags) => {
                setTags(projectTags);
            })
            .catch(err => {
                setError(err.message);
            });

    }, [projectName]);

    if (error) return <div>{error}</div>;
    if (!projectName || !project) return <div>Loading...</div>;

    return (
        <div>
            <h1 className="text-header">{project.projectName}</h1>
            <button
                type="button"
                onClick={() => nav("edit")}
                >
                Edit
            </button>
            <button
                type="button"
                onClick={() => setShowModal(true)}
            >
                Delete project
            </button>
            <div>
                {tags.map((tag: ProjectTag) => (
                    <p key={tag.tagName}>{tag.tagName}</p>
                ))}
            </div>
            <div>
                <p>Created {project.createdDateTime}</p>
                <p>Last updated {project.updatedDateTime}</p>
                <p>{project.description}</p>
            </div>
            <ImageCarousel projectName={project.projectName} />
            {showModal && (
                <ConfirmDeleteProjectModal projectName={project.projectName} onClose={() => setShowModal(false)}/>
            )}
        </div>
    )
}
