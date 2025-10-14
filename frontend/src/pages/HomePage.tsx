import { useEffect, useState } from "react"
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../AuthContext.tsx"
import type { Project } from "../types/project"
import type { Tag } from "../types/tag";
import { getAllProjectsWithTags } from "../api/projects.ts";
import { getTagsForUser } from "../api/tags.ts";
import { navToErrorPage } from "./ErrorPage.tsx";
import { ImageCarouselPreview } from "../components/business/ImageCarousel.tsx";
import { SecondaryButton, DeselectedTagButton, SelectedTagButton } from "../components/ui/Button.tsx";
import { Background, BackgroundBorder, EdgePadding } from "../components/ui/Background.tsx";
import { H3, Text } from "../components/ui/Text.tsx";

export function HomePage() {
    const { username } = useParams<{ username: string }>()
    const { getUsername } = useAuth();
    const [projects, setProjects] = useState<Project[]>([])
    const [deselectedTags, setDeselectedTags] = useState<Tag[]>([])
    const [selectedTags, setSelectedTags] = useState<Tag[]>([])
    // const [error, setError] = useState<string | null>(null);
    const nav = useNavigate()

    useEffect(() => {
        if (!username) {
            return;
        }

        getTagsForUser(username)
            .then((allTags) => {
                setDeselectedTags(allTags)
            })
            .catch(err => {
                navToErrorPage({ nav, err });
            })
    }, [username]);

    useEffect(() => {
        if (!username) {
            return;
        }

        getAllProjectsWithTags(username, selectedTags)
            .then((allProjects) => {
                setProjects(allProjects)
            })
            .catch(err => {
                navToErrorPage({ nav, err });
            })
    }, [selectedTags, deselectedTags]);

    const selectTag = (selectedTag: Tag) => {
        setSelectedTags(prev => [...prev, selectedTag]);
        setDeselectedTags(prev => prev.filter(tag => tag.tagName !== selectedTag.tagName));
    };

    const deselectTag = (deselectedTag: Tag) => {
        setDeselectedTags(prev => [...prev, deselectedTag]);
        setSelectedTags(prev => prev.filter(tag => tag.tagName !== deselectedTag.tagName));
    };

    const modButtonClassName = username === getUsername() ? "" : "hidden";
    if (!username) {
        return;
    }

    return (
        <Background>
            <BackgroundBorder>
                <EdgePadding>
                    <div className={`mb-2 ${modButtonClassName}`}>
                        <SecondaryButton type="button" text="New project" onClick={() => nav("new")} />
                    </div>
                    <div>
                        <div className="flex flex-wrap">
                            {selectedTags.map((tag: Tag) => (
                                <div key={tag.tagName} className="mr-2">
                                    <SelectedTagButton type="button" text={tag.tagName} onClick={() => deselectTag(tag)} />
                                </div>
                            ))}
                            {deselectedTags.map((tag: Tag) => (
                                <div key={tag.tagName} className="mr-2">
                                    <DeselectedTagButton type="button" text={tag.tagName} onClick={() => selectTag(tag)} />
                                </div>
                            ))}
                        </div>
                    </div>
                    <div className="mt-4">
                        {projects.map((project: Project) => (
                            <div key={project.projectName} className="rounded-xl p-2 bg-white mb-4 group cursor-pointer" onClick={() => nav(project.projectName)}>
                                <H3 className="group-hover:opacity-50 transition-opacity" content={project.projectName} />
                                <Text className="mb-2 group-hover:opacity-50 transition-opacity" content={project.description} />
                                <div className="rounded-lg bg-surface group-hover:opacity-50 transition-opacity">
                                    <ImageCarouselPreview projectName={project.projectName} username={username} />
                                </div>
                            </div>
                        ))}
                    </div>
                </EdgePadding>
            </BackgroundBorder>
        </Background>
    );
}
