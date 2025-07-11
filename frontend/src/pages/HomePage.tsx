import {useEffect, useState} from "react"

import type {Project} from "../types/project"
import {getAllProjectsWithTags} from "../api/projects.ts";
import {ImageCarouselPreview} from "../components/ImageCarousel.tsx";
import {Link} from "react-router-dom";
import {getTagsForUser} from "../api/tags.ts";
import type {Tag} from "../types/tag";
import {useNavigate} from "react-router-dom";

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
        <div>
            <h1>My Projects</h1>
            <div>
                <button
                    type={"button"}
                    onClick={() => nav("new")}
                >
                    New project
                </button>
            </div>
            <div>
                {selectedTags.map((tag: Tag) => (
                    <div key={tag.tagName}>
                        <button
                            type="button"
                            className="bg-blue-500"
                            onClick={() => deselectTag(tag)}
                        >
                            {tag.tagName}
                        </button>
                    </div>
                ))}
                {deselectedTags.map((tag: Tag) => (
                    <div key={tag.tagName}>
                        <button
                            type="button"
                            onClick={() => selectTag(tag)}
                        >
                            {tag.tagName}
                        </button>
                    </div>
                ))}
            </div>
            <div>
                {projects.map((project: Project) => (
                    <div key={project.projectName}>
                        <h2>
                            <Link to={project.projectName}>
                                {project.projectName}
                            </Link>
                        </h2>
                        <p>{project.description}</p>
                        <ImageCarouselPreview projectName={project.projectName} />
                    </div>
                ))}
            </div>
        </div>
    )
}
