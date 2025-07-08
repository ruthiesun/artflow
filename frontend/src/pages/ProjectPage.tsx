import {useEffect, useState} from "react";
import type {Project} from "../types/project";
import {getProject} from "../api/projects.ts";
import {useNavigate, useParams} from "react-router-dom";
import type {ProjectTag} from "../types/tag";
import {getAllTagsForProject} from "../api/tags.ts";
import {ConfirmDeleteProjectModal} from "../components/ConfirmDeleteProjectModal.tsx";
import {ImageCarousel, ImageCarouselPreview} from "../components/ImageCarousel.tsx";

export function ProjectPage() {
    const [project, setProject] = useState<Project>(null)
    const { projectName } = useParams<{ projectName: string }>()
    const [error, setError] = useState<string | null>(null)
    const [tags, setTags] = useState<ProjectTag[]>([])
    const [loading, setLoading] = useState(true);
    const nav = useNavigate();
    const [showModal, setShowModal] = useState(false);

    if (projectName == null) {
        setError("Null project name")
        return <div>{error}</div>;
    }

    useEffect(() => {
        getProject(projectName)
            .then((retrievedProject) => {
                setProject(retrievedProject)
                setLoading(false)
            });}, []);



    useEffect(() => {
        getAllTagsForProject(projectName)
            .then((retrievedTags) => {
                setTags(retrievedTags)
            });}, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;
    if (!project) return <div>No project found</div>;

    return (
        <div>
            <h1>{project.projectName}</h1>
            <button
                type="button"
                onClick={() => nav('edit')}
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
                {tags.map((tag) => (
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
                <ConfirmDeleteProjectModal projectName={project.projectName} />
            )}
        </div>
    )
}
