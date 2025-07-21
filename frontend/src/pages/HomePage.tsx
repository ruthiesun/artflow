import {useEffect, useState} from "react"

import type {Project} from "../types/project"
import {getAllProjectsWithTags} from "../api/projects.ts";
import {ImageCarouselPreview} from "../components/ImageCarousel.tsx";
import {getTagsForUser} from "../api/tags.ts";
import type {Tag} from "../types/tag";
import {useNavigate} from "react-router-dom";
import {NavButton, DeselectedTagButton, SelectedTagButton} from "../components/Button.tsx";
import {Background, BackgroundBorder} from "../components/Background.tsx";
import {H1, H3, Text} from "../components/Text.tsx";

export function HomePage() {
    const [projects, setProjects] = useState<Project[]>([])
    const [deselectedTags, setDeselectedTags] = useState<Tag[]>([])
    const [selectedTags, setSelectedTags] = useState<Tag[]>([])
    const [error, setError] = useState<string | null>(null);
    const nav = useNavigate()

    useEffect(() => {
        getTagsForUser()
            .then((allTags) => {
                setDeselectedTags(allTags)
            })
            .catch(err => {
                setError(err.message)
            })
        }, []);

    useEffect(() => {
        getAllProjectsWithTags(selectedTags)
            .then((allProjects) => {
                setProjects(allProjects)
            })
            .catch(err => {
                setError(err.message)
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

    if (error) return <div>{error}</div>;

    return (
        <Background className="px-10 py-5" content={
            <BackgroundBorder content={
                <div>
                    <H1 content="My Projects" />
                    <div>
                        <NavButton type="button" text="New project" onClick={() => nav("new")} />
                    </div>
                    <div>
                        <div className="flex flex-wrap">
                            {selectedTags.map((tag: Tag) => (
                                <div key={tag.tagName}>
                                    <SelectedTagButton key={tag.tagName} type="button" text={tag.tagName} onClick={() => deselectTag(tag)} />
                                </div>
                            ))}
                            {deselectedTags.map((tag: Tag) => (
                                <div key={tag.tagName}>
                                    <DeselectedTagButton key={tag.tagName} type="button" text={tag.tagName} onClick={() => selectTag(tag)} />
                                </div>
                            ))}
                        </div>
                    </div>
                    <div className="mt-4">
                        {projects.map((project: Project) => (
                            <div key={project.projectName} className="rounded-xl p-2 bg-project-preview-bg mb-4 group cursor-pointer border border-project-preview-border" onClick={() => nav(project.projectName)}>
                                <H3 className="text-project-name group-hover:opacity-50 transition-opacity" content={project.projectName} />
                                <Text className="mb-2 group-hover:opacity-50 transition-opacity" content={project.description} />
                                <div className="rounded-lg bg-white group-hover:opacity-50 transition-opacity">
                                    <ImageCarouselPreview projectName={project.projectName} />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            } />
        } />
    );
}
