import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../AuthContext.tsx"
import type { Project } from "../types/project";
import type { ProjectTag } from "../types/tag";
import { getProject } from "../api/projects.ts";
import { getTagsForProject } from "../api/tags.ts";
import { navToErrorPage } from "./ErrorPage.tsx";
import { LoadingOverlay } from "../components/business/LoadingOverlay.tsx";
import { ConfirmDeleteProjectModal } from "../components/business/ConfirmDeleteProjectModal.tsx";
import { ImageCarousel } from "../components/business/ImageCarousel.tsx";
import { DisplayOnlyTagButton, DeleteButton, SecondaryButton } from "../components/ui/Button.tsx";
import { Background, BackgroundBorder, EdgePadding } from "../components/ui/Background.tsx";
import { H1, Text, TimestampText } from "../components/ui/Text.tsx";

export function ProjectPage() {
    const { username } = useParams<{ username: string }>();
    const { projectName } = useParams<{ projectName: string }>();
    const { getUsername } = useAuth();
    const [project, setProject] = useState<Project | null>(null);
    const [tags, setTags] = useState<ProjectTag[]>([]);
    const [showModal, setShowModal] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const nav = useNavigate();

    useEffect(() => {
        if (!projectName || !username) {
            return;
        }

        getProject(username, projectName)
            .then((retrievedProject) => {
                setProject(retrievedProject);
                setIsLoading(false);
            })
            .catch(err => {
                navToErrorPage({ nav, err });
            });

        getTagsForProject(username, projectName)
            .then((projectTags) => {
                setTags(projectTags);
            })
            .catch(err => {
                navToErrorPage({ nav, err });
            });

    }, [username, projectName]);

    function getPrettyTime(timeString: string): string {
        return (new Date(timeString)).toLocaleTimeString(undefined, {
            hour: '2-digit',
            minute: '2-digit',
            hour12: true,   // enables AM/PM
        });
    }

    const modButtonClassName = username === getUsername() ? "" : "hidden";
    if (!project || !username) {
        return;
    }

    return (
        <Background>
            <BackgroundBorder>
                <EdgePadding>
                    {!isLoading && <H1 content={project.projectName} />}
                    <div className={`mt-4 mb-2 flex flex-row ${modButtonClassName}`}>
                        <div className="mr-2">
                            <SecondaryButton type="button" text="Edit" disabled={isLoading} onClick={() => nav("edit")} />
                        </div>
                        <div className="mr-2">
                            <DeleteButton type="button" text="Delete project" disabled={isLoading} onClick={() => setShowModal(true)} />
                        </div>
                    </div>
                    <div className="flex flex-row">
                        {tags.map((tag: ProjectTag) => (
                            <div key={tag.tagName} className="mr-2">
                                <DisplayOnlyTagButton type="button" text={tag.tagName} />
                            </div>
                        ))}
                    </div>
                    {!isLoading && <div className="mt-4 mb-4">
                        <TimestampText content={`Created: ${project.createdDateTime.split("T")[0]} ${getPrettyTime(project.createdDateTime)}`} />
                        <TimestampText content={`Last updated: ${project.updatedDateTime.split("T")[0]} ${getPrettyTime(project.updatedDateTime)}`} />
                        <Text content={project.description} />
                    </div>}
                </EdgePadding>
                {!isLoading && <div className="flex justify-center items-center">
                    <div className="w-full">
                        <ImageCarousel projectName={project.projectName} username={username} />
                    </div>
                </div>}
                {showModal && (
                    <ConfirmDeleteProjectModal projectName={project.projectName} username={username} onClose={() => setShowModal(false)} />
                )}
                {isLoading && <LoadingOverlay />}

            </BackgroundBorder>
        </Background>
    );
}
