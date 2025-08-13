import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import type {Project} from "../types/project";
import type {ProjectTag} from "../types/tag";
import {getProject} from "../api/projects.ts";
import {getTagsForProject} from "../api/tags.ts";
import {navToErrorPage} from "./ErrorPage.tsx";
import {LoadingOverlay} from "../components/business/LoadingOverlay.tsx";
import {ConfirmDeleteProjectModal} from "../components/business/ConfirmDeleteProjectModal.tsx";
import {ImageCarousel} from "../components/business/ImageCarousel.tsx";
import {DisplayOnlyTagButton, DeleteButton, SecondaryButton, DeselectedTagButton, SelectedTagButton} from "../components/ui/Button.tsx";
import {Background, BackgroundBorder} from "../components/ui/Background.tsx";
import {H1, H3, Text, TimestampText} from "../components/ui/Text.tsx";

export function ProjectPage() {
    const {projectName} = useParams<{ projectName: string }>();
    const [project, setProject] = useState<Project>(null);
    const [tags, setTags] = useState<ProjectTag[]>([]);
    const [showModal, setShowModal] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
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
                setIsLoading(false);
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

    return (
        <Background className="px-10 py-5" content={
            <BackgroundBorder content={
                <div>
                    {!isLoading && <H1 content={project.projectName} />}
                    <SecondaryButton type="button" text="Edit" disabled={isLoading} onClick={() => nav("edit")} />
                    <DeleteButton type="button" text="Delete project" disabled={isLoading} onClick={() => setShowModal(true)} />
                    <div>
                        {tags.map((tag: ProjectTag) => (
                            <DisplayOnlyTagButton key={tag.tagName} type="button" text={tag.tagName} />
                        ))}
                    </div>
                    {!isLoading && <div className="mt-5 mb-5">
                        <TimestampText content={`Created: ${project.createdDateTime}`} />
                        <TimestampText content={`Last updated: ${project.updatedDateTime}`} />
                        <Text content={project.description} />
                    </div>}
                    {!isLoading && <div className="flex justify-center items-center">
                        <div className="w-full p-4 sm:p-6 md:p-8 lg:p-10">
                            <ImageCarousel projectName={project.projectName} />
                        </div>
                    </div>}
                    {showModal && (
                        <ConfirmDeleteProjectModal projectName={project.projectName} onClose={() => setShowModal(false)}/>
                    )}
                    {isLoading && <LoadingOverlay/>}
                </div>
            } />
        } />
    );
}
