import {useEffect, useState} from "react";
import type {Project} from "../types/project";
import {getProject} from "../api/projects.ts";
import {useNavigate, useParams} from "react-router-dom";
import type {ProjectTag} from "../types/tag";
import {getTagsForProject} from "../api/tags.ts";
import {ConfirmDeleteProjectModal} from "../components/ConfirmDeleteProjectModal.tsx";
import {ImageCarousel} from "../components/ImageCarousel.tsx";
import {DisplayOnlyTagButton, DeleteButton, NavButton, DeselectedTagButton, SelectedTagButton} from "../components/Button.tsx";
import {Background, BackgroundBorder} from "../components/Background.tsx";
import {H1, H3, Text, TimestampText} from "../components/Text.tsx";

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
                navToErrorPage(nav, err);
            });

        getTagsForProject(projectName)
            .then((projectTags) => {
                setTags(projectTags);
            })
            .catch(err => {
                navToErrorPage(nav, err);
            });

    }, [projectName]);

    if (!projectName || !project) return <div>Loading...</div>;

    return (
        <Background className="px-10 py-5" content={
            <BackgroundBorder content={
                <div>
                    <H1 content={project.projectName} />
                    <NavButton type="button" text="Edit" onClick={() => nav("edit")} />
                    <DeleteButton type="button" text="Delete project" onClick={() => setShowModal(true)} />
                    <div>
                        {tags.map((tag: ProjectTag) => (
                            <DisplayOnlyTagButton key={tag.tagName} type="button" text={tag.tagName} />
                        ))}
                    </div>
                    <div className="mt-5 mb-5">
                        <TimestampText content={`Created: ${project.createdDateTime}`} />
                        <TimestampText content={`Last updated: ${project.updatedDateTime}`} />
                        <Text content={project.description} />
                    </div>
                    <div className="flex justify-center items-center rounded-lg bg-white">
                    <div className="max-w-full w-max p-4 sm:p-6 md:p-8 lg:p-10">
                        <ImageCarousel projectName={project.projectName} />
                    </div>
                    </div>
                    {showModal && (
                        <ConfirmDeleteProjectModal projectName={project.projectName} onClose={() => setShowModal(false)}/>
                    )}
                </div>
            } />
        } />
    );
}
